package eu.ops.plugin.irssparqlexpand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.irs.IRS;
import uk.ac.manchester.cs.irs.IRSException;
import uk.ac.manchester.cs.irs.IRSImpl;
import uk.ac.manchester.cs.irs.beans.Match;

/**
 * The <code>eu.ops.plugin.irssparqlexpand.IRSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IRS service.
 */
public class IRSSPARQLExpand extends Plugin {

    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);
    private IRS irsHandle = null;

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
        try {
            irsHandle = instantiateIRS();
        } catch (IRSException ex) {
            System.err.println("Could not instantiate IRS.");
            logger.error("Could not instantiate IRS.");
        }
        logger.info("IRSSPARQLExpand initialized.");
    }
    
    protected IRS instantiateIRS() throws IRSException {
            return new IRSImpl();
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
        List<Match> matches;
        List<StatementPattern> spList = new ArrayList<StatementPattern>();
        boolean found;
        String queryFirstBlock = "";

        if (query instanceof SPARQLQuery) {
            String queryString = query.toString();
            String queryStart = queryString.substring(0, queryString.indexOf("{")+1);
            String queryEnd = queryString.substring(queryString.lastIndexOf("}")-1, queryString.length());

            StatementPatternCollector spc = new StatementPatternCollector();
            ((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr().visit(spc);
            StringBuilder queryBuilder = new StringBuilder();            
            found = false;
            for (StatementPattern sp : spc.getStatementPatterns()) {
                String subject = varAsString(sp.getSubjectVar());
                String predicate = varAsString(sp.getPredicateVar());
                
                Value o = (Value) sp.getObjectVar().getValue();
                try {
                    if (o instanceof URI) {
                        matches = irsHandle.getMappingsWithURI(o.stringValue(), null, null);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Number of matches for " + o.stringValue() + 
                                    " = " + matches.size());
                        }
                        spList = expandObjectURI(sp, matches);
                        found = true;
                    }
                } catch (IRSException ex) {
                    logger.warn("Unable to retrieve mappings.", ex);
                }
                if (found) {
                    queryFirstBlock = queryBuilder.toString();
                    queryBuilder = new StringBuilder();
                    found = false;
                } else {
                    queryBuilder.append(subject).append(" ");
                    queryBuilder.append(predicate).append(" ");
                    queryBuilder.append(varAsString(sp.getObjectVar())).append(" . ");
                }
            }
            SPARQLQuery expandedQuery = expandQuery(queryStart, queryFirstBlock, 
                    spList, queryBuilder.toString(), queryEnd);
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
     * @param query original SPARQL query
     * @param uriMappings map of equivalent URIs for each URI in the query
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
                + " <http://rdf.chemspider.com/37> ?p ?o ."
                + "}";

        System.out.println("Original query:\n\t" + qStr + "\n");
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        System.out.println("Expanded query:\n\t" + eQuery);
    }

}
