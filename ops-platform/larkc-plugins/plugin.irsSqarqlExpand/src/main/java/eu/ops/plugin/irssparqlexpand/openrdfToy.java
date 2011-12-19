/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.helpers.QueryModelTreePrinter;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Christian
 */
public class openrdfToy {
     public static void main(String[] args) throws MalformedQueryException, Exception {
         SPARQLParser parser = new SPARQLParser();
         String queryStr0 = " SELECT ?protein ?protein2"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein2 <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
         String queryStr1 = "SELECT ?protein"
                + " WHERE {"
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1>)} "
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine2 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/2.2.2.2>)} "
                + "}";
         String queryStr2 = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
         String queryStr = "SELECT ?protein ?name "
            + "WHERE { "
            + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . }"
            + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
            + "}";
         ParsedQuery parsedQuery = parser.parseQuery(queryStr, null); 
         TupleExpr tupleExpr = parsedQuery.getTupleExpr();
         System.out.println(tupleExpr);
         QueryWriterModelVisitor myVisitor = new QueryWriterModelVisitor();
         tupleExpr.visit(myVisitor);
         String newQuery = myVisitor.getQuery();
         System.out.println(newQuery);
         ParsedQuery newParsedQuery = parser.parseQuery(newQuery, null); 
         TupleExpr newTupleExpr = newParsedQuery.getTupleExpr();
         if (newTupleExpr.equals(tupleExpr)){
             System.out.println("ok");
         }
     }
}
