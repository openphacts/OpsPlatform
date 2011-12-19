package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>eu.ops.plugin.irssparqlexpand.IRSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IRS service.
 */
public class IRSSPARQLExpand1 extends Plugin {

    protected final Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand1.class);
    private IRSMapper irsMapper = null;
    private boolean showExpandedVariables = false;
    
    /**
     * Constructor.
     * 
     * @param pluginUri 
     * 		a URI representing the plug-in type, e.g. 
     * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
     */
    public IRSSPARQLExpand1(URI pluginUri) {
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
        irsMapper = instantiateIRSMapper();
        logger.info("IRSSPARQLExpand initialized.");
        System.out.println("*********************Initialised!!!");
    }
    
    IRSMapper instantiateIRSMapper() {
    	System.out.println("*********************");
            return new IRSClient1();
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
        System.out.println("*********************Invoked!!!");
        if (logger.isDebugEnabled()) {
            logger.debug("Input: " + input.getStatements().toString());
        }
        System.out.println("Input: " + input.getStatements().toString());
        // Does not care about the input name since it has a single argument, use any named graph
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);
        //Only working with select queries of BGP
        if (!query.isSelect()) {
            logger.info("No query exapnsion performed as not a select query.");
            return input;
        }
        if (query instanceof SPARQLQuery) {
            String queryString = query.toString();
            TupleExpr tupleExpr;
            try {
                tupleExpr = QueryUtils.queryStringToTupleExpr(queryString);
                URIFinderVisitor uriFindervisitor = new URIFinderVisitor();
                tupleExpr.visit(uriFindervisitor);
                Set<URI> uriSet = uriFindervisitor.getURIS();
                Map<URI, List<URI>> uriMappings = irsMapper.getMatchesForURIs(uriSet);    
                QueryExpandAndWriteVisitor writerVisitor = 
                        new QueryExpandAndWriteVisitor(uriMappings, showExpandedVariables);
                tupleExpr.visit(writerVisitor);
                String expandedQueryString = writerVisitor.getQuery();
                //System.out.println(expandedQueryString);
                SPARQLQuery expandedQuery = new SPARQLQueryImpl(expandedQueryString);
                return expandedQuery.toRDF();
            } catch (MalformedQueryException ex) {
                logger.warn("Problem converting query String to TupleExpr.", ex);
            } catch (QueryModelExpanderException ex) {
                logger.warn("Problem extracting URIs.", ex);
            } catch (UnexpectedQueryException ex) {
                logger.warn("Problem writing expanded query.", ex);
            }
            //Failed so return input
            return input;
        }
        //We don't do None SPARQL queries.
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

    public void setShowExpandedVariable(boolean show){
        showExpandedVariables = show;
    }
    
    public static void main(String[] args) {
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand"));
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
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println("Expanded query:\n\t" + query);
    }

}
