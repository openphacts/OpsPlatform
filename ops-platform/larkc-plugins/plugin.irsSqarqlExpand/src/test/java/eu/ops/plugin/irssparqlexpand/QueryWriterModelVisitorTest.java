/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

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
}
