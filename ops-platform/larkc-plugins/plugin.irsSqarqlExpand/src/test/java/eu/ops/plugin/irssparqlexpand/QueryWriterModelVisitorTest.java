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
    
    private void convertAndTest(String query) throws MalformedQueryException, UnexpectedQueryException{
        TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(query);
        String newQuery = QueryUtils.tupleExprToQueryString(tupleExpr);
        assertTrue(QueryUtils.sameTupleExpr(query, query));
    }
    
    @Test
    public void test_firstTest() throws MalformedQueryException, UnexpectedQueryException{
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
    public void test_ASK_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.ASK_QUERY);

    }
    
    @Test
    public void test_COMPLEX_CHAIN_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.COMPLEX_CHAIN_QUERY);

    }
    
    @Test
    @Ignore
    public void test_COMPLEX_CHAIN_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.COMPLEX_CHAIN_QUERY_PLUS_FILTER);

    }
    
    @Test
    @Ignore
    public void test_CONSTRUCT_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.CONSTRUCT_QUERY);

    }
    @Test
    @Ignore
    public void test_DESSCRIBE_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.DESSCRIBE_QUERY);

    }
    @Test
    public void test_MINIMAL_SPACING_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.MINIMAL_SPACING_QUERY);

    }
    @Test
    public void test_NO_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.NO_URI_QUERY);

    }
    
    @Test
    @Ignore
    public void test_ONE_BGP_OBJECT_WITH_FILTER_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(QueryModelExpanderTest.ONE_BGP_OBJECT_WITH_FILTER_QUERY);
    }

    @Test
    @Ignore
    public void test_ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(QueryModelExpanderTest.ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY);
    }

    @Test
    @Ignore
    public void test_ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(QueryModelExpanderTest.ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_PLUS_FILTER);
    }

    @Test
    @Ignore
    public void test_OPTIONAL_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.OPTIONAL_QUERY);

    }
    @Test
    public void test_PREFIX_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.PREFIX_QUERY);

    }
    @Test
    public void test_REPEATED_SUBJECT_PREDICATE_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_PREDICATE_QUERY);
    }
    @Test
    @Ignore
    public void test_REPEATED_SUBJECT_PREDICATE_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_PREDICATE_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_REPEATED_SUBJECT_SHORTHAND_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_SHORTHAND_QUERY);

    }
    @Test
    @Ignore
    public void test_REPEATED_SUBJECT_SHORTHAND_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.REPEATED_SUBJECT_SHORTHAND_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_SHARED2_SUBJECT_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SHARED2_SUBJECT_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_SHARED2_SUBJECT_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SHARED2_SUBJECT_URI_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_SHARED3_SUBJECT_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SHARED3_SUBJECT_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_SHARED3_SUBJECT_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SHARED3_SUBJECT_URI_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_SIMPLE_CHAIN_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_CHAIN_QUERY);

    }
    @Test
    @Ignore
    public void test_SIMPLE_CHAIN_QUERY_PLUS_FILTERS() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SIMPLE_CHAIN_QUERY_PLUS_FILTERS);

    }
    @Test
    public void test_SINGLE_BOTH_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_SINGLE_BOTH_URI_QUERY_PLUS_4FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY_PLUS_4FILTER);

    }
    @Test
    @Ignore
    public void test_SINGLE_BOTH_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_BOTH_URI_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_SINGLE_OBJECT_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_OBJECT_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_SINGLE_OBJECT_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_OBJECT_URI_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_SINGLE_SUBJECT_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_SUBJECT_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_SINGLE_SUBJECT_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.SINGLE_SUBJECT_URI_QUERY_PLUS_FILTER);

    }
    @Test
    public void test_TWO_STATEMENTS_ONE_OBJECT_URI_QUERY() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.TWO_STATEMENTS_ONE_OBJECT_URI_QUERY);

    }
    @Test
    @Ignore
    public void test_TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_PLUS_FILTER() throws MalformedQueryException, UnexpectedQueryException{
        convertAndTest(IRSSPARQLExpandTest.TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_PLUS_FILTER);

    } 
}
