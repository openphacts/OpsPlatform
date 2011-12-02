package eu.ops.plugin.sparqlexpand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.query.RemoteSPARQLEndpoint;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.plugin.Plugin;

/**
 * <p>Generated LarKC plug-in skeleton for <code>eu.ops.plugin.sparqlexpand.SPARQLExpand</code>.
 * Use this class as an entry point for your plug-in development.</p>
 */
public class SPARQLExpand extends Plugin
{
	private static final URIImpl ENDPOINTADDRESS = new URIImpl("http://larkc.eu/schema#endpointaddr");
//	private static final URI TIMEOUT = new URIImpl("http://larkc.eu/schema#timeout");;
	private List<String> endpoints;
	public static final URI FIXEDCONTEXT=new URIImpl("http://larkc.eu#Fixedcontext");
	private URI outputGraphName;
//	private int timeout;

	/**
	 * Constructor.
	 * 
	 * @param pluginUri 
	 * 		a URI representing the plug-in type, e.g. 
	 * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public SPARQLExpand(URI pluginUri) {
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
	protected void initialiseInternal(SetOfStatements params) {
		// TODO Auto-generated method stub
		logger.info("SPARQLExpand initialized. Hello World!");
		
		endpoints= DataFactory.INSTANCE.extractObjectsForPredicate(params, ENDPOINTADDRESS);
		/*		List<String> to= DataFactory.INSTANCE.extractObjectsForPredicate(params, TIMEOUT);
				if (to.size()==1)
					this.timeout=Integer.parseInt(to.get(0));
				else if(to.size()!=0)
					throw new IllegalArgumentException("Multiple timeout values at initialization: timeouts=" + to);
		*/		
		outputGraphName=super.getNamedGraphFromParameters(params, RDFConstants.DEFAULTOUTPUTNAME);
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
		logger.info("SPARQLExpand working.");
		
		
	    logger.debug("Input: "+input.getStatements().toString());
		
	    final RdfStoreConnection myStore=DataFactory.INSTANCE.createRdfStoreConnection();
	    final URI label;
		if (outputGraphName==null)
			label=new URIImpl("http://larkc.eu#arbitraryGraphLabel" + UUID.randomUUID());
		else
			label=outputGraphName;
	    
	    
	 // Does not care about the input name since it has a single argument, use any named graph
		SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);

		
		if( query instanceof SPARQLQueryImpl)
		{
			StatementPatternCollector spc = new StatementPatternCollector();
			((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr().visit(spc);
	
			for (StatementPattern sp : spc.getStatementPatterns())
			{
					String originalQueryString=query.toString();
				    String smallq = "SELECT * WHERE { " + this.printStatementPattern(sp) + "}"; 
				    
				    
					logger.debug("Query = " + smallq);
					
					
					SPARQLQuery nq = new SPARQLQueryImpl(smallq);
				    runSPARQLQuery(nq.toRDF(), myStore);
				   
				
			}
		}
		
		//final ArrayList<Statement> l=new ArrayList<Statement>();
		//l.add(new StatementImpl(new BNodeImpl(UUID.randomUUID()+""), RDFConstants.DEFAULTOUTPUTNAME, label));
		
		return new SetOfStatementsImpl(new ArrayList<Statement>());
	}
	
	protected void runSPARQLQuery(SetOfStatements input, RdfStoreConnection myStore)
	{
		// Convert query into a CONSTRUCT query
		SPARQLQuery originalQuery=DataFactory.INSTANCE.createSPARQLQuery(input);
		
		SPARQLQueryImpl modifiedQuery =new SPARQLQueryImpl(transformQuery(originalQuery));
		logger.info("Query rewritten to: " +modifiedQuery);
			
		
		
		final URI label;
		if (outputGraphName==null)
			label=new URIImpl("http://larkc.eu#arbitraryGraphLabel" + UUID.randomUUID());
		else
			label=outputGraphName;
		
		for (String endpoint: endpoints) {
			logger.debug("Invoking " + endpoint);
			
			RemoteSPARQLEndpoint se=new RemoteSPARQLEndpoint(new URIImpl(endpoint));
			int count=0;

			
			CloseableIterator<Statement> res=se.executeConstruct(modifiedQuery).getStatements();
			while (res.hasNext()) {
				Statement s=res.next();
				if (s.getContext()!=null)
					myStore.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), (URI)s.getContext(), label);
				else
					myStore.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), FIXEDCONTEXT, label);
				count++;
			}
			logger.debug("Downloaded " + count + " triples");
		}
		
		
	}

	protected String transformQuery(SPARQLQuery originalQuery) {
		String originalQueryString=originalQuery.toString();
		String modifiedQuery="";
		
		if (originalQuery.isAsk() || originalQuery.isDescribe())
			throw new IllegalArgumentException("SPARQLSelector only works with SELECT and CONSTRUCT queries");
		
		if (originalQuery.isSelect()) {
			// Convert to CONSTRUCT

			StatementPatternCollector spc = new StatementPatternCollector();
			((SPARQLQueryImpl) originalQuery).getParsedQuery().getTupleExpr()
					.visit(spc);

			modifiedQuery+="construct {";
			
			for (StatementPattern sp : spc.getStatementPatterns()) {
				modifiedQuery+=printStatementPattern(sp);
			}
			modifiedQuery+="} where ";
			int beginWhere=originalQueryString.indexOf("{");
			modifiedQuery+=originalQueryString.substring(beginWhere);
		}
		else
			modifiedQuery=originalQueryString;
		return modifiedQuery;
	}

	protected String printStatementPattern(StatementPattern sp) {
		String ret="";
		
		Resource s = (Resource) sp.getSubjectVar().getValue();
		URI p = (URI) sp.getPredicateVar().getValue();
		Value o = (Value) sp.getObjectVar().getValue();

		if (s != null) {
			ret+="<"+s.toString() +">";
		} else
			ret+="?"+ sp.getSubjectVar().getName();

		ret+=" ";
		
		ret+= (p == null) ? ("?" + sp.getPredicateVar()
				.getName()) : "<" + p.toString() + ">";
		
		ret+=" ";
		
		if (o == null) {
			ret+="?" + sp.getObjectVar().getName();
		} 
		else if (o instanceof Literal) {
			ret+=o.toString();
		}
		else if (o instanceof URI) {
			ret+="<"+o.toString()+">";
		} else
			throw new IllegalArgumentException(
					"Unrecognized term in object position");
		
		ret+=". ";
		
		return ret;
	}
	
	
	/**
	 * Called on plug-in destruction. Plug-ins are destroyed on workflow deletion.
	 * Free an resources you might have allocated here.
	 */
	@Override
	protected void shutdownInternal() {
		// TODO Auto-generated method stub
	}
	
	public static void main(String[] args) {
		SPARQLExpand s=new SPARQLExpand(null);
		s.endpoints=new ArrayList<String>();
		s.endpoints.add("http://linkedlifedata.com/sparql");
		String qStr = " SELECT ?protein" +
				" WHERE {" +
				" ?protein <http://www.biopax.org/release/biopax-level2.owl#NAME> \"Phytochrome A\" " +
				"}";
		
		System.out.println(qStr);
		s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
		
		//?interaction <http://www.biopax.org/release/biopax-level2.owl#PARTICIPANTS> ?p ." +
		//" ?p <http://www.biopax.org/release/biopax-level2.owl#PHYSICAL-ENTITY> ?protein ." +
	}
}
