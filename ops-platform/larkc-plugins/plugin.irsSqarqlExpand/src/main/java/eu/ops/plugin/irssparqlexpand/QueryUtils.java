package eu.ops.plugin.irssparqlexpand;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 * Provides a few static Utility methods.
 * 
 * @author Christian
 */
public class QueryUtils {
   
    static SPARQLParser parser = new SPARQLParser();
    
    /**
     * Convenience method for converting a String into a Tuple Expresions.
     * 
     * Based purely on openrdf code.
     * 
     * @param querySting text of query to parse.
     * @return Tuple Expression representation of this query 
     * @throws MalformedQueryException If openrdf can not read the query.
     */
    public static TupleExpr queryStringToTupleExpr(String querySting) throws MalformedQueryException{                 
        ParsedQuery parsedQuery = parser.parseQuery(querySting, null); 
        return parsedQuery.getTupleExpr();
    }
    
    public static String tupleExprToQueryString (TupleExpr tupleExpr) throws UnexpectedQueryException{
        QueryWriterModelVisitor queryWriter = new QueryWriterModelVisitor();
        try {
            tupleExpr.visit(queryWriter);
            String newQuery = queryWriter.getQuery();
            return newQuery; 
        } catch (UnexpectedQueryException ex){
            throw ex;
        } catch (Exception ex) {
            throw new UnexpectedQueryException("Exception converting TupleExpr to String", ex);
        }
    }
    
    /**
     * Compares two queryStrings to see if they generate the same TupleExpr.
     * <p>
     * This allows query Strings that differ only on whitespacing to be considered equal.
     * <p>
     * It may also allow some queries with statments in a slightly different order to be considered equal,
     *    but only if the openrdf parse would switch the order in one of the queries.
     * <p>
     * However a false does not mean that the queries can not be semantically equivellant.
     * <p>
     * Based on the implementation of TupleExpr's and its Children's Equals methods, 
     * so supports query not yet convertable from tupleExpr to string.
     * 
     * @param query1 A Sparql query as a String
     * @param query2 Another Sparql query as a String
     * @return True if and only if the two queries generate equals TupleExpr. 
     * @throws MalformedQueryException 
     */
    public static boolean sameTupleExpr(String query1, String query2) throws MalformedQueryException{
        TupleExpr tupleExpr1 = queryStringToTupleExpr(query1);
        TupleExpr tupleExpr2 = queryStringToTupleExpr(query2);
        return tupleExpr1.equals(tupleExpr2);
    }
}
