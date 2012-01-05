/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import java.util.Set;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.URI;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.query.MalformedQueryException;

/**
 *
 * @author Christian
 */
public class QueryUtilsTest {
    
    public QueryUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * No idea how to test this as I don't know how to build a TupleExpr manually.
     * On the other hand it relies on only highly used thirdParty code.
     * /
    @Test
    public void testQueryStringToTupleExpr() throws Exception {
        System.out.println("queryStringToTupleExpr");
        String querySting = "";
        TupleExpr expResult = null;
        TupleExpr result = QueryUtils.queryStringToTupleExpr(querySting);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * No idea how to test this as I don't know how to build a TupleExpr manually.
     * On the other hand it relies on only highly used thirdParty code.
     * /
    @Test
    public void testTupleExprToQueryString() throws Exception {
        System.out.println("tupleExprToQueryString");
        TupleExpr tupleExpr = null;
        String expResult = "";
        String result = QueryUtils.tupleExprToQueryString(tupleExpr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of sameTupleExpr method, of class QueryUtils.
     * Using the exact same query
     */
    @Test
    public void testSameTupleExpr_sameQuery() throws Exception {
        System.out.println("sameTupleExpr samequery");
         String query1 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
         String query2 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
        boolean result = QueryUtils.sameTupleExpr(query1, query2);
        assertTrue(result);
    }

    /**
     * Test of sameTupleExpr method, of class QueryUtils.
     * Using queries that differ only on whitespace.
     */
    @Test
    public void testSameTupleExpr_differentWhiteSpaceQuery() throws Exception {
        System.out.println("sameTupleExpr different whiteSpace");
         String query1 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
         String query2 = "SELECT ?protein"
                + "     WHERE{"
                + "?protein   <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1>."
                + "   ?protein     <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name. "
                + "<http://rdf.chemspider.com/37> ?p      ?o ."
                + "       }";
        boolean result = QueryUtils.sameTupleExpr(query1, query2);
        assertTrue(result);
    }

    /**
     * Test of sameTupleExpr method, of class QueryUtils.
     * Using queries with a different uri.
     */
    @Test
    public void testSameTupleExpr_differentURI() throws Exception {
        System.out.println("sameTupleExpr different uri");
         String query1 = " SELECT ?protein"
                + " WHERE{"
                + "?protein  <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> ."
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37>  ?p  ?o ."
                + "}";
         String query2 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/2.2.2.2> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
        assertFalse(QueryUtils.sameTupleExpr(query1, query2, false));
    }

    /**
     * Test of sameTupleExpr method, of class QueryUtils.
     * Using semantically the same query but with staements in a different order.
     * 
     * This test is commented out because it does not matter if the two queries are considered equal or not.
     * But it does show that !tupleExpr1.equals(tupleExpr2) is not a guarantee that the two are not sematically equivellant. 
     * /
    @Test
    public void testSameTupleExpr_differentOrder() throws Exception {
        System.out.println("sameTupleExpr diiferentOrder");
         String query1 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
         String query2 = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "}";
        boolean result = QueryUtils.sameTupleExpr(query1, query2);
        assertFalse(result);
    }*/
    
    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "FILTER (?protein = <http://something.org>) . "
                + "}"; 
    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY_EXPECTED = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "?objectUri1 . "
                + "FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) . "
                + "FILTER (?protein = <http://something.org>) . "
                + "}";
 
    @Test
    public void test_getURIS() throws MalformedQueryException, QueryExpansionException {
        String query = ONE_BGP_OBJECT_WITH_FILTER_QUERY;
        HashSet<URI> expected = new HashSet<URI>();
        ValueFactory factory = ValueFactoryImpl.getInstance();
        expected.add(factory.createURI("http://brenda-enzymes.info/1.1.1.1"));
        expected.add(factory.createURI("http://something.org"));
        Set<URI> results = QueryUtils.getURIS(query);
        assertEquals(results, expected);
    }
}
