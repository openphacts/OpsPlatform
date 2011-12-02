package eu.ops.plugin.chemcallout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.plugin.Plugin;
import eu.ops.services.chemspider.SearchClient;
import eu.ops.services.chemspider.model.ArrayOfInt;
import eu.ops.services.chemspider.model.ERequestStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input: SPARQL query, containing at least one pattern {?x [HAS_SIMILAR|HAS_SUBSTRUCTURE_MATCH|HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER|HAS_EXACT_STRUCTURE_MATCH] ?y}, where ?y is a bound literal representing a molecule
 * Output: Reference to graph with triples in the form: {?x [HAS_SIMILAR|HAS_SUBSTRUCTURE_MATCH|HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER|HAS_EXACT_STRUCTURE_MATCH] ?y}, where ?y is a bound URI in the form (http://inchi.chemspider.com/Chemical-Structure."+csid+".html), where csid is retrieved from chemspider
 * 
 * Given a Set of statementpatterns, invokes the chemspider service and retrieves similar molecules
 */
public class ChemCallout extends Plugin
{
	private static final String HAS_SIMILAR = "http://wiki.openphacts.org/index.php/ext_function#has_similar";
	private static final String HAS_SUBSTRUCTURE_MATCH = "http://wiki.openphacts.org/index.php/ext_function#has_substructure_match";
	private static final String HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER = "http://wiki.openphacts.org/index.php/ext_function#has_substructure_match_or_tautomer";
	private static final String HAS_EXACT_STRUCTURE_MATCH = "http://wiki.openphacts.org/index.php/ext_function#has_exact_structure_match";
	private static final String HTML = "http://wiki.openphacts.org/index.php/ext_function#html";	
	private static String OPS_TOKEN = "5d749a0a-f4b0-444b-8287-aba2c2800ebaXt";
	private static String CHEMSPIDER_WS = "http://inchi.chemspider.com/Search.asmx";
	public static final URI FIXEDCONTEXT=new URIImpl("http://larkc.eu#Fixedcontext");
	private static final int TIMEOUT = 300;
	
	protected final Logger logger = LoggerFactory.getLogger(Plugin.class);
	
	SearchClient chemSpiderClient = null;
	private URI outputGraphName;
	
	/**
	 * Constructor.
	 * 
	 * @param pluginUri 
	 * 		a URI representing the plug-in type, e.g. 
	 * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public ChemCallout(URI pluginUri) {
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
		try {
			chemSpiderClient = new SearchClient();
			chemSpiderClient.setServiceUrl(new java.net.URI(CHEMSPIDER_WS));
			chemSpiderClient.setToken(OPS_TOKEN);
			chemSpiderClient.setJaxbContext(JAXBContext.newInstance(ERequestStatus.class, ArrayOfInt.class));
		} catch (Exception e) {
			logger.error("Failed to initialise ChemCallout plugin", e);
			chemSpiderClient = null;
			return;
		}

		// Get the label of the graph
		outputGraphName=super.getNamedGraphFromParameters(workflowDescription, RDFConstants.DEFAULTOUTPUTNAME);
		logger.info("ChemCallout initialized. Hello World!");
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
		if (chemSpiderClient == null) {
			logger.error("ChemCallout not initialised correctly");
			return null;
		}
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
	
			for (StatementPattern sp : spc.getStatementPatterns()) {
				if (sp.getPredicateVar().getValue()==null || sp.getObjectVar().getValue()==null)
					continue;
				URI p = (URI) sp.getPredicateVar().getValue();
				Value o = (Value) sp.getObjectVar().getValue();
				logger.info(sp.toString());

				// if predicate is my has_similar function
				if (p.toString().equals(HAS_SIMILAR)) {
					// grab objects and pass to ChemSpider
					String rid = chemSpiderClient.similaritySearch(o.stringValue(), "Tanimoto", .99);
					logger.info("ChemSpider RID="+rid);
					int waittime=0;
					boolean gotResult = false;
					while (!gotResult) {
						if (waittime++>TIMEOUT) {
							logger.error("Chemspider web service call timed out after " + waittime + " seconds.");
							break;
						}
						pause(1);
						ERequestStatus status = chemSpiderClient.getAsyncSearchStatus(rid);
						logger.info("Status="+status.name());
						if (status==ERequestStatus.FAILED){
							logger.error("Chemspider web service call failed.");
							break;
						}
						if (status == ERequestStatus.RESULT_READY) {
							List<Integer> results = chemSpiderClient.getAsyncSearchResult(rid);
							if (results!=null){
								for (Integer csid : results) {
									// make up triples: chemspider_url has_similar object
									//					chemspider_url html html_page
									Resource subj = new URIImpl("http://rdf.chemspider.com/"+csid);
									myStore.addStatement(subj, p, o, FIXEDCONTEXT, label);
									Value html = new URIImpl("http://inchi.chemspider.com/Chemical-Structure."+csid+".html");
									URI html_pred=new URIImpl(HTML);
									myStore.addStatement(subj, html_pred, html, FIXEDCONTEXT, label);
									logger.info("Created triple: "+subj.stringValue()+" has_similar "+o.stringValue());
									logger.info("Created triple: "+subj.stringValue()+" html "+html.stringValue());
								}
								gotResult = true;
							}
						}
					}
				}
				// if predicate is the has_substructure_match function
				else if (p.toString().equals(HAS_SUBSTRUCTURE_MATCH)) {
					// grab objects and pass to ChemSpider
					String rid = chemSpiderClient.substructureSearch(o.stringValue(), false);
					logger.info("ChemSpider RID="+rid);
					int waittime=0;
					boolean gotResult = false;
					while (!gotResult) {
						if (waittime++>TIMEOUT) {
							logger.error("Chemspider web service call timed out after " + " seconds.");
							break;
						}
						pause(1);
						ERequestStatus status = chemSpiderClient.getAsyncSearchStatus(rid);
						logger.info("Status="+status.name());
						if (status==ERequestStatus.FAILED){
							logger.error("Chemspider web service call failed.");
							break;
						}
						if (status == ERequestStatus.RESULT_READY) {
							List<Integer> results = chemSpiderClient.getAsyncSearchResult(rid);
							if (results!=null){
								for (Integer csid : results) {
									// make up triples: chemspider_url has_substructure_match object
									//					chemspider_url html html_page
									Resource subj = new URIImpl("http://rdf.chemspider.com/"+csid);
									myStore.addStatement(subj, p, o, FIXEDCONTEXT, label);
									Value html = new URIImpl("http://inchi.chemspider.com/Chemical-Structure."+csid+".html");
									URI html_pred=new URIImpl(HTML);
									myStore.addStatement(subj, html_pred, html, FIXEDCONTEXT, label);;
									logger.info("Created triple: "+subj.stringValue()+" has_substructure_match "+o.stringValue());
									logger.info("Created triple: "+subj.stringValue()+" html "+html.stringValue());
								}
								gotResult = true;	
							}
						}
					}
				}
				// if predicate is the has_substructure_match_or_tautomer function
				else if (p.toString().equals(HAS_SUBSTRUCTURE_MATCH_OR_TAUTOMER)) {
					// grab objects and pass to ChemSpider
					String rid = chemSpiderClient.substructureSearch(o.stringValue(), true);
					logger.info("ChemSpider RID="+rid);
					int waittime=0;
					boolean gotResult = false;
					while (!gotResult) {
						if (waittime++>TIMEOUT) {
							logger.error("Chemspider web service call timed out after " + " seconds.");
							break;
						}
						pause(1);
						ERequestStatus status = chemSpiderClient.getAsyncSearchStatus(rid);
						logger.info("Status="+status.name());
						if (status==ERequestStatus.FAILED){
							logger.error("Chemspider web service call failed.");
							break;
						}
						if (status == ERequestStatus.RESULT_READY) {
							List<Integer> results = chemSpiderClient.getAsyncSearchResult(rid);
							if (results!=null){
								for (Integer csid : results) {
									// make up triples: chemspider_url has_substructure_match object
									//					chemspider_url html html_page
									Resource subj = new URIImpl("http://rdf.chemspider.com/"+csid);
									myStore.addStatement(subj, p, o, FIXEDCONTEXT, label);
									Value html = new URIImpl("http://inchi.chemspider.com/Chemical-Structure."+csid+".html");
									URI html_pred=new URIImpl(HTML);
									myStore.addStatement(subj, html_pred, html, FIXEDCONTEXT, label);
									logger.info("Created triple: "+subj.stringValue()+" has_substructure_match_or_tautomer "+o.stringValue());
									logger.info("Created triple: "+subj.stringValue()+" html "+html.stringValue());
								}
								gotResult = true;	
							}
						}
					}
				}
				// if predicate is the has_exact_structure_match function
				else if (p.toString().equals(HAS_EXACT_STRUCTURE_MATCH)) {
					// grab objects and pass to ChemSpider
					String rid = chemSpiderClient.structureSearch(o.stringValue(), "ExactMatch");
					logger.info("ChemSpider RID="+rid);
					int waittime=0;
					boolean gotResult = false;
					while (!gotResult) {
						if (waittime++>TIMEOUT) {
							logger.error("Chemspider web service call timed out after " + " seconds.");
							break;
						}
						pause(1);
						ERequestStatus status = chemSpiderClient.getAsyncSearchStatus(rid);
						logger.info("Status="+status.name());
						if (status==ERequestStatus.FAILED){
							logger.error("Chemspider web service call failed.");
							break;
						}
						if (status == ERequestStatus.RESULT_READY) {
							List<Integer> results = chemSpiderClient.getAsyncSearchResult(rid);
							if (results!=null){
								for (Integer csid : results) {
									// make up triples: chemspider_url has_substructure_match object
									//					chemspider_url html html_page
									Resource subj = new URIImpl("http://rdf.chemspider.com/"+csid);
									myStore.addStatement(subj, p, o, FIXEDCONTEXT, label);
									Value html = new URIImpl("http://inchi.chemspider.com/Chemical-Structure."+csid+".html");
									URI html_pred=new URIImpl(HTML);
									myStore.addStatement(subj, html_pred, html, FIXEDCONTEXT, label);
									logger.info("Created triple: "+subj.stringValue()+" has_exact_structure_match "+o.stringValue());
									logger.info("Created triple: "+subj.stringValue()+" html "+html.stringValue());
								}
								gotResult = true;	
							}
						}
					}
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
	
	
	
	private void pause(long s) {
		try {
			Thread.currentThread().sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
