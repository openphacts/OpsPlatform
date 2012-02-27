package eu.ops.plugin.imssparqlexpand;

import eu.ops.plugin.irssparqlexpand.version1.URIFinderVisitor;
import eu.larkc.core.data.DataSet;
import eu.larkc.core.data.RdfGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelTreePrinter;
import org.openrdf.query.impl.DatasetImpl;
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
    
    /**
     * Compares two Datasets. Allows for both to be null otherwise uses Equals();
     * <p>
     * If used to confirm two datasets are not equal, the suggestion is to set verbose to false.
     * 
     * @param dataset1
     * @param dataset2
     * @param verbose If true outputs messages if the datasets are not equal.
     * @return True if both Datasets are nulls or dataset1.equals(dataset2), otherwise false.
     */
    public static boolean compare(Dataset  dataset1, Dataset  dataset2, boolean verbose) {
        if (dataset1 == null){
            if (dataset2 == null){
                return true;
            } else {
                if (verbose){
                    System.out.println("Dataset 1 is null while Dataset 2 is:");
                    System.out.println(dataset2);
                }
                return false;
            }         
        } else {
            if (dataset2 == null){
                if (verbose){
                    System.out.println("Dataset 2 is null while Dataset 1 is:");
                    System.out.println(dataset1);
                }
                return false;
            }         
        }
        Set<URI> defaultGraphs1 = dataset1.getDefaultGraphs();
        Set<URI> defaultGraphs2 = dataset2.getDefaultGraphs();
        //ystem.out.println("defaultGraphs");
        //ystem.out.println(defaultGraphs1);
        //ystem.out.println(defaultGraphs2);
        if (!(defaultGraphs1.equals(defaultGraphs2))){
            if (verbose){
               System.out.println("*** defaultGraphs do not match ***");
            }
            return false;
        }
        Set<URI> namedGraphs1 = dataset1.getNamedGraphs();
        Set<URI> namedGraphs2 = dataset2.getNamedGraphs();
        //ystem.out.println("namedGraphs");
        //ystem.out.println(namedGraphs1);
        //ystem.out.println(namedGraphs2);
        if (!(namedGraphs1.equals(namedGraphs2))){
            if (verbose){
                System.out.println("*** namedGraphs do not match ***");
            }
            return false;
        }
        return true;
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
     * @param verbose If true outputs messages if the datasets are not equal.
     * @return True if and only if the two queries generate equals TupleExpr. 
     * @throws MalformedQueryException 
     */
    public static boolean sameTupleExpr(String query1, String query2, boolean verbose) throws MalformedQueryException{
        ParsedQuery parsedQuery1 = parser.parseQuery(query1, null); 
        TupleExpr tupleExpr1 =  parsedQuery1.getTupleExpr();
        ParsedQuery parsedQuery2 = parser.parseQuery(query2, null); 
        TupleExpr tupleExpr2 =  parsedQuery2.getTupleExpr();
        //if (compare(tupleExpr1, tupleExpr2)){
        if ((tupleExpr1.equals(tupleExpr2))){
            Dataset  dataset1 = parsedQuery1.getDataset();
            Dataset  dataset2 = parsedQuery2.getDataset();
            return compare(dataset1, dataset2, verbose);
        } else {
            if (verbose){
                System.out.println("*** Queries do not match ***");
                System.out.println(query1);
                System.out.println(QueryModelTreePrinter.printTree(tupleExpr1));
                System.out.println("*");
                System.out.println(query2);
                System.out.println(QueryModelTreePrinter.printTree(tupleExpr2));
            }
            return false;
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
     * @param option2 A Sparql query as a String
     * @param option2 Another Sparql query as a String
     * @param verbose If true outputs messages if the datasets are not equal.
     * @return True if and only if the query generate a TupleExpr equal to one of the two options. 
     * @throws MalformedQueryException 
     */
    public static boolean sameTupleExpr(String query, String option1, String option2, boolean verbose) throws MalformedQueryException{
        ParsedQuery parsedQuery = parser.parseQuery(query, null); 
        TupleExpr tupleExpr =  parsedQuery.getTupleExpr();
        ParsedQuery parsedQuery1 = parser.parseQuery(option1, null); 
        TupleExpr tupleExpr1 =  parsedQuery1.getTupleExpr();
        if (tupleExpr1.equals(tupleExpr)){
            Dataset  dataset1 = parsedQuery.getDataset();
            Dataset  dataset2 = parsedQuery1.getDataset();
            return compare(dataset1, dataset2, verbose);
        }
        ParsedQuery parsedQuery2 = parser.parseQuery(option2, null); 
        TupleExpr tupleExpr2 =  parsedQuery2.getTupleExpr();
        //if (compare(tupleExpr1, tupleExpr2)){
        if ((tupleExpr2.equals(tupleExpr))){
            Dataset  dataset1 = parsedQuery1.getDataset();
            Dataset  dataset2 = parsedQuery2.getDataset();
            return compare(dataset1, dataset2, verbose);
        } else {
            if (verbose){
                System.out.println("*** Queries do not match ***");
                System.out.println(query);
                //System.out.println(QueryModelTreePrinter.printTree(tupleExpr));
                System.out.println("*");
                System.out.println(option1);
                System.out.println("nor");
                System.out.println(option2);
                //System.out.println(QueryModelTreePrinter.printTree(tupleExpr1));
            }
            return false;
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
     * @param verbose (ALWAYS TRUE) If true outputs messages if the datasets are not equal.
     * @return True if and only if the two queries generate equals TupleExpr. 
     * @throws MalformedQueryException 
     */
    public static boolean sameTupleExpr(String query1, String query2) throws MalformedQueryException{
        return sameTupleExpr(query1, query2, true);
    }
    
    /**
     * Gets the URIs in the Query
     * 
     * Currently not used but used by version 1
     * 
     * @param query
     * @return
     * @throws MalformedQueryException
     * @throws QueryExpansionException 
     */
    public static Set<URI> getURIS(String query) throws MalformedQueryException, QueryExpansionException{
        TupleExpr tupleExpr = queryStringToTupleExpr(query);
        URIFinderVisitor visitor = new URIFinderVisitor();
        tupleExpr.visit(visitor);
        return visitor.getURIS();
    }

    /**
     * Converts an eu.larkc.core.data.DataSet to an org.openrdf.query.Dataset
     * 
     * Copies both the default graphs and the named graphs.
     * 
     * @param larkcDataset openrdf format dataset org.openrdf.query.Dataset
     * @return larkc fomrat dataset eu.larkc.core.data.DataSet
     */
    public static Dataset convertToOpenRdf (DataSet larkcDataset){
        DatasetImpl openRdfDataSet = new DatasetImpl();
        Set<RdfGraph> rdfGraphs = larkcDataset.getDefaultGraphs();
        for (RdfGraph rdfGraph: rdfGraphs){
            openRdfDataSet.addDefaultGraph(rdfGraph.getName());
        }
        rdfGraphs = larkcDataset.getNamedGraphs();
        for (RdfGraph rdfGraph: rdfGraphs){
            openRdfDataSet.addNamedGraph(rdfGraph.getName());
        }
        return openRdfDataSet;
    }
}
