package eu.ops.plugin.irssparqlexpand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.irs.beans.Match;

/**
 * The <code>eu.ops.plugin.irssparqlexpand.IRSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IRS service.
 */
public class IRSSPARQLExpand extends Plugin {

    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);
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
    }
    
    protected IRSClient instantiateIRSClient() {
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

        if (logger.isDebugEnabled()) {
            logger.debug("Input: " + input.getStatements().toString());
        }

        // Does not care about the input name since it has a single argument, use any named graph
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);
        //Only working with select queries of BGP
        if (!query.isSelect()) {
            logger.info("No query exapnsion performed as not a select query.");
            return input;
        }
        
        List<Match> matches;
        List<StatementPattern> spList = new ArrayList<StatementPattern>();
        boolean found;
        String queryFirstBlock = "";
        Var s;
        Var o;

        if (query instanceof SPARQLQuery) {
            String queryString = query.toString();
            final int whereStartIndex = queryString.indexOf("{")+1;
            final int whereEndIndex = queryString.lastIndexOf("}")-1;
            String queryStart = queryString.substring(0, whereStartIndex);
            String queryWhereClause = queryString.substring(whereStartIndex, whereEndIndex);
            String queryEnd = queryString.substring(whereEndIndex, queryString.length());
            System.out.println("Query:\n\t" + queryStart + "\n\t" + 
                    queryWhereClause + "\n\t" + queryEnd + "\n\n");

            StatementPatternCollector spc = new StatementPatternCollector();
            ((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr().visit(spc);
            StringBuilder queryBuilder = new StringBuilder();            
            found = false;
            Map<URI, List<URI>> uriMap = new HashMap<URI, List<URI>>();
            for (StatementPattern sp : spc.getStatementPatterns()) {
                String subject = varAsString(sp.getSubjectVar());
                s = sp.getSubjectVar();
                if (s.hasValue()) {
                    Value value = s.getValue();
                    if (value instanceof URI) {
                        URI uri = (URI) value;
                        List<URI> uriList = irsClient.getMatchesForURI(uri);
                        uriMap.put(uri, uriList);
System.out.println("Number of matches for " + uri + ": " + uriList.size());
                    }
                }
                
                o = sp.getObjectVar();
                if (o.hasValue()) {
                    Value value = o.getValue();
                    if (value instanceof URI) {
                        URI uri = (URI) value;
                        List<URI> uriList = irsClient.getMatchesForURI(uri);
                        uriMap.put(uri, uriList);
System.out.println("Number of matches for " + uri + ": " + uriList.size());
                    }
                }
            }
            SPARQLQuery expandedQuery = constructExpandedQuery(queryStart, queryWhereClause, queryEnd, uriMap);
            return expandedQuery.toRDF();
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unable to expand query " + input);
        }
        return input;
    }

    /**
     * Generates a string representation of a Statement Pattern.
     * 
     * @param sp statement that should be converted to a string
     * @return String representation of the statement
     */
    private String statementPatternAsString(StatementPattern sp) {
        StringBuilder queryBuilder = new StringBuilder();
        String subject = varAsString(sp.getSubjectVar());
        String predicate = varAsString(sp.getPredicateVar());
        String object = varAsString(sp.getObjectVar());
        queryBuilder.append(subject).append(" ");
        queryBuilder.append(predicate).append(" ");
        queryBuilder.append(object).append(" . ");
        return queryBuilder.toString();
    }

    /**
     * Retrieves a string representation of part of a statement pattern that is 
     * represented as a Var.
     * @param var object to be converted to a valid string representation
     * @return string representation.
     */
    private String varAsString(Var var) {
        String varString;
        if (var.hasValue()) {
            final Value value = var.getValue();
            varString = value.stringValue();
            if (value instanceof URI) {
                varString = "<" + varString + ">";
            }
        } else {
            varString = "?" + var.getName();
        }
        return varString;
    }
    
    /**
     * Expand a statement pattern by interchanging matches on the object
     * 
     * @param sp statement pattern to expand
     * @param matches matches found for the object URI
     * @return list of equivalent statement patterns
     */
    private List<StatementPattern> expandObjectURI(StatementPattern sp, List<Match> matches) {
        List<StatementPattern> spList = new ArrayList<StatementPattern>();
        spList.add(sp);
        Var subject = sp.getSubjectVar();
        Var predicate = sp.getPredicateVar();
        for (Match match : matches) {
            URI uri = new URIImpl(match.getMatchUri());
            Var object = new Var();
            object.setValue(uri);
            StatementPattern spClone = new StatementPattern(subject, predicate, object);
            spList.add(spClone);
        }
        return spList;
    }

    /**
     * Expand the supplied query into a set of UNION queries
     * 
     * @param queryStart select clause of the original SPARQL query with WHERE {
     * @param queryWhereClause original text of the WHERE clause
     * @param queryEnd everything from the close of the WHERE clause in the original query
     * @param uriMap Map of equivalent URIs for all URIs that appear in the subject or object of BGP in the query
     * @return expanded query
     */
    private SPARQLQuery constructExpandedQuery(String queryStart, 
            String queryWhereClause, String queryEnd, Map<URI, List<URI>> uriMap) {
        StringBuilder expandedWhereClause = new StringBuilder(" { " + queryWhereClause);
        String whereClauseText;
        for (URI uri : uriMap.keySet()) {
            for (URI uriMatch : uriMap.get(uri)) {
                expandedWhereClause.append(" } UNION { ");
//                System.out.println(uri.stringValue());
//                System.out.println(uriMatch.stringValue());
                String equivalentWhereClause = queryWhereClause.replace(uri.stringValue(), uriMatch.stringValue());
//                System.out.println(equivalentWhereClause);
                expandedWhereClause.append(equivalentWhereClause);
            }
        }
        expandedWhereClause.append(" } ");
        return new SPARQLQueryImpl(queryStart + expandedWhereClause.toString() + queryEnd);
    }
    
    /**
     * Expand the supplied query into a set of UNION queries
     * 
     * @param queryStart select clause of the original SPARQL query with WHERE {
     * @param queryFirstBlock first part of the where clause up to where the URI was found
     * @param spList List of equivalent statements
     * @param queryLastBlock any BGP that appear after the URI was expanded
     * @param queryEnd everything from the close of the WHERE clause in the original query
     * @return expanded query
     */
    private SPARQLQuery expandQuery(String queryStart, String queryFirstBlock,
            List<StatementPattern> spList, String queryLastBlock, String queryEnd) {
        logger.debug("Expanding query:");
        StringBuilder queryBuilder = new StringBuilder(queryStart);
        Iterator<StatementPattern> it = spList.iterator();
        while (it.hasNext()) {
            StatementPattern sp = it.next();
            queryBuilder.append(" { ");
            queryBuilder.append(queryFirstBlock);
            queryBuilder.append(statementPatternAsString(sp));
            queryBuilder.append(queryLastBlock);
            queryBuilder.append(" } ");
            if (it.hasNext()) {
                queryBuilder.append(" UNION ");
            }
        }
        queryBuilder.append(queryEnd);
        if (logger.isDebugEnabled()) {
            logger.debug("Expanded query: " + queryBuilder.toString());
        }
        return new SPARQLQueryImpl(queryBuilder.toString());
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
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand"));
        s.initialiseInternal(null);
        String qStr = " SELECT ?protein"
                + " WHERE {"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + " <http://brenda-enzymes.info/1.1.1.1> . "
//                + " ?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
//                + " <http://rdf.chemspider.com/37> ?p ?o ."
                + "}";

        System.out.println("Original query:\n\t" + qStr + "\n");
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        System.out.println("Expanded query:\n\t" + eQuery);
    }

}
