package eu.ops.plugin.conceptwiki;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParser.DatatypeHandling;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.turtle.TurtleParser;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.plugin.Plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input: SPARQL query, containing at least one pattern {?x [HAS_SIMILAR|HAS_SUBSTRUCTURE_MATCH|HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER|HAS_EXACT_STRUCTURE_MATCH] ?y}, where ?y is a bound literal representing a molecule
 * Output: Reference to graph with triples in the form: {?x [HAS_SIMILAR|HAS_SUBSTRUCTURE_MATCH|HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER|HAS_EXACT_STRUCTURE_MATCH] ?y}, where ?y is a bound URI in the form (http://inchi.chemspider.com/Chemical-Structure."+csid+".html), where csid is retrieved from chemspider
 * 
 * Given a Set of statementpatterns, invokes the chemspider service and retrieves similar molecules
 */
public class ConceptWiki extends Plugin
{
	private static final String CW_QUERY = "http://wiki.openphacts.org/index.php/ext_function#conceptwiki_";
	private static final String CW_PREFIX = "http://www.conceptwiki.org/wiki/concept/";
	private static final String CW_SEARCH = "search";
	private static final String CW_GET_CONCEPT = "get_concept";
	private static final String CW_SEARCH_URL = "search_url";
	private static final String CW_SEARCH_BY_TAG = "search_by_tag";
	private static final String CW_TAG_SPEC = "semantic_type";
	public static final URI FIXEDCONTEXT=new URIImpl("http://larkc.eu#Fixedcontext");
	
	private Set<String> uuidCache = new HashSet<String>();
	private URI outputGraphName;
	
	private static Logger logger = LoggerFactory.getLogger(Plugin.class);

	/**
	 * Constructor.
	 * 
	 * @param pluginUri 
	 * 		a URI representing the plug-in type, e.g. 
	 * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public ConceptWiki(URI pluginUri) {
		super(pluginUri);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Called on plug-in initialisation. The plug-in instances are initialised on
	 * workflow initialisation.
	 * 
	 * @param workflowDescription 
	 * 		set of statements containing plug-in specific 
	 * 		information which might be needed for initialization (e.g. plug-in parameters).
	 */
	@Override
	protected void initialiseInternal(SetOfStatements workflowDescription) {
		// Get the label of the graph
		logger.info("ConceptWiki plugin initialized");
		outputGraphName=super.getNamedGraphFromParameters(workflowDescription, RDFConstants.DEFAULTOUTPUTNAME);
	}

	private InputStream cw_call_api(String query) {
		String q = query.replaceAll("[\'\"]", "");
		String request = "http://staging.conceptwiki.org/web-ws/concept/" + q;
		logger.info(request);
		HttpClient client = new HttpClient();
	    PostMethod method = new PostMethod(request);
	    method.addRequestHeader("Accept","text/turtle");
	    logger.info(method.toString());
	    try {
			int statusCode = client.executeMethod(method);
			if(statusCode != 200) {
			    logger.info(method.getResponseBodyAsString());
			}
		} catch (HttpException e) { // any error fetching will be suppressed: log only
			logger.error("HttpException, printing stacktrace");
			e.printStackTrace();
		} catch (IOException e) {
	        logger.error("IOException, printing stacktrace");
			e.printStackTrace();
		}
	    InputStream result = null;
	    // Get the response body
	    try {
			result = method.getResponseBodyAsStream();
		} catch (IOException e) {
	        logger.error("IOException, printing stacktrace");
			e.printStackTrace();
		}
	    //logger.info(result);
	    return result;
	}

	
	
	/**
	 * Called on plug-in invokation. The actual "work" should be done in this method.
	 * 
	 * @param input 
	 * 		a set of statements containing the input for this plug-in
	 * 
	 * @return a set of statements containing the output of this plug-in
	 */
	@Override
	protected SetOfStatements invokeInternal(SetOfStatements input) {
		logger.debug("Input: "+input.getStatements().toString());
	
		final URI label;
		if (outputGraphName==null)
			label=new URIImpl("http://larkc.eu#arbitraryGraphLabel" + UUID.randomUUID());
		else
			label=outputGraphName;
		
	    final RdfStoreConnection myStore=DataFactory.INSTANCE.createRdfStoreConnection();
		
		// Does not care about the input name since it has a single argument, use any named graph
		SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);

		if( query instanceof SPARQLQueryImpl)
		{
			StatementPatternCollector spc = new StatementPatternCollector();
			((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr().visit(spc);
	
			Iterator<StatementPattern> sp_i = spc.getStatementPatterns().iterator();
			while (sp_i.hasNext()) {
				StatementPattern sp = sp_i.next();
				if (sp.getPredicateVar().getValue()==null || sp.getObjectVar().getValue()==null)
					continue;
				URI p = (URI) sp.getPredicateVar().getValue();
				Value o = (Value) sp.getObjectVar().getValue();
				logger.info("sp: " + sp.toString());

				if (p.toString().startsWith(CW_QUERY)) { // parse SPARQL query and build CW REST query string
					String cw_query = "";
					String var2 = null;
					String pred2 = null;
					if(p.toString().endsWith(CW_SEARCH)) {
						logger.info("Found a " + CW_SEARCH + " predicate");
						cw_query += "search/?q=";
					} else if(p.toString().endsWith(CW_SEARCH_URL)) {
						logger.info("Found a " + CW_SEARCH_URL + " predicate");
						cw_query += "search/forUrl?q=";
					} else if(p.toString().endsWith(CW_GET_CONCEPT)) {
						logger.info("Found a " + CW_GET_CONCEPT + " predicate");
						cw_query += "get/?uuid=";
					} else if(p.toString().endsWith(CW_SEARCH_BY_TAG) || p.toString().endsWith(CW_TAG_SPEC)) {
						cw_query += "search/byTag?";
						if(p.toString().endsWith(CW_SEARCH_BY_TAG)) {
							logger.info("Found a " + CW_SEARCH_BY_TAG + " predicate");
							cw_query += "q=" + o.toString() + "&uuid=";
						} else {
							logger.info("Found a " + CW_TAG_SPEC + " predicate");
							cw_query += "uuid=" + o.toString() + "&q=";
 						}
						if(sp_i.hasNext()) {
							sp = sp_i.next();
							var2 = o.toString();
							o = (Value) sp.getObjectVar().getValue();
							p = (URI) sp.getPredicateVar().getValue();
							pred2 = p.toString();

							
						} else {
							logger.error("ConceptWiki search_by_tag requires two predicates: " + CW_SEARCH_BY_TAG + " and " + CW_TAG_SPEC);
						}
					} else {
						logger.error("Not a ConceptWiki function");
					}
                    RDFParser parser=new TurtleParser();
                    parser.setStopAtFirstError(false);
                    parser.setVerifyData(false);
                    parser.setDatatypeHandling(DatatypeHandling.IGNORE);
                    parser.setRDFHandler(new RDFHandlerBase() {
                    	public void handleStatement(Statement s) {
                    		//logger.info("statement sub: " + s.getSubject().toString().trim() + "\n");
                    		//logger.info("statement pre: " + s.getPredicate().toString().trim() + "\n");
                    		//logger.info("statement obj: " + s.getObject().toString().trim() + "\n");

                    		// collect UUIDs 
                    		if(s.getPredicate().toString().endsWith("www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                    			uuidCache.add(s.getSubject().toString());
                    		}
                    		if (s.getContext()!=null)
                    			myStore.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), (URI)s.getContext(), label);
                    		else
                    			myStore.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), FIXEDCONTEXT, label);
                           }
                    });
                    try {
						//parser.parse(test, outputGraphName.toString());
                    	
                    	// if the cw_call_api() succeeds, then add 1 or 2 (for getByTag) triples that link query to result
                    	// parse call will put UUIDs in uuidCache
                    	uuidCache.clear();
                    	InputStream cw_result = cw_call_api(cw_query + o.toString());
						parser.parse(cw_result, outputGraphName.toString());
					} catch (Exception e) {
						logger.error("Could not parse result of call to ConceptWiki");
						e.printStackTrace();
					}
						//System.out.println("uuidCache contains: " + uuidCache.size());
						String obj = o.toString();
						obj = obj.replaceAll("\"", "");

						//System.out.println(id + " " + p.toString() + " " + obj);

						for(String id: uuidCache) {
							if(! id.startsWith("http")) {
								id = CW_PREFIX + id;
							}

                			
							if( var2 == null ) {
								if(p.toString().endsWith(CW_GET_CONCEPT)) {
									if(! obj.startsWith("http")) {
										obj = CW_PREFIX + obj;
									}
									//System.out.println(id + " " + p.toString() + " " + obj);
									myStore.addStatement(new URIImpl(id), new URIImpl(p.toString()), new URIImpl(obj), FIXEDCONTEXT, label);
								} else {
									//System.out.println(id + " " + p.toString() + " " + obj);
									myStore.addStatement(new URIImpl(id), new URIImpl(p.toString()), new LiteralImpl(obj), FIXEDCONTEXT, label);
								}
								
							} else {
								if(pred2.endsWith(CW_TAG_SPEC)) {
									//System.out.println("pred2: " + pred2);
									//obj = obj.replaceAll("\"", "");
									if(! obj.startsWith("http")) {
										obj = CW_PREFIX + obj;
									}
									//System.out.println(id + " " + p.toString() + " " + obj);
									myStore.addStatement(new URIImpl(id), new URIImpl(p.toString()), new URIImpl(obj), FIXEDCONTEXT, label);
									//System.out.println(id + " " + CW_QUERY + CW_SEARCH_BY_TAG + " " + var2);
									myStore.addStatement(new URIImpl(id), new URIImpl(CW_QUERY+CW_SEARCH_BY_TAG), new LiteralImpl(var2), FIXEDCONTEXT, label);
								} else {
									//System.out.println(id + " " + p.toString() + " " + obj);
									myStore.addStatement(new URIImpl(id), new URIImpl(p.toString()), new LiteralImpl(obj), FIXEDCONTEXT, label);
									var2 = var2.replaceAll("\"", "");
									if(! var2.startsWith("http")) {
										var2 = CW_PREFIX + var2;
									}
									//System.out.println(id + " " + CW_QUERY + CW_TAG_SPEC + " " + var2);
									myStore.addStatement(new URIImpl(id), new URIImpl(CW_QUERY+CW_TAG_SPEC), new URIImpl(var2), FIXEDCONTEXT, label);
								}
							}
						}
				} else {
					logger.info("No ConceptWiki predicates found");
				}
			}
		}
		
		
		
		myStore.close();
		// Create the metadata for the output
		ArrayList<Statement> l=new ArrayList<Statement>();
		l.add(new StatementImpl(new BNodeImpl(UUID.randomUUID()+""), RDFConstants.DEFAULTOUTPUTNAME, label));
		
		return new SetOfStatementsImpl(l);
	}

	/**
	 * Called on plug-in destruction. Plug-ins are destroyed on workflow deletion.
	 * Free an resources you might have allocated here.
	 */
	@Override
	protected void shutdownInternal() {
		// TODO Auto-generated method stub
	}
}
