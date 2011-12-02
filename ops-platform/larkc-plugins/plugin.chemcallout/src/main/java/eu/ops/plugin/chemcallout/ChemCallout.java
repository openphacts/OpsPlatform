package eu.ops.plugin.chemcallout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.query.TriplePattern;
import eu.larkc.core.query.TriplePatternQuery;
import eu.larkc.core.query.TriplePatternQueryImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.plugin.Plugin;
import eu.ops.services.chemspider.SearchClient;
import eu.ops.services.chemspider.model.ERequestStatus;

/**
 * <p>Generated LarKC plug-in skeleton for <code>eu.ops.plugin.chemcallout.ChemCallout</code>.
 * Use this class as an entry point for your plug-in development.</p>
 */
public class ChemCallout extends Plugin
{
	private static String OPS_TOKEN = "5d749a0a-f4b0-444b-8287-aba2c2800ebaXt";
	private static String CHEMSPIDER_WS = "http://inchi.chemspider.com/Search.asmx";
	public static final URI FIXEDCONTEXT=new URIImpl("http://larkc.eu#Fixedcontext");
	
	SearchClient chemSpiderClient = null;

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
		} catch (Exception e) {
			logger.error("Failed to initialise ChemCallout plugin", e);
			chemSpiderClient = null;
			return;
		}		
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
	
		URI label=new URIImpl("http://larkc.eu#arbitraryGraphLabel" + UUID.randomUUID());
	    final RdfStoreConnection myStore=DataFactory.INSTANCE.createRdfStoreConnection();
		
		// Does not care about the input name since it has a single argument, use any named graph
		SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);

		TriplePatternQuery tpq = new TriplePatternQueryImpl(new ArrayList<TriplePattern>());
		
		if( query instanceof SPARQLQueryImpl)
		{
			StatementPatternCollector spc = new StatementPatternCollector();
			((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr().visit(spc);
	
			for (StatementPattern sp : spc.getStatementPatterns()) {
				Resource s = (Resource) sp.getSubjectVar().getValue();
				URI p = (URI) sp.getPredicateVar().getValue();
				Value o = (Value) sp.getObjectVar().getValue();
				logger.info(sp.toString());

				// if predicate is my has_similar function
				if (p.toString().equals("http://wiki.openphacts.org/index.php/ext_function#has_similar")) {
					// grab objects and pass to ChemSpider
					String rid = chemSpiderClient.similaritySearchMock(o.stringValue(), "Tanimoto", .99);
					logger.info("ChemSpider RID="+rid);
					boolean gotResult = false;
					while (!gotResult) {
						pause(1);
						ERequestStatus status = chemSpiderClient.getAsyncSearchStatus(rid);
						logger.info("Status="+status.name());
						if (status == ERequestStatus.RESULT_READY) {
							List<Integer> results = chemSpiderClient.getAsyncSearchResult(rid);
							for (Integer csid : results) {
								// make up triples: chemspider_url has_similar object
								Resource subj = new URIImpl("http://inchi.chemspider.com/Chemical-Structure."+csid+".html");
								myStore.addStatement(subj, p, o, FIXEDCONTEXT, label);
								logger.info("Created triple: "+subj.stringValue()+" has_similar "+o.stringValue());
							}
							gotResult = true;			
						}
					}
				}
			}
		}
			
		return input;
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