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
                + "FILTER (?objectUriLine = <http://bar.com/8hd83> || "
                + "?objectUriLine2 = <http://foo.info/2.2.2.2>)} "
                + "}";
         String queryStr2 = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
         String queryStr3 = "SELECT ?protein ?name "
                + "WHERE { "
                + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . }"
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
         String queryStr4 = "SELECT ?protein"
                + " WHERE {"
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1> || "
                + "(?objectUriLine1 = <http://bar.com/8hd83> && "
                + "?objectUriLine1 = <http://foo.info/2.2.2.2>))} "
                + "}";
         String queryStr5 = "SELECT  ?protein"
                + "{"
                + "FILTER (( ?protein = <http://www.another.org> ||  ?protein = <http://something.org>))"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 ."
                + " FILTER (?objectUri1 = <http://example.com/983juy> || ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>)"
                +" }";
         String queryStr6 = "SELECT ?stuff ?protein "
            + "WHERE {"
            + "?stuff <http://www.foo.com/predicate> ?protein . "
            + "FILTER (?stuff = <http://brenda-enzymes.info/1.1.1.1> || <http://Fishlink/123> = ?stuff)"
            + "}";
         String queryStr7 = "SELECT ?stuff ?protein WHERE {FILTER ((?stuff = <http://example.com/983juy> || ?stuff = <http://manchester.com/983juy> ||?stuff = <http://brenda-enzymes.info/1.1.1.1>) ||?stuff = (<http://Fishlink/456> || ?stuff = <http://Fishlink/123>))?stuff <http://www.foo.com/predicate> ?protein . }";
         String queryStr = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "PREFIX org:    <http://example.com/ns#>"
            + "CONSTRUCT { ?x foaf:name ?name }"
            + "WHERE  { ?x org:employeeName ?name }";
         
         String queryStr9 = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title"
            + "WHERE   { ?x dc:title ?title"
            + "          FILTER regex(?title, \"web\", \"i\" ) " 
            + "        }";
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
