package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>eu.ops.plugin.irssparqlexpand.IRSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IRS service.
 */
public class IRSSPARQLExpand extends Plugin {

    protected final Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);
    private IRSClient irsClient = null;

    /**
     * Constructor.
     * 
     * @param pluginUri 
     * 		a URI representing the plug-in type, e.g. 
     * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
     */
    public IRSSPARQLExpand(URI pluginUri) {
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
        irsClient = instantiateIRSClient();
        logger.info("IRSSPARQLExpand initialized.");
        System.out.println("*********************Initialised!!!");
    }
    
    IRSClient instantiateIRSClient() {
    	System.out.println("*********************");
            return new IRSClient();
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
            final int whereStartIndex = queryString.indexOf("{")+1;
            final int whereEndIndex = queryString.lastIndexOf("}");
            String queryStart = queryString.substring(0, whereStartIndex);
            String queryEnd = queryString.substring(whereEndIndex, queryString.length());

            StatementPatternCollector spc = new StatementPatternCollector();
            final ParsedQuery parsedQuery = ((SPARQLQueryImpl) query).getParsedQuery();
//System.out.println(parsedQuery);
            final TupleExpr tupleExpr = parsedQuery.getTupleExpr();
            tupleExpr.visit(spc); 
            Map<URI, List<URI>> uriMappings = retrieveRequiredUriMappings(spc);
            if (logger.isDebugEnabled()) {
                logger.debug("Number of URIs in query " + uriMappings.size());
            }
            if (uriMappings.isEmpty()) {
                return input;
            } else {               
                try {
System.out.println("****Original query tree" + tupleExpr + "\n");
                    QueryModelExpander queryExpander = new QueryModelExpander(uriMappings);
                    tupleExpr.visit(queryExpander);
System.out.println("****Expanded query tree" + tupleExpr + "\n");
                    String expandedQueryString = QueryUtils.tupleExprToQueryString(tupleExpr);
System.out.println("****Expanded query:\n\t" + expandedQueryString);
                    SPARQLQuery expandedQuery = new SPARQLQueryImpl(expandedQueryString);
                    return expandedQuery.toRDF();
                } catch (UnexpectedQueryException ex) {
                    logger.warn("Problem in writing expanded query back to a query string. "
                            + "Returning original query.", ex);
                    return input;
                } catch (QueryModelExpanderException ex) {
                    logger.warn("Problem in expanding query. Returning original query.", ex);
                    return input;
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unable to expand query " + input);
        }
        return input;
    }

    /**
     * Scan query for all occurrences of instance URIs and retrieve
     * equivalence mappings.
     * 
     * @param spc
     * @return Map of equivalent URIs
     */
    private Map<URI, List<URI>> retrieveRequiredUriMappings(StatementPatternCollector spc) {
        Value value;
        Var s, o;
        Set<URI> uriSet = new HashSet<URI>();
        for (StatementPattern sp : spc.getStatementPatterns()) {
            s = sp.getSubjectVar();
            if (s.hasValue()) {
                value = s.getValue();
                if (value instanceof URI) {
                    URI uri = (URI) value;
                    uriSet.add(uri);
                }
            }
            o = sp.getObjectVar();
            if (o.hasValue()) {
                value = o.getValue();
                if (value instanceof URI) {
                    URI uri = (URI) value;
                    uriSet.add(uri);
                }
            }
        }
        Map<URI, List<URI>> uriMappings = irsClient.getMatchesForURIs(uriSet);
        return uriMappings;
    }
//
//    /**
//     * Generates a string representation of a Statement Pattern.
//     * 
//     * @param sp statement that should be converted to a string
//     * @return String representation of the statement
//     */
//    private String statementPatternAsString(StatementPattern sp) {
//        StringBuilder queryBuilder = new StringBuilder();
//        String subject = varAsString(sp.getSubjectVar());
//        String predicate = varAsString(sp.getPredicateVar());
//        String object = varAsString(sp.getObjectVar());
//        queryBuilder.append(subject).append(" ");
//        queryBuilder.append(predicate).append(" ");
//        queryBuilder.append(object).append(" . ");
//        return queryBuilder.toString();
//    }
//
//    /**
//     * Retrieves a string representation of part of a statement pattern that is 
//     * represented as a Var.
//     * @param var object to be converted to a valid string representation
//     * @return string representation.
//     */
//    private String varAsString(Var var) {
//        String varString;
//        if (var.hasValue()) {
//            final Value value = var.getValue();
//            varString = value.stringValue();
//            if (value instanceof URI) {
//                varString = "<" + varString + ">";
//            }
//        } else {
//            varString = "?" + var.getName();
//        }
//        return varString;
//    }
//
//    /**
//     * Expand the supplied query into a set of UNION queries
//     * 
//     * @param queryStart select clause of the original SPARQL query with WHERE {
//     * @param queryWhereClause original text of the WHERE clause
//     * @param queryEnd everything from the close of the WHERE clause in the original query
//     * @param uriMap Map of equivalent URIs for all URIs that appear in the subject or object of BGP in the query
//     * @return expanded query
//     */
//    private SPARQLQuery constructExpandedQuery(String queryStart, 
//            StatementPatternCollector spc, String queryEnd, Map<URI, List<URI>> uriMap) 
//            throws QueryExpansionException {
//        logger.debug("Expanding query:");
//        Value value;
//        
//        final List<StatementPattern> statementPatterns = spc.getStatementPatterns();
//        StringBuilder whereClause = new StringBuilder();
//        for (int i = 0; i < statementPatterns.size(); i++) {
//            int lineNumber = i + 1;
//            StatementPattern sp = statementPatterns.get(i);
//            String subject = "";
//            String subjectFilter = "";
//            String predicate = "";
//            String object = "";
//            String objectFilter = "";
//            Var s = sp.getSubjectVar();
//            if (s.hasValue() && s.getValue() instanceof URI) {
//                URI uri = (URI) s.getValue();
//                subject = "?subjectUriLine" + lineNumber;
//                final List<URI> mappings = uriMap.get(uri);
//                mappings.add(uri);
//                subjectFilter = constructFilter(subject, mappings);
//            } else {
//                subject = varAsString(s);
//            }
//            predicate = varAsString(sp.getPredicateVar());
//            Var o = sp.getObjectVar();
//            if (o.hasValue() && o.getValue() instanceof URI) {
//                URI uri = (URI) o.getValue();
//                object = "?objectUriLine" + lineNumber;
//                final List<URI> mappings = uriMap.get(uri);
//                mappings.add(uri);
//                objectFilter = constructFilter(object, mappings);
//            } else {
//                object = varAsString(o);
//            }
//            whereClause.append(subject).append(" ")
//                    .append(predicate).append(" ")
//                    .append(object).append(" . ");
//            whereClause.append(subjectFilter);
//            whereClause.append(objectFilter);
//        }
//        final String queryText = queryStart + whereClause.toString() + queryEnd;
////System.out.println(queryText);        
//        final SPARQLQueryImpl expandedQuery = 
//                new SPARQLQueryImpl(queryText);
//        if (logger.isDebugEnabled()) {
//            logger.debug("Expanded query: " + expandedQuery.toString());
//        }
//        return expandedQuery;
//    }
//    
//    /**
//     * Constructs the FILTER clause to check the given variable with all
//     * permutations of equivalent URIs.
//     * 
//     * @param variableName name of the new variable in the query
//     * @param mappings List of equivalent URIs
//     * @return FILTER clause checking the disjunction of the URIs
//     */
//    private String constructFilter(String variableName, List<URI> mappings) {
//        StringBuilder filterText = new StringBuilder("FILTER (");
//        for (URI uri : mappings) {
//            filterText.append(variableName).append(" = ");
//            filterText.append("<").append(uri.stringValue()).append(">");
//            filterText.append(" || ");
//        }
//        filterText.replace(filterText.length() - 4, filterText.length(), ") ");
//        return filterText.toString();
//    }

    /**
     * Called on plug-in destruction. Plug-ins are destroyed on workflow deletion.
     * Free an resources you might have allocated here.
     */
    @Override
    protected void shutdownInternal() {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) {
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand"));
        s.initialiseInternal(null);
        String qStr = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
//                + "OPTIONAL {?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
//                + "<http://rdf.chemspider.com/37> ?p ?o ."
//                + "FILTER (?protein = ?o) . "
                + "FILTER (?protein = <http://something.org>) . "// || ?protein = <http://somewhere.com>) ."
//                + "FILTER (?protein = ?name). }"
                + "}";

        System.out.println("Original query:\n\t" + qStr + "\n");
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println("Expanded query:\n\t" + query);
    }

}
