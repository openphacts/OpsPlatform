package eu.ops.plugin.imssparqlexpand;

import eu.ops.plugin.imssparqlexpand.DummyIMSMapper;
import eu.ops.plugin.imssparqlexpand.IMSSPARQLExpand;
import eu.ops.plugin.imssparqlexpand.IMSMapper;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import eu.ops.plugin.imssparqlexpand.QueryUtils;
import static org.junit.Assert.*;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test all the queries found in <http://www.w3.org/TR/2008/REC-rdf-sparql-query-20080115/.>
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
        
    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);

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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/book/book1","http://example.org/book/book1");
        dummyIMSMapper.addMapping("http://example.org/book/book1","http://example.org/book/other");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://example.org/book/book1");
        dummyIMSMapper.addMapping("http://example.org/book/book1", "http://other.com/livre/2345");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:alice@work.example","mailto:alice@work.example");
        dummyIMSMapper.addMapping("mailto:alice@work.example","mailto:alice.jones@work.example");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob@work.example");
        dummyIMSMapper.addMapping("mailto:bob@work.example","mailto:bob.smith@work.example");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 9.3
     */ 
    @Test()
    public void test9_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
            + "SELECT ?name WHERE { ?x foaf:name ?name }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 9.3.1 
     */ 
    @Test
    public void test9_3_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT DISTINCT ?name WHERE { ?x foaf:name ?name }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 9-3_2
     */ 
    @Test
    public void test9_3_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT REDUCED ?name WHERE { ?x foaf:name ?name }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 9.4 
     */ 
    @Test
    public void test9_4() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT  ?name "
                + "WHERE   { ?x foaf:name ?name }"
                + "ORDER BY ?name "
                + "LIMIT   5 "
                + "OFFSET  10 ";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 9.5
     */ 
    @Test
    public void test9_5() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name"
                + "WHERE { ?x foaf:name ?name }"
                + "LIMIT 20";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.1 
     */ 
    @Test
    public void test10_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?nameX ?nameY ?nickY"
                + "WHERE"
                + "  { ?x foaf:knows ?y ;"
                + "       foaf:name ?nameX ."
                + "    ?y foaf:name ?nameY ."
                + "    OPTIONAL { ?y foaf:nick ?nickY }"
                + "  }";
        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.2 
     */ 
    @Test
    public void test10_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#>"
                + "CONSTRUCT   { <http://example.org/person#Alice> vcard:FN ?name }"
                + "WHERE       { ?x foaf:name ?name }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.2.1
     */ 
    @Test
    public void test10_2_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#>"
                + "CONSTRUCT { ?x  vcard:N _:v ."
                + "            _:v vcard:givenName ?gname ."
                + "            _:v vcard:familyName ?fname }"
                + "WHERE"
                + " {"
                + "    { ?x foaf:firstname ?gname } UNION  { ?x foaf:givenname   ?gname } ."
                + "    { ?x foaf:surname   ?fname } UNION  { ?x foaf:family_name ?fname } ."
                + " }";
        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.2.2.
     * Note: This required an extra prefix for xsd
     */ 
    @Test
    public void test10_2_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
                + "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX app: <http://example.org/ns#>"
                + "CONSTRUCT { ?s ?p ?o } WHERE"
                + " {"
                + "   GRAPH ?g { ?s ?p ?o } ."
                + "   { ?g dc:publisher <http://www.w3.org/> } ."
                + "   { ?g dc:date ?date } ."
                + "   FILTER ( app:customDate(?date) > \"2005-02-28T00:00:00Z\"^^xsd:dateTime ) ."
                + " }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.2.3
     */ 
    @Test
    public void test10_2_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX site: <http://example.org/stats#> "
                + "CONSTRUCT { [] foaf:name ?name } "
                + "WHERE "
                + "{ [] foaf:name ?name ;"
                + "     site:hits ?hits ."
                + "}"
                + "ORDER BY desc(?hits) "
                + "LIMIT 2";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.3
     */ 
    @Test
    public void test10_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
               + "ASK  { ?x foaf:name  \"Alice\" }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test a query like the query found in Section 10.3
     */ 
    @Test
    public void test10_3_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT * "
                + "WHERE{ "
                + "?x foaf:name  \"Alice\" }"
                + "LIMIT 1";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test a query like the query found in Section 10.3
     */ 
    @Test
    public void test10_3_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT REDUCED * "
                + "WHERE{ "
                + "?x foaf:name  \"Alice\" }"
                + "LIMIT 1";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test a query like the query found in Section 10.3
     */ 
    @Test
    public void test10_3_c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT DISTINCT * "
                + "WHERE{ "
                + "?x foaf:name  \"Alice\" }"
                + "LIMIT 1";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the second query found in Section 10.3 with one match
     */ 
    @Test
    public void test10_3_d() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "ASK  { ?x foaf:name  \"Alice\" ;"
                + "          foaf:mbox  <mailto:alice@work.example> }";

        String expectedQuery = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "ASK  { ?x foaf:name  \"Alice\" ;"
                + "          foaf:mbox  <mailto:aliceJones@work.example> }";;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:alice@work.example", "mailto:aliceJones@work.example");
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.1
     */ 
    @Test
    public void test10_4_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="DESCRIBE <http://example.org/>";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.1
     * With a one to one mapping
     */ 
    @Test
    public void test10_4_1a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="DESCRIBE <http://example.org/>";

        String expectedQuery = "DESCRIBE <http://SomeotherURI/qwerty>";
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/", "http://SomeotherURI/qwerty");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.1
     * With a one to many mapping
     */ 
    @Test
    public void test10_4_1b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="DESCRIBE <http://example.org/>";

        String expectedQuery = "DESCRIBE <http://example.org/> <http://SomeotherURI/qwerty> "
                + "                      <http://AnotherOtherURI/df7fj3d>";
               
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/", "http://example.org/");
        dummyIMSMapper.addMapping("http://example.org/", "http://SomeotherURI/qwerty");
        dummyIMSMapper.addMapping("http://example.org/", "http://AnotherOtherURI/df7fj3d");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.1
     * With more than one uri no mappings
     */ 
    @Test
    public void test10_4_1c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="DESCRIBE <http://example.org/> <http://SomeotherURI/qwerty> "
                + "                      <http://SomeotherURI/qwerty>";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.1
     * With more than one uri some mappings
     */ 
    @Test
    public void test10_4_1d() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="DESCRIBE <http://example.org/1> <http://example.org/45> "
                + "                  <http://example.org/561> <http://example.org/455>";

        String expectedQuery = "DESCRIBE <http://SomeotherURI/qwerty> <http://example.org/1> "
                + "<http://SomeotherURI/dsssas> <http://example.org/45> "
                + "<http://example.org/561> "
                + "<http://SomeotherURI/fgdgdsfd> <http://example.org/455> <http://brenda.org/prtoien/sdfsffs>";
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://example.org/1", "http://SomeotherURI/qwerty");
        dummyIMSMapper.addMapping("http://example.org/1", "http://example.org/1");
        dummyIMSMapper.addMapping("http://example.org/45", "http://SomeotherURI/dsssas");
        dummyIMSMapper.addMapping("http://example.org/45", "http://example.org/45");
        dummyIMSMapper.addMapping("http://example.org/561", "http://example.org/561");
        dummyIMSMapper.addMapping("http://example.org/455", "http://SomeotherURI/fgdgdsfd");
        dummyIMSMapper.addMapping("http://example.org/455", "http://example.org/455");
        dummyIMSMapper.addMapping("http://example.org/455", "http://brenda.org/prtoien/sdfsffs");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the first query found in 10.4.2
     */ 
    @Test
    public void test10_4_2a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "DESCRIBE ?x "
                + "WHERE    { ?x foaf:mbox <mailto:alice@org> }";

        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "DESCRIBE ?x "
                + "WHERE {"
                + "    ?x foaf:mbox ?objectUri1 "
                + "        FILTER (?objectUri1 = <mailto:aliceJones@org>"
                + "             || ?objectUri1 = <mailto:alice@org>)"     
                + "}";
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("mailto:alice@org", "mailto:aliceJones@org");
        dummyIMSMapper.addMapping("mailto:alice@org", "mailto:alice@org");
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println(query.toString());
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the second query found in Section 10.4.2
     */ 
    @Test
    public void test10_4_2b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "DESCRIBE ?x"
                + "WHERE    { ?x foaf:name \"Alice\" }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the third query found in Section 10.4.2
     */ 
    @Test
    public void test10_4_2c() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "DESCRIBE ?x ?y <http://example.org/>"
                + "WHERE    {?x foaf:knows ?y}";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 10.4.3
     */ 
    @Test
    public void test10_4_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX ent:  <http://org.example.com/employees#> "
                + "DESCRIBE ?x WHERE { ?x ent:employeeId \"1234\" }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 11
     */ 
    @Test
    public void test11() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX a:      <http://www.w3.org/2000/10/annotation-ns#>"
                + "PREFIX dc:     <http://purl.org/dc/elements/1.1/>"
                + "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?annot "
                + "WHERE { ?annot  a:annotates  <http://www.w3.org/TR/rdf-sparql-query/> ."
                + "        ?annot  dc:date      ?date ."
                + "        FILTER ( ?date > \"2005-01-01T00:00:00Z\"^^xsd:dateTime ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 11.4.1
     */ 
    @Test
    public void test11_4_1() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "PREFIX dc:   <http://purl.org/dc/elements/1.1/>"
                + "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?name"
                + " WHERE { ?x foaf:givenName  ?givenName ."
                + "         OPTIONAL { ?x dc:date ?date } ."
                + "         FILTER ( bound(?date) ) }";
        //Order of statements comes our diffeently but same query.
        String expectedQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "PREFIX dc:   <http://purl.org/dc/elements/1.1/>"
                + "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?name"
                + " WHERE { ?x foaf:givenName  ?givenName ."
                + "         FILTER ( bound(?date) )"
                + "         OPTIONAL { ?x dc:date ?date } "
                + "}";
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 11.4.2
     * Slighlty changed order.
     */
    @Test
    @Ignore
    public void test11_4_2() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { "
                + "       FILTER isIRI(?mbox) "
                + "       ?x foaf:name  ?name ;"
                + "          foaf:mbox  ?mbox ."
                + "}";

        //ISURI and ISIRI result in the same TupleExpr
        String expectedQuery = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { "
                + "       FILTER isURI(?mbox) "
                + "       ?x foaf:name  ?name ;"
                + "          foaf:mbox  ?mbox ."
                + "}";
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        //Test with IsIri
        assertTrue(QueryUtils.sameTupleExpr(inputQuery, query.toString()));
        //Test with IsUri
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 11.4.3
     */ 
    @Test
    public void test11_4_3() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX a:      <http://www.w3.org/2000/10/annotation-ns#>"
                + "PREFIX dc:     <http://purl.org/dc/elements/1.1/>"
                + "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?given ?family "
                + "WHERE { "
                + "         ?annot  a:annotates  <http://www.w3.org/TR/rdf-sparql-query/> ."
                + "         ?annot  dc:creator   ?c ."
                + "         OPTIONAL { ?c  foaf:given   ?given ; foaf:family  ?family } ."
                + "         FILTER isBlank(?c)"
                + "       }";

        //Filter has moved up.
        String expectedQuery = "PREFIX a:      <http://www.w3.org/2000/10/annotation-ns#>"
                + "PREFIX dc:     <http://purl.org/dc/elements/1.1/>"
                + "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?given ?family "
                + "WHERE { "
                + "         FILTER isBlank(?c)"
                + "         ?annot  a:annotates  <http://www.w3.org/TR/rdf-sparql-query/> ."
                + "         ?annot  dc:creator   ?c ."
                + "         OPTIONAL { ?c  foaf:given   ?given ; foaf:family  ?family } ."
                + "       }";

              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test the query found in Section 11.4.4 
     */ 
    @Test
    public void test11_4_4() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox"
                + "WHERE { ?x foaf:name  ?name ;"
                + "           foaf:mbox  ?mbox ."
                + "         FILTER isLiteral(?mbox) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 11.4.5
     */ 
    @Test
    public void test11_4_5() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { ?x foaf:name  ?name ;"
                + "            foaf:mbox  ?mbox ."
                + "         FILTER regex(str(?mbox), \"@work.example\") }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 11.4.6
     */ 
    @Test
    public void test11_4_6() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { ?x foaf:name  ?name ;"
                + "            foaf:mbox  ?mbox ."
                + "         FILTER ( lang(?name) = \"ES\" ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 11.4.7
     */ 
    @Test
    public void test11_4_7() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX eg:   <http://biometrics.example/ns#>"
                + "SELECT ?name ?shoeSize "
                + "WHERE { ?x foaf:name  ?name ; eg:shoeSize  ?shoeSize ."
                + "         FILTER ( datatype(?shoeSize) = xsd:integer ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the first query found in Section 11.4.10
     */ 
    @Test
    public void test11_4_10a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name1 ?name2 "
                + "WHERE { ?x foaf:name  ?name1 ;"
                + "            foaf:mbox  ?mbox1 ."
                + "         ?y foaf:name  ?name2 ;"
                + "            foaf:mbox  ?mbox2 ."
                + "         FILTER (?mbox1 = ?mbox2 && ?name1 != ?name2)"
                + "       }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 11.4.10
     */ 
    @Test
    public void test11_4_10() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX a:      <http://www.w3.org/2000/10/annotation-ns#>"
                + "PREFIX dc:     <http://purl.org/dc/elements/1.1/>"
                + "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?annotates "
                + "WHERE { ?annot  a:annotates  ?annotates ."
                + "        ?annot  dc:date      ?date ."
                + "        FILTER ( ?date = xsd:dateTime(\"2005-01-01T00:00:00Z\") ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the first query found in Section 11.4.11
     */ 
    @Test
    public void test11_4_11a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name1 ?name2 "
                + "WHERE { ?x foaf:name  ?name1 ;"
                + "            foaf:mbox  ?mbox1 ."
                + "         ?y foaf:name  ?name2 ;"
                + "            foaf:mbox  ?mbox2 ."
                + "         FILTER (sameTerm(?mbox1, ?mbox2) && !sameTerm(?name1, ?name2))"
                + "       }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 11.4.11
     * With the missing closing bracket added!
     */ 
    @Test
    public void test11_4_11() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  :      <http://example.org/WMterms#>"
                + "PREFIX  t:     <http://example.org/types#>"
                + "SELECT ?aLabel1 ?bLabel "
                + "WHERE { ?a  :label        ?aLabel ."
                + "        ?a  :weight       ?aWeight ."
                + "        ?a  :displacement ?aDisp ."
                + "        ?b  :label        ?bLabel ."
                + "        ?b  :weight       ?bWeight ."
                + "        ?b  :displacement ?bDisp ."
                + "        FILTER ( sameTerm(?aWeight, ?bWeight) && !sameTerm(?aDisp, ?bDisp) ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 11.4.12
     */ 
    @Test
    public void test11_4_12() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT ?title "
                + "WHERE { ?x dc:title  \"That Seventies Show\"@en ;"
                + "            dc:title  ?title ."
                + "         FILTER langMatches( lang(?title), \"FR\" ) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the query found in Section 11.4.13
     */ 
    @Test
    public void test11_4_13() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT ?name "
                + "WHERE { ?x foaf:name  ?name"
                + "         FILTER regex(?name, \"^ali\", \"i\") }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the first query found in Section 11.6
     */ 
    @Test
    public void test11_6a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "PREFIX func: <http://example.org/functions#>"
                + "SELECT ?name ?id "
                + "WHERE { ?x foaf:name  ?name ;"
                + "           func:empId   ?id ."
                + "        FILTER (func:even(?id)) }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test the second query found in Section 11.6
     */ 
    @Test
    public void test11_6b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX aGeo: <http://example.org/geo#>"
                + "SELECT ?neighbor "
                + "WHERE { ?a aGeo:placeName \"Grenoble\" ."
                + "        ?a aGeo:location ?axLoc ."
                + "        ?a aGeo:location ?ayLoc ."
                + "        ?b aGeo:placeName ?neighbor ."
                + "        ?b aGeo:location ?bxLoc ."
                + "        ?b aGeo:location ?byLoc ."
                + "        FILTER ( aGeo:distance(?axLoc, ?ayLoc, ?bxLoc, ?byLoc) < 10 ) ."
                + "      }";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    /*Lastone*/

    /**
     * Test the query found in Section 
     * / 
    @Test
    public void test() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="";

        String expectedQuery = inputQuery;
              
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
