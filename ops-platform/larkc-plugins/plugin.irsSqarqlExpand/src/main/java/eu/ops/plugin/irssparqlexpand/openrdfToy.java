/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Christian
 */
public class openrdfToy {
     public static void expanderTest(String inputQuery, String expectedQuery) throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
         SetOfStatements larkc = new SPARQLQueryImpl(inputQuery).toRDF();
         SPARQLQuery larkcQuery = DataFactory.INSTANCE.createSPARQLQuery(larkc);
         String larkcString = larkcQuery.toString();
         if (QueryUtils.sameTupleExpr(inputQuery,  larkcString)) {
             System.out.println("larkc OK");
         }
         if (larkcQuery instanceof SPARQLQueryImpl){
            SPARQLQueryImpl impl = (SPARQLQueryImpl)larkcQuery;
            ParsedQuery larkcParsedQuery = impl.getParsedQuery();
            Dataset larkcDataset = larkcParsedQuery.getDataset();
            System.out.println(larkcDataset);
         }
         final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
         IRSSPARQLExpand1 expander = 
                new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
         };
         expander.initialiseInternal(null);
         SetOfStatements newLarkc = expander.invokeInternalWithExceptions(larkc);
         SPARQLQuery expandedQuery = DataFactory.INSTANCE.createSPARQLQuery(newLarkc);
         larkcString = expandedQuery.toString();
         System.out.println("expanded query");
         System.out.println(larkcString);
         if (QueryUtils.sameTupleExpr(expectedQuery,  larkcString)) {
             System.out.println("expanded OK");
         }         
     }

     public static void main(String[] args) throws MalformedQueryException, Exception {
         SPARQLParser parser = new SPARQLParser();
         String queryStr0 = " SELECT  DISTINCT ?protein ?protein2"
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
         String queryStr4 = "SELECT ?protein "
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
         String queryStr8 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "PREFIX org:    <http://example.com/ns#>"
            + "CONSTRUCT { ?x foaf:name ?name } "
            + "WHERE  { ?x org:employeeName ?name }";
         
         String queryStr9 = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title "
            + "WHERE   { ?x dc:title ?title "
            + "          FILTER regex(?title, \"web\", \"i\" ) " 
            + "        }";
         String queryStr10 = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "PREFIX  ns:  <http://example.org/ns#>"
            + "SELECT  ?title ?price "
            + "WHERE   { ?x dc:title ?title ."
            + "          OPTIONAL { ?x ns:price ?price . FILTER (?price < 30) }"
            + "        }";
         String queryStr11 = "PREFIX dc10:  <http://purl.org/dc/elements/1.0/>"
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT ?title "
            + "WHERE  { { ?book dc10:title  ?title } UNION { ?book dc11:title  ?title } }";
         String queryStrTooHard12 = "PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?person "
            + "WHERE "
            + "{"
            + "    ?person rdf:type  foaf:Person ."
            + "    FILTER EXISTS {?person foaf:name ?name }"
            + "}     ";
         String queryStrTooHard13 = "PREFIX :       <http://example/>" 
            + "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT DISTINCT ?s "
            + "WHERE { "
            + "   ?s ?p ?o ."
            + "   MINUS {"
            + "      ?s foaf:givenName \"Bob\" ."
            + "   } "
            + "}";
         String queryStr14 = "SELECT * "
            + "WHERE { "
            + "   ?s ?p ?o ."
            + "}";
         String queryStrTooHard15 = "PREFIX :  <http://books.example/> "
            + "SELECT (SUM(?lprice) AS ?totalPrice) "
            + "WHERE { "
            + "  ?org :affiliates ?auth . "
            + "  ?auth :writesBook ?book . "
            + "  ?book :price ?lprice . "
            + "} "
            + "GROUP BY ?org "
            + "HAVING (SUM(?lprice) > 10) ";
         String queryStr16 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT  ?name "
            + "FROM    <http://example.org/foaf/aliceFoaf> "
            + "WHERE   { ?x foaf:name ?name }";
         String queryStr17 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?who ?g ?mbox "
            + "FROM <http://example.org/dft.ttl> "
            + "FROM NAMED <http://example.org/alice> "
            + "FROM NAMED <http://example.org/bob> "
            + "WHERE { "
            + "    ?g dc:publisher ?who ."
            + "    GRAPH ?g { ?x foaf:mbox ?mbox. }"
            + "}";
         String queryStr18 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?src ?bobNick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "    GRAPH ?src "
            + "    { ?x foaf:mbox <mailto:bob@work.example> ."
            + "      ?x foaf:nick ?bobNick "
            + "    }"
            + "  }";
        String queryStr ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX data: <http://example.org/foaf/> "
            + "SELECT ?nick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "     GRAPH data:bobFoaf {"
            + "         ?x foaf:mbox \"mailto:bob@work.example\" ."
            + "         ?x foaf:nick ?nick }"
            + "  }";

         ParsedQuery parsedQuery = parser.parseQuery(queryStr, null); 
         TupleExpr tupleExpr = parsedQuery.getTupleExpr();
         Dataset dataset = parsedQuery.getDataset();
         System.out.println(tupleExpr);
         QueryWriterModelVisitor myVisitor = new QueryWriterModelVisitor(dataset);
         tupleExpr.visit(myVisitor);
         String newQuery = myVisitor.getQuery();
         System.out.println(newQuery);
         ParsedQuery newParsedQuery = parser.parseQuery(newQuery, null); 
         if (QueryUtils.sameTupleExpr(queryStr,  newQuery)) {
             System.out.println("ok");
         }
         //expanderTest(queryStr, queryStr);
     }
     
     
}
