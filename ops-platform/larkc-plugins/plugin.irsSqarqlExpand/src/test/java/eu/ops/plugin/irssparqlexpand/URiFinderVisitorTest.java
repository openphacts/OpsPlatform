package eu.ops.plugin.irssparqlexpand;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;

/**
 *
 * @author Christian
 */
public class URiFinderVisitorTest {

    public static ValueFactory factory;
    public static URIFinderVisitor visitor;
    
    public URiFinderVisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = ValueFactoryImpl.getInstance();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        visitor = new URIFinderVisitor();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_ONE_BGP_OBJECT_WITH_FILTER_QUERY() throws MalformedQueryException, QueryModelExpanderException{
        String query = QueryModelExpanderTest.ONE_BGP_OBJECT_WITH_FILTER_QUERY;
        HashSet<URI> expected = new HashSet<URI>();
        expected.add(factory.createURI("http://brenda-enzymes.info/1.1.1.1"));
        expected.add(factory.createURI("http://something.org"));
        
        TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(query);
        tupleExpr.visit(visitor);
        Set<URI> results = visitor.getURIS();
        assertEquals(results, expected);
    }

}
