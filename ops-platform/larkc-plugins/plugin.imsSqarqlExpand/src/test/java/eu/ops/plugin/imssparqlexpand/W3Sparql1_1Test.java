package eu.ops.plugin.imssparqlexpand;

import eu.ops.plugin.imssparqlexpand.IMSSPARQLExpand;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import eu.ops.plugin.imssparqlexpand.QueryUtils;
import static org.junit.Assert.*;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.ops.plugin.imssparqlexpand.ims.DummyIMSMapper;
import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
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
 * This class will test all the queries found in http://www.w3.org/TR/2010/WD-sparql11-query-20100601/.
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
public class W3Sparql1_1Test {
        
    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);

    public W3Sparql1_1Test() {
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
                + "?replacedURI1 <http://purl.org/dc/elements/1.1/title> ?title ."
                + "FILTER (?replacedURI1 = <http://example.org/book/book1> || ?replacedURI1 = <http://example.org/book/other>)}";      

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
     * Test third query found in Section 2.3.1
     */
    @Test
    public void test2_3_1_c() throws MalformedQueryException, QueryExpansionException {
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
     * Test query found in Section 2.3.2
     */
    @Test
    public void test2_3_2() throws MalformedQueryException, QueryExpansionException {
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
     * Test query found in Section 2.6
     */
    @Test
    public void test2_6() throws MalformedQueryException, QueryExpansionException {
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
            + "WHERE   {?replacedURI1 dc:title ?title"
            + "         FILTER (?replacedURI1 = <http://example.org/book/book1> || "
            + "                 ?replacedURI1 = <http://other.com/livre/2345>) } ";
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
     * Test the first query found in Section 8.1
     * 
     * Openrdf appears not able to handle this query. Likely cause is the EXISTS clause.
     */
    @Test
    @Ignore
    public void test8_1_a() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?person "
            + "WHERE "
            + "{ "
            + "    ?person rdf:type  foaf:Person ."
            + "    FILTER NOT EXISTS { ?person foaf:name ?name }"
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
     * Test the second query found in Section 8.2
     * 
     * Openrdf appears not able to handle this query. Likely cause is the EXISTS clause.
     */
    @Test
    @Ignore
    public void test8_1_b() throws MalformedQueryException, QueryExpansionException {
        String inputQuery ="PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?person "
            + "WHERE "
            + "{ "
            + "    ?person rdf:type  foaf:Person ."
            + "    FILTER EXISTS { ?person foaf:name ?name }"
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
     * Test the query found in Section 8.2
     * 
     * Openrdf appears not able to handle this query. Likely cause is the MINUS clause.
     */
    @Test
    @Ignore
    public void test8_2() throws MalformedQueryException, QueryExpansionException {
         String inputQuery = "PREFIX :       <http://example/>" 
            + "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT DISTINCT ?s "
            + "WHERE { "
            + "   ?s ?p ?o ."
            + "   MINUS {"
            + "      ?s foaf:givenName \"Bob\" ."
            + "   } "
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


}
