/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import org.junit.Ignore;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class QueryWriterModelVisitorTest {
    
    public QueryWriterModelVisitorTest() {
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
    
    private void convertAndTest(String query) throws MalformedQueryException, QueryExpansionException{
        TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(query);
        String newQuery = QueryUtils.tupleExprToQueryString(tupleExpr);
        assertTrue(QueryUtils.sameTupleExpr(query, newQuery));
    }
    
    @Test
    public void test_firstTest() throws MalformedQueryException, QueryExpansionException{
       String queryStr = " SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
       convertAndTest(queryStr);
    }
    
    @Test
    @Ignore
    public void test_ASK_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.ASK_QUERY);

    }
    
    @Test
    public void test_COMPLEX_CHAIN_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.COMPLEX_CHAIN_QUERY);

    }
    
    @Test
    @Ignore //order is different
    public void test_COMPLEX_CHAIN_QUERY_PLUS_FILTER() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.COMPLEX_CHAIN_QUERY_EXPECTED);

    }
    
    @Test
    @Ignore
    public void test_CONSTRUCT_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.CONSTRUCT_QUERY);

    }
    @Test
    @Ignore
    public void test_DESSCRIBE_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.DESSCRIBE_QUERY);

    }
    @Test
    public void test_MINIMAL_SPACING_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.MINIMAL_SPACING_QUERY);

    }
    @Test
    public void test_NO_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.NO_URI_QUERY);

    }
    
    @Test
    public void test_ONLY_OPTIONAL_STATEMENTS_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.ONLY_OPTIONAL_STATEMENTS_QUERY);
    }
    
    @Test
    public void test_ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED);
    }

    @Test
    public void test_ONE_BGP_OBJECT_WITH_FILTER_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(QueryUtilsTest.ONE_BGP_OBJECT_WITH_FILTER_QUERY);
    }

    static String ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "}";

    static String ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_EXPECTED =  "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "?objectUri1 . "
                + "FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) . "
                + "}";                       
     
    
    @Test
    public void test_ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY);
    }

    @Test
    public void test_ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_PLUS_FILTER() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_EXPECTED);
    }

    @Test
    public void test_PREFIX_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.PREFIX_QUERY);
    }

    @Test
    @Ignore //Repeated version gives same constant names. Writer version gives different constant names.
    public void test_REPEATED_SUBJECT_PREDICATE_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_PREDICATE_QUERY);
    }
    @Test
    @Ignore //different order
    public void test_REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED);

    }
    @Test
    public void test_REPEATED_SUBJECT_SHORTHAND_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_SHORTHAND_QUERY);

    }
    @Test
    @Ignore //different order
    public void test_REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED);

    }
    @Test
    public void test_SHARED2_SUBJECT_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SHARED2_SUBJECT_URI_QUERY);

    }
    @Test
    @Ignore //Filters in different order
    public void test_SHARED2_SUBJECT_URI_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SHARED2_SUBJECT_URI_QUERY_EXPECTED);

    }
    @Test
    public void test_SHARED3_SUBJECT_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SHARED3_SUBJECT_URI_QUERY);

    }
    @Test
    @Ignore //Different order.
    public void test_SHARED3_SUBJECT_URI_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SHARED3_SUBJECT_URI_QUERY_EXPECTED);

    }
    @Test
    public void test_SIMPLE_CHAIN_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_CHAIN_QUERY);

    }
    @Test
    @Ignore //Different order
    public void test_SIMPLE_CHAIN_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_CHAIN_QUERY_EXPECTED);

    }
    @Test
    public void test_SIMPLE_OPTIONAL_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_OPTIONAL_QUERY);

    }
    @Test
    public void test_SIMPLE_OPTIONAL_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_OPTIONAL_QUERY_EXPECTED);

    }
    @Test
    public void test_SINGLE_BOTH_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY);

    }
    @Test
    @Ignore  //Different order
    public void test_SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES);

    }
    @Test
    @Ignore //Different order
    public void test_SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH);

    }
    @Test
    public void test_SINGLE_OBJECT_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_OBJECT_URI_QUERY);

    }
    @Test
    public void test_SINGLE_OBJECT_URI_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_OBJECT_URI_QUERY_EXPECTED);

    }
    @Test
    public void test_SINGLE_SUBJECT_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_SUBJECT_URI_QUERY);

    }
    @Test
    public void test_SINGLE_SUBJECT_URI_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_SUBJECT_URI_QUERY_EXPECTED);

    }
    @Test
    public void test_TWO_STATEMENTS_ONE_OBJECT_URI_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.TWO_STATEMENTS_ONE_OBJECT_URI_QUERY);

    }

    @Test
    public void test_TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(IRSSPARQLExpandTest.TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED);
    } 
    
    String AND_QUERY = "SELECT ?protein"
                + " WHERE {"
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1> || "
                + "(?objectUriLine1 = <http://bar.com/8hd83> && "
                + "?objectUriLine1 = <http://foo.info/2.2.2.2>))} "
                + "}";

    @Test
    public void test_AND_QUERY() throws MalformedQueryException, QueryExpansionException{
        convertAndTest(AND_QUERY);
    } 
    
    /**
     * Test to see it can correcly handle many anonymois vaiables,
     * Should be able to do over 600.
     * @throws MalformedQueryException
     * @throws QueryExpansionException 
     */
    @Test
    public void test_ManyAnons() throws MalformedQueryException, QueryExpansionException{
        String inputquery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name"
                + "WHERE  { "
                + "[] foaf:name ?name ."
                + "[] foaf:name2 ?name ."
                + "[] foaf:name3 ?name ."
                + "[] foaf:name4 ?name ."
                + "[] foaf:name5 ?name ."
                + "[] foaf:name6 ?name ."
                + "[] foaf:name7 ?name ."
                + "[] foaf:name8 ?name ."
                + "[] foaf:name9 ?name ."
                + "[] foaf:name10 ?name ."
                + "[] foaf:name11 ?name ."
                + "[] foaf:name12 ?name ."
                + "[] foaf:name13 ?name ."
                + "[] foaf:name14 ?name ."
                + "[] foaf:name15 ?name ."
                + "[] foaf:name16 ?name ."
                + "[] foaf:name17 ?name ."
                + "[] foaf:name18 ?name ."
                + "[] foaf:name19 ?name ."
                + "[] foaf:name20 ?name ."
                + "[] foaf:name21 ?name ."
                + "[] foaf:name22 ?name ."
                + "[] foaf:name23 ?name ."
                + "[] foaf:name24 ?name ."
                + "[] foaf:name25 ?name ."
                + "[] foaf:name26 ?name ."
                + "[] foaf:name27 ?name ."
                + "[] foaf:name28 ?name ."
                + "[] foaf:name29 ?name ."
                + "[] foaf:name30 ?name ."
                + "[] foaf:name31 ?name ."
                + "[] foaf:name32 ?name ."
                + "[] foaf:name33 ?name ."
                + "[] foaf:name34 ?name ."
                + "[] foaf:name35 ?name ."
                + "[] foaf:name36 ?name ."
                + "[] foaf:name37 ?name ."
                + "[] foaf:name38 ?name ."
                + "[] foaf:name39 ?name ."
                + "[] foaf:name40 ?name ;"
                + "   foaf:name41 ?name ."
                + "[] foaf:name42 ?name ;"
                + "   foaf:name43 ?name ."
                + "[] foaf:name44 ?name ;"
                + "   foaf:name45 ?name ;"
                + "   foaf:name46 ?name ;"
                + "   foaf:name47 ?name ."
                + "[] foaf:name46 ?name ;"
                + "   foaf:name47 ?name ."
                + "[] foaf:name48 ?name ;"
                + "   foaf:name49 ?name ."
                + "[] foaf:name50 ?name ."
                + "[] foaf:name51 ?name ."
                + "[] foaf:name52 ?name ."
                + "[] foaf:name53 ?name ."
                + "[] foaf:name54 ?name ."
                + "[] foaf:name55 ?name ."
                + "[] foaf:name56 ?name ."
                + "[] foaf:name57 ?name ."
                + "[] foaf:name58 ?name ."
                + "[] foaf:name59 ?name ."
                + "}";
        convertAndTest(inputquery);
    } 
    
    
}
