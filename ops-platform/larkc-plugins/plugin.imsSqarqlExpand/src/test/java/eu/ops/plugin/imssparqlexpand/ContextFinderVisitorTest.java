/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Christian
 */
public class ContextFinderVisitorTest {
    
    static SPARQLParser parser;
    
    public ContextFinderVisitorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        parser = new SPARQLParser();
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
     * Test that a tuple expression with no context retruns null.
     */
    @Test
    public void testNoContext() throws Exception {
       String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT  ?name "
            + "FROM    <http://example.org/foaf/aliceFoaf> "
            + "WHERE   { ?x foaf:name ?name }";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var, null);
    }

    /**
     * Test that a tuple expression with a single variable graph.
     */
    @Test
    public void testSingeGraphSingleLineVariable() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph ?g {?x foaf:name ?name }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var.getName(), "g");
    }

    /**
     * Test that a tuple expression with a shared context.
     */
    @Test
    public void testSharedVariable() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph ?g {"
                + "         ?x foaf:name ?name ."
                + "         ?x foaf:title ?title ."
                + "    }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var.getName(), "g");
    }

    /**
     * Test that a tuple expression with a shared context.
     */
    @Test
    public void testMultipleVariable() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph ?g {?x foaf:name ?name }"
                + "    graph ?g {?x foaf:title ?title }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var.getName(), "g");
    }

    /**
     * Test that a tuple expression with a constant context and nin context statement.
     */
    @Test
    public void testOneConstantContextOneNone() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    {?x foaf:name ?name }"
                + "    graph <http://example.org/mygraph> {?x foaf:title ?title }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertNull(var);
    }

    /**
     * Test that a tuple expression with a single constant graph.
     */
    @Test
    public void testSingeConstantGraphSingleLineVariable() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph <http://example.org/mygraph> {?x foaf:name ?name }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var.getValue().stringValue(), "http://example.org/mygraph");
    }

    /**
     * Test that a tuple expression with a shared constant context.
     */
    @Test
    public void testSharedConstantVariable() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph <http://example.org/mygraph> {"
                + "         ?x foaf:name ?name ."
                + "         ?x foaf:title ?title ."
                + "    }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var.getValue().stringValue(), "http://example.org/mygraph");
    }

    /**
     * Test that a tuple expression with multiple constant context.
     */
    @Test
    public void testMultipleSameConstant() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    graph <http://example.org/mygraph> {?x foaf:name ?name }"
                + "    graph <http://example.org/mygraph> {?x foaf:title ?title }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertNull(var);
    }

    /**
     * Test that a tuple expression with a context and nin context statement.
     */
    @Test
    public void testOneContextOneNone() throws Exception {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT  ?name ?title "
                + "FROM    <http://example.org/foaf/aliceFoaf> "
                + "WHERE   { "
                + "    {?x foaf:name ?name }"
                + "    graph ?g {?x foaf:title ?title }"
                + "}";

        ContextFinderVisitor instance = new ContextFinderVisitor();
        ParsedQuery parsedQuery = parser.parseQuery(inputQuery, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();

        tupleExpr.visit(instance);
        Var var = instance.getContext();
        
        assertEquals(var, null);
    }
}
