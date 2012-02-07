package eu.ops.plugin.imssparqlexpand;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import eu.ops.plugin.imssparqlexpand.ims.IMSClient;
import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>eu.ops.plugin.imssparqlexpand.IMSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IMS service.
 */
public class IMSSPARQLExpand extends Plugin {

    protected static Logger logger = LoggerFactory.getLogger(Plugin.class);
    private IMSMapper imsMapper = null;
    private boolean showExpandedVariables = false;
    List<String> requiredAttributes;
    static final String ATTR_PARAM = "http://larkc.eu/schema#requiredAttributes";

    /**
     * Constructor.
     * 
     * @param pluginUri 
     * 		a URI representing the plug-in type, e.g. 
     * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
     */
    public IMSSPARQLExpand(URI pluginUri) {
        super(pluginUri);
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
        imsMapper = instantiateIMSMapper();
        if (params != null) {
            CloseableIterator<Statement> parameters = params.getStatements();
            while (parameters.hasNext()) {
                Statement stmt = parameters.next();
                if (stmt.getPredicate().equals(new URIImpl(ATTR_PARAM))) {
                    requiredAttributes = new ArrayList<String>();
                    String attributes = stmt.getObject().stringValue();
                    String[] splitAttributes = attributes.split(",");
                    for (int i = 0; i < splitAttributes.length; i++) {
                        requiredAttributes.add(splitAttributes[i].trim());
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Required Attributes: " + requiredAttributes);
        }
        logger.info("IMSSPARQLExpand initialized.");
        //ystem.out.println("*********************Initialised!!!");
    }

    IMSMapper instantiateIMSMapper() {
        //ystem.out.println("*********************");
        return new IMSClient();
    }

    private SetOfStatements expandQuery(TupleExpr tupleExpr, Dataset dataset)
            throws QueryExpansionException {
        //URIFinderVisitor uriFindervisitor = new URIFinderVisitor();
        //tupleExpr.visit(uriFindervisitor);
        //Set<URI> uriSet = uriFindervisitor.getURIS();
        //Map<URI, List<URI>> uriMappings = imsMapper.getMatchesForURIs(uriSet);   
         ContextListerVisitor counter = new ContextListerVisitor();
         tupleExpr.visit(counter);
         ArrayList<Var> contexts = counter.getContexts();

        QueryExpandAndWriteVisitor writerVisitor = 
                new QueryExpandAndWriteVisitor(dataset, requiredAttributes, imsMapper, contexts);
        tupleExpr.visit(writerVisitor);
        String expandedQueryString = writerVisitor.getQuery();
        //logger.info("Expanded SPARQL: " + expandedQueryString);
        //ystem.out.println(expandedQueryString);
        try {
            SPARQLQuery expandedQuery = new SPARQLQueryImpl(expandedQueryString);
            return expandedQuery.toRDF();
        } catch (IllegalArgumentException ex){
            System.out.println(expandedQueryString);
            ex.printStackTrace();
            throw ex;
        }    
    }

    /**
     * Called on plug-in invokation. The actual "work" should be done in this method.
     * <p>
     * For testing and none Larkc use it is better to throw the exceptions.
     * @param input 
     * 		a set of statements containing the input for this plug-in
     * 
     * @return a set of statements containing the output of this plug-in
     */
    public final SetOfStatements invokeInternalWithExceptions(SetOfStatements input)
            throws MalformedQueryException, QueryExpansionException {
        logger.info("SPARQLExpand working.");
        //ystem.out.println("*********************Invoked!!!");
        if (logger.isDebugEnabled()) {
            logger.debug("Input: " + input.getStatements().toString());
        }
        //ystem.out.println("Input: " + input.getStatements().toString());
        // Does not care about the input name since it has a single argument, use any named graph
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);
        logger.info("IMSSPARQLExpand: Query is a: " + query.getClass());
        if (query instanceof SPARQLQueryImpl) {
            SPARQLQueryImpl impl = (SPARQLQueryImpl) query;
            ParsedQuery parsedQuery = impl.getParsedQuery();
            TupleExpr tupleExpr = parsedQuery.getTupleExpr();
            Dataset dataset = parsedQuery.getDataset();
            //TODO getAttributes
            return expandQuery(tupleExpr, dataset);
        } else {
            String queryString = query.toString();
            TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(queryString);
            Dataset dataset;
            try {
                dataset = QueryUtils.convertToOpenRdf(query.getDataSet());
            } catch (NullPointerException e) {
                //crap implementation does not check if dataset is null.
                dataset = null;
            }
            //TODO getAttributes
            return expandQuery(tupleExpr, dataset);
        }
    }

    /**
     * Called on plug-in invokation. The actual "work" should be done in this method.
     * <p>
     * Larkc can not handle exceptions so best to catch and log them and just return input.
     * @param input 
     * 		a set of statements containing the input for this plug-in
     * 
     * @return a set of statements containing the output of this plug-in
     */
    @Override
    protected SetOfStatements invokeInternal(SetOfStatements input) {
        try {
            SetOfStatements result = invokeInternalWithExceptions(input);
            logger.info("Query expansion successful: " + result.toString());
            return result;
        } catch (MalformedQueryException ex) {
            logger.warn("Problem converting query String to TupleExpr.", ex);
        } catch (QueryExpansionException ex) {
            logger.warn("Problem writing expanded query.", ex);
        }
        //Failed so return input
        logger.info("IMSSPARQLExpand: ERROR: Returning input");
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

    public void setShowExpandedVariable(boolean show) {
        showExpandedVariables = show;
    }

    public static void main(String[] args) throws MalformedQueryException, QueryExpansionException {
        IMSSPARQLExpand s = new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand"));
        s.initialiseInternal(null);
        String qStr = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                //                + "OPTIONAL {?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                //                + "<http://rdf.chemspider.com/37> ?p ?o ."
                //                + "FILTER (?protein = ?o) . "
                //               + "FILTER (?protein = <http://something.org>) . "// || ?protein = <http://somewhere.com>) ."
                //                + "FILTER (?protein = ?name). }"
                + "}";

        System.out.println("Original query:\n\t" + qStr + "\n");
        SetOfStatements eQuery = s.invokeInternalWithExceptions(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println("Expanded query:\n\t" + query);
    }
}
