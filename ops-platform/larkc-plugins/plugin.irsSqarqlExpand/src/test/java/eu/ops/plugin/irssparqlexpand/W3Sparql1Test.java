package eu.ops.plugin.irssparqlexpand;

import static org.junit.Assert.*;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test all the queries found in http://www.w3.org/TR/2008/REC-rdf-sparql-query-20080115/.
 * <p>
 * Where the w3 page says that various formats are semantic sugar for the same query 
 *    they are all tested against the same expected Query.
 * <p>
 * In each case there will be exactly 2 matches for each URI. Itself plus one more.
 * <p>
 * NOTE: Larkc uses openrdf 2.3.2 so sparql1.1 tests makes no sense here.
 * 
 * @author Christian
 */
public class W3Sparql1Test {
        
    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);

    public W3Sparql1Test() {
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
     * Test query found in Section 2.1
     */
    @Test
    public void test2_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT ?title WHERE {"
                + "<http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title .}";
        String expectedQuery = "SELECT ?title WHERE {"
                + "?subjectUri1 <http://purl.org/dc/elements/1.1/title> ?title ."
                + "FILTER (?subjectUri1 = <http://example.org/book/book1> || ?subjectUri1 = <http://example.org/book/other>)}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/book/book1","http://example.org/book/book1");
        dummyIRSMapper.addMapping("http://example.org/book/book1","http://example.org/book/other");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section 2.2
     */
    @Test
    public void test2_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE"
            + "  { ?x foaf:name ?name ."
            + "    ?x foaf:mbox ?mbox }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }


    /**
     * Test first query found in Section 2.3.1
     */
    @Test
    public void test2_3_1_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT ?v WHERE { ?v ?p \"cat\" }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test second query found in Section 2.3.1
     */
    @Test
    public void test2_3_1_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT ?v WHERE { ?v ?p \"cat\"@en }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section 2.3.2
     */
    @Test
    public void test2_3_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT ?v WHERE { ?v ?p 42 }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section 2.3.3
     */
    @Test
    public void test2_3_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT ?v WHERE { ?v ?p \"abc\"^^<http://example.org/datatype#specialDatatype> }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section 2.4
     */
    @Test
    public void test2_4() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?x ?name"
                + "WHERE  { ?x foaf:name ?name }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section 2.5
     */
    @Test
    public void test2_5() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "PREFIX org:    <http://example.com/ns#>"
            + "CONSTRUCT { ?x foaf:name ?name }"
            + "WHERE  { ?x org:employeeName ?name }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test first query found in Section 3.1
     */
    @Test
    public void test3_1_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title"
            + "WHERE   { ?x dc:title ?title"
            + "          FILTER regex(?title, \"^SPARQL\")" 
            + "        }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test second query found in Section 3.1
     */
    @Test
    public void test3_1_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title"
            + "WHERE   { ?x dc:title ?title"
            + "          FILTER regex(?title, \"web\", \"i\" ) " 
            + "        }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test query found in Section 3.2
     */
    @Test
    public void test3_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "PREFIX  ns:  <http://example.org/ns#>"
            + "SELECT  ?title ?price"
            + "WHERE   { ?x ns:price ?price ."
            + "          FILTER (?price < 30.5)"
            + "          ?x dc:title ?title . }";
        String expectedQuery = inputQuery;

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    String ExpectedQuery4_2 = "PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title"
            + "WHERE   {?subjectUri1 dc:title ?title"
            + "         FILTER (?subjectUri1 = <http://example.org/book/book1> || "
            + "                 ?subjectUri1 = <http://other.com/livre/2345>) } ";
    /**
     * Test first query found in Section 4.2
     * <p>
     * All 4.2 input queries are the same.
     */
    @Test
    public void test4_2_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title"
            + "WHERE   { <http://example.org/book/book1> dc:title ?title } ";
        String expectedQuery = ExpectedQuery4_2;
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test second query found in Section 4.2
     * <p>
     * All 4.2 input queries are the same.
     */
    @Test
    public void test4_2_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
            + "PREFIX  : <http://example.org/book/>"
            + "SELECT  $title"
            + "WHERE   { :book1  dc:title  $title }";
        String expectedQuery = ExpectedQuery4_2;
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test third query found in Section 4.2
     * <p>
     * All 4.2 input queries are the same.
     */
    @Test
    public void test4_2_c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "BASE    <http://example.org/book/>"
            + "PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
            + "SELECT  $title"
            + "WHERE   { <book1>  dc:title  ?title }";
        String expectedQuery = ExpectedQuery4_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIRSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    private String query5_2 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  {"
            + "          ?x foaf:name ?name ."
            + "          ?x foaf:mbox ?mbox ."
            + "       }";
    
    /**
     * Test first query found in Section 5.2
     * <p>
     * All 5.2 input queries are the same.
     */
    @Test
    public void test5_2_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = query5_2;
        String expectedQuery = query5_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test second query found in Section 5.2
     * <p>
     * All 5.2 input queries are the same.
     */
    @Test
    public void test5_2_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  { { ?x foaf:name ?name . }"
            + "         { ?x foaf:mbox ?mbox . }"
            + "       }";
        String expectedQuery = query5_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    private String query5_2_2 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  {"
            + "          ?x foaf:name ?name ."
            + "          ?x foaf:mbox ?mbox ."
            + "          FILTER regex(?name, \"Smith\")"
            + "       }";

    /**
     * Test first query found in Section 5.2.2
     * <p>
     * All 5.2.2 input queries are the same.
     * The openRDF parser pushes filters to the top no matter where they are in the query.
     */
    @Test
    public void test5_2_2_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = query5_2_2;
        String expectedQuery = query5_2_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test second query found in Section 5.2.2
     * <p>
     * All 5.2.2 input queries are the same.
     * The openRDF parser pushes filters to the top no matter where they are in the query.
     */
    @Test
    public void test5_2_2_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  {"
            + "          FILTER regex(?name, \"Smith\")"
            + "          ?x foaf:name ?name ."
            + "          ?x foaf:mbox ?mbox ."
            + "       }";
        String expectedQuery = query5_2_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * Test third query found in Section 5.2.2
     * <p>
     * All 5.2.2 input queries are the same.
     * The openRDF parser pushes filters to the top no matter where they are in the query.
     */
    @Test
    public void test5_2_2_c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  {"
            + "          ?x foaf:name ?name ."
            + "          FILTER regex(?name, \"Smith\")"
            + "          ?x foaf:mbox ?mbox ."
            + "       }";
        String expectedQuery = query5_2_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    /**
     * This tests to show that bracketing does not keeps filters more local
     * <p>
     * All 5.2.2 input queries are the same.
     * The openRDF parser pushes filters to the top no matter where they are in the query.
     */
    @Test
    public void test5_2_2_d() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  {"
            + "          ?x foaf:name ?name ."
            + "          { FILTER regex(?name, \"Smith\")"
            + "          ?x foaf:mbox ?mbox .}"
            + "       }";
        String expectedQuery = query5_2_2;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 6.1
     */
    @Test
    public void test6_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox"
            + "WHERE  { ?x foaf:name  ?name ."
            + "         OPTIONAL { ?x  foaf:mbox  ?mbox }"
            + "       }";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 6.2
     */
    @Test
    public void test6_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "PREFIX  ns:  <http://example.org/ns#>"
            + "SELECT  ?title ?price "
            + "WHERE   { ?x dc:title ?title ."
            + "          OPTIONAL { ?x ns:price ?price . FILTER (?price < 30) }"
            + "        }";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 6.3
     */
    @Test
    public void test6_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name ?mbox ?hpage"
            + "WHERE  { ?x foaf:name  ?name ."
            + "         OPTIONAL { ?x foaf:mbox ?mbox } ."
            + "         OPTIONAL { ?x foaf:homepage ?hpage }"
            + "       }";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the first query found in Section 7
     */
    @Test
    public void test7_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX dc10:  <http://purl.org/dc/elements/1.0/> "
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?title "
            + "WHERE  { { ?book dc10:title  ?title } UNION { ?book dc11:title  ?title } } ";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 7
     */
    @Test
    public void test7_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX dc10:  <http://purl.org/dc/elements/1.0/> "
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?x ?y "
            + "WHERE  { { ?x dc10:title  ?title } UNION { ?y dc11:title  ?title } } ";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8.2.1
     */
    @Test
    public void test8_2_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT  ?name "
            + "FROM    <http://example.org/foaf/aliceFoaf> "
            + "WHERE   { ?x foaf:name ?name }";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_2_3
     */
    @Test
    public void test8_2_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?who ?g ?mbox "
            + "FROM <http://example.org/dft.ttl> "
            + "FROM NAMED <http://example.org/alice> "
            + "FROM NAMED <http://example.org/bob> "
            + "WHERE { "
            + "    ?g dc:publisher ?who ."
            + "    GRAPH ?g { ?x foaf:mbox ?mbox. }"
            + "}";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_1 simplified
     */
    @Test
    public void test8_3_1_simple() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?src ?bobNick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "    GRAPH ?src "
            + "    { ?x foaf:mbox \"bob@work.example\" ."
            + "      ?x foaf:nick ?bobNick "
            + "    }"
            + "  }";
        String expectedQuery = inputQuery;
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_1
     */
    @Test
    public void test8_3_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
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
        String expectedQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?src ?bobNick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "    GRAPH ?src {"
            + "       ?x foaf:mbox ?objectUri1 ."
            + "           FILTER (?objectUri1 = <mailto:bob@work.example> || "
            + "                   ?objectUri1 = <mailto:bob.smith@work.example>) . "
            + "       ?x foaf:nick ?bobNick "
            + "    }"
            + "  }";
        
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_2 simplified.
     */ 
    @Test
    public void test8_3_2_simplified() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
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
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_2
     */
    @Test
    public void test8_3_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX data: <http://example.org/foaf/> "
            + "SELECT ?nick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE {"
            + "    GRAPH data:bobFoaf {"
            + "        ?x foaf:mbox <mailto:bob@work.example> ."
            + "        ?x foaf:nick ?nick }"
            + "}";
        String expectedQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX data: <http://example.org/foaf/> "
            + "SELECT ?nick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE {"
            + "    GRAPH data:bobFoaf {"
            + "    ?x foaf:mbox ?objectUri1 ."
            + "        FILTER (?objectUri1 = <mailto:bob@work.example> || "
            + "                ?objectUri1 = <mailto:bob.smith@work.example>) . "
            + "    ?x foaf:nick ?nick }"
            + "}";
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_3
     */
    @Test
    public void test8_3_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  data:  <http://example.org/foaf/>"
            + "PREFIX  foaf:  <http://xmlns.com/foaf/0.1/>"
            + "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"

            + "SELECT ?mbox ?nick ?ppd "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf>"
            + "FROM NAMED <http://example.org/foaf/bobFoaf>"
            + "WHERE"
            + "{"
            + "  GRAPH data:aliceFoaf "
            + "  {" 
            + "    ?alice foaf:mbox <mailto:alice@work.example> ;"
            + "           foaf:knows ?whom ."
            + "    ?whom  foaf:mbox ?mbox ;"
            + "           rdfs:seeAlso ?ppd ."
            + "    ?ppd  a foaf:PersonalProfileDocument ."
            + "  } ."
            + "  GRAPH ?ppd "
            + "  {"
            + "      ?w foaf:mbox ?mbox ;"
            + "         foaf:nick ?nick "
            + "  }"
            + "}";
    String expectedQuery = "PREFIX  data:  <http://example.org/foaf/>"
            + "PREFIX  foaf:  <http://xmlns.com/foaf/0.1/>"
            + "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"

            + "SELECT ?mbox ?nick ?ppd "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf>"
            + "FROM NAMED <http://example.org/foaf/bobFoaf>"
            + "WHERE"
            + "{"
            + "  GRAPH data:aliceFoaf "
            + "  {" 
            + "    ?alice foaf:mbox ?objectUri1 ."
            + "        FILTER (?objectUri1 = <mailto:alice@work.example> || "
            + "                ?objectUri1 = <mailto:alice.jones@work.example>) . "
            + "    ?alice foaf:knows ?whom ."
            + "    ?whom  foaf:mbox ?mbox ;"
            + "           rdfs:seeAlso ?ppd ."
            + "    ?ppd  a foaf:PersonalProfileDocument ."
            + "  } ."
            + "  GRAPH ?ppd "
            + "  {"
            + "      ?w foaf:mbox ?mbox ;"
            + "         foaf:nick ?nick "
            + "  }"
            + "}";
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("mailto:alice@work.example","mailto:alice@work.example");
        dummyIRSMapper.addMapping("mailto:alice@work.example","mailto:alice.jones@work.example");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 8_3_4 
     */ 
    @Test
    public void test8_3_4() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
            + "PREFIX dc:   <http://purl.org/dc/elements/1.1/>"

            + "SELECT ?name ?mbox ?date "
            + "WHERE "
            + "  {  ?g dc:publisher ?name ;"
            + "        dc:date ?date ."
            + "    GRAPH ?g"
            + "      { ?person foaf:name ?name ; foaf:mbox ?mbox }"
            + "  }";
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIRSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the first query found in Section 9.1
     */ 
    @Test
    public void test9_1_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"

            + "SELECT ?name "
            + "WHERE { ?x foaf:name ?name }"
            + "ORDER BY ?name";
//
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 9.1
     */ 
    @Test
    public void test9_1_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX     :    <http://example.org/ns#>"
            + "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>"

            + "SELECT ?name "
            + "WHERE { ?x foaf:name ?name ; :empId ?emp }"
            + "ORDER BY DESC(?emp)";
//            + "
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 9.1 with ASC
     */ 
    @Test
    public void test9_1_b_ASC() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX     :    <http://example.org/ns#>"
            + "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>"

            + "SELECT ?name "
            + "WHERE { ?x foaf:name ?name ; :empId ?emp }"
            + "ORDER BY ASC(?emp)";
//            + "
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the third query found in Section 9.1
     */ 
    @Test
    public void test9_1_c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX     :    <http://example.org/ns#>"
                + "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name "
                + "WHERE { ?x foaf:name ?name ; "
                + "           :empId ?emp }"
                + "ORDER BY ?name DESC(?emp)";
//
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 9.2
     */ 
    @Test
    public void test9_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:       <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name "
            + "WHERE "
            + " { ?x foaf:name ?name }";

        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 
     * / 
    @Test
    public void test() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="";
//            + "
        String expectedQuery = inputQuery;
              
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    /**/
}
