package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import static org.junit.Assert.*;

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
 * Unit test for your LarKC plug-in.
 */
public class IRSSPARQLExpand1Test {

    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand1.class);

    public IRSSPARQLExpand1Test() {
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
    
    static String CONSTRUCT_QUERY = "CONSTRUCT { ?s ?p ?o } WHERE { ?o ?p ?s }";           
    /**
     * Test that we do not do anything with CONSTRUCT queries
     */
    @Test
    public void testConstructQuery() {
        IRSSPARQLExpand1 expander = 
                new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(
                new SPARQLQueryImpl(CONSTRUCT_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(CONSTRUCT_QUERY, query.toString());
    }

    
        
    
    static String DESSCRIBE_QUERY = "DESCRIBE <http://brenda-enzymes.info/1.1.1.1>";
    /**
     * Test that we do not do anything with DESCRIBE queries
     */
    @Test
    public void testDescribeQuery() {
        IRSSPARQLExpand1 expander = 
                new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(
                new SPARQLQueryImpl(DESSCRIBE_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(DESSCRIBE_QUERY, query.toString());
    }

    
    
    
    static String ASK_QUERY = "ASK { ?s ?p ?o }";
    /**
     * Test that we do not do anything with ASK queries
     */
    @Test
    public void testAskQuery() {
        IRSSPARQLExpand1 expander = 
                new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(
                new SPARQLQueryImpl(ASK_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(ASK_QUERY, query.toString());
    }
    
    
    
    
    static String MINIMAL_SPACING_QUERY = "SELECT ?book ?title "
            + "WHERE{?book <http://dc/title> ?title.}";
    /**
     * Test that a query with minimal spacing in its text is processed correctly
     */
    @Test
    public void testSpacing() throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(MINIMAL_SPACING_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(MINIMAL_SPACING_QUERY, query.toString()));
    }
  
    
    
    
    static String PREFIX_QUERY = "PREFIX books: <http://example.org/book/> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT ?book ?title WHERE { ?book dc:title ?title . }";    
    /**
     * Test that a query involving prefixes is output correctly
     */
    @Test
    public void testPrefixes() throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(PREFIX_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(PREFIX_QUERY, query.toString()));
    }
 
    

    
    static String NO_URI_QUERY = "SELECT ?book ?author WHERE { "
                + "?book <http://dc.org/author> ?author . "
                + "}";
    /**
     * Test that nothing happens to a query without any URIs present.
     */
    @Test
    public void testQueryWithoutURIs() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return new DummyIRSMapper();
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(NO_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(NO_URI_QUERY, query.toString()));
    }
    
    
    
    
    static String SINGLE_OBJECT_URI_QUERY = "SELECT ?protein"
                + " WHERE { "
                + "?protein <http://www.foo.org/somePredicate> <http://foo.info/1.1.1.1> . "
                + "}";
    static String SINGLE_OBJECT_URI_QUERY_EXPECTED = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.foo.org/somePredicate> "
                + "?objectUri1 . "
                + "FILTER (?objectUri1 = <http://bar.com/8hd83> || "
                + "?objectUri1 = <http://foo.info/1.1.1.1>) "
                + "}";            
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * object is expanded.
     */
    @Test
    public void testOneBGPOneObjectURI() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1", "http://bar.com/8hd83");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1", "http://foo.info/1.1.1.1");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SINGLE_OBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SINGLE_OBJECT_URI_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    
    static String SINGLE_SUBJECT_URI_QUERY = "SELECT ?p ?o"
                + " WHERE {"
                + " <http://foo.com/45273> "
                + " ?p "
                + " ?o . "
                + "}";
    static String SINGLE_SUBJECT_URI_QUERY_EXPECTED =  "SELECT ?p ?o"
                + " WHERE {"
                + "?subjectUri1 ?p ?o . "
                + "FILTER (?subjectUri1 = <http://bar.co.uk/346579> || "
                + "?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "?subjectUri1 = <http://foo.com/45273>) "
                + "}";    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject is expanded.
     */
    @Test
    public void testOneBGPOneSubjectURI() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.com/45273","http://bar.co.uk/346579");
        dummyIRSMapper.addMapping("http://foo.com/45273","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://foo.com/45273","http://foo.com/45273");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SINGLE_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SINGLE_SUBJECT_URI_QUERY_EXPECTED, query.toString()));
    }

    
    
    
    static String SINGLE_BOTH_URI_QUERY = "SELECT ?p "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "}";
    static String SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri1 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri1 = <http://bar.info/u83hs> || "
                + "?objectUri1 = <http://foo.com/1.1.1.1>) "
                + "}";                      
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIOneMatchEach() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://result.com/90");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://example.org/chem/8j392");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://bar.info/u83hs");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://foo.com/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SINGLE_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH, query.toString()));
    }

    static String SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_ONLY_SUBJECT_EXPECTED = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p <http://foo.com/1.1.1.1> . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "}";                      
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIOnlySubjectMatch() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://result.com/90");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://example.org/chem/8j392");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://foo.com/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SINGLE_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_ONLY_SUBJECT_EXPECTED, query.toString()));
    }
        
    static String SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri1 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://somewhere.com/chebi/7s82> || "
                + "?subjectUri1 = <http://another.com/maps/hsjnc> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri1 = <http://bar.info/u83hs> || "
                + "?objectUri1 = <http://onemore.co.uk/892k3> || "
                + "?objectUri1 = <http://foo.com/1.1.1.1>) "
                + "}";    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is more than one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIMultipleMatches() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://result.com/90");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://somewhere.com/chebi/7s82");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://another.com/maps/hsjnc");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://example.org/chem/8j392");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://bar.info/u83hs");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://onemore.co.uk/892k3");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://foo.com/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SINGLE_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES, query.toString()));
    }

    
    
    
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> <http://foo.info/1.1.1.1> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED =  "SELECT ?protein"
                + " WHERE {"
                + "{?protein <http://foo.com/somePredicate> ?objectUri1 . "
                + "FILTER (?objectUri1 = <http://bar.com/9khd7> || "
                + "?objectUri1 = <http://foo.info/1.1.1.1>) }"
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
    /**
     * Test that a query with a two basic graph patterns with a single URI in the 
     * object is expanded.
     */
    @Test
    public void testTwoBGPOneObjectURI() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
 
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(TWO_STATEMENTS_ONE_OBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    
    static String SHARED2_SUBJECT_URI_QUERY = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "}";    
    static String SHARED2_SUBJECT_URI_QUERY_EXPECTED = "SELECT ?protein ?name "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "   FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "           ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "   FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "           ?subjectUri2 = <http://foo.info/1.1.1.1>) }"
                + "}";
    /**
     * Test that a query with a two basic graph patterns which share a single 
     * subject URI is expanded to every combination.
     */
    @Test
    public void testTwoBGPShareSubjectURI() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SHARED2_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SHARED2_SUBJECT_URI_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    
    static String SHARED3_SUBJECT_URI_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "<http://foo.info/1.1.1.1> <http://bar.org/relation> ?enzyme . "
                + "}";    
    static String SHARED3_SUBJECT_URI_QUERY_EXPECTED = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "    FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "            ?subjectUri1 = <http://example.ac.uk/89ke> || "
                + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri2 <http://foo.com/anotherPredicate> ?name ."
                + "    FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "            ?subjectUri2 = <http://example.ac.uk/89ke> || "
                + "            ?subjectUri2 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri3 <http://bar.org/relation> ?enzyme . "
                + "    FILTER (?subjectUri3 = <http://bar.com/9khd7> || "
                + "            ?subjectUri3 = <http://example.ac.uk/89ke> || "
                + "            ?subjectUri3 = <http://foo.info/1.1.1.1>) }"
                + "}";
    /**
     * Test that a query with a three basic graph patterns which share a single 
     * subject URI is expanded to every combination.
     */
    @Test
    public void testThreeBGPShareSubjectURI() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://example.ac.uk/89ke");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SHARED3_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SHARED3_SUBJECT_URI_QUERY_EXPECTED, query.toString()));
    }

    
    
    
    static String SIMPLE_CHAIN_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "?protein <http://foo.com/somePredicate> <http://example.org/chem/2918> . "
                + "<http://example.org/chem/2918> <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String SIMPLE_CHAIN_QUERY_EXPECTED = "SELECT ?protein ?name "
                + "WHERE {"
                + "{?protein <http://foo.com/somePredicate> ?objectUri1 . "
                + "     FILTER (?objectUri1 = <http://bar.com/9khd7> || "
                + "             ?objectUri1 = <http://foo.info/1.1.1.1> || "
                + "             ?objectUri1 = <http://example.org/chem/2918>) }"
                + "{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "     FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "             ?subjectUri2 = <http://foo.info/1.1.1.1> || "
                + "             ?subjectUri2 = <http://example.org/chem/2918>) }"
                + "}";            
    /**
     * Test that a query with a two basic graph patterns which form a chain
     * based on the object URI of one with the subject URI of the second.
     */
    @Test
    public void testBGPChainSimple() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://example.org/chem/2918");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SIMPLE_CHAIN_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SIMPLE_CHAIN_QUERY_EXPECTED, query.toString()));
    }

    
    
    
    static String COMPLEX_CHAIN_QUERY = "SELECT ?name ?value1 ?value2 "
                + "WHERE { "
                + "<http://bar.co.uk/998234> <http://foo.com/somePredicate> <http://example.org/chem/2918> . "
                + "<http://bar.co.uk/998234> <http://foo.com/predicate> ?value1 . "
                + "<http://example.org/chem/2918> <http://foo.com/anotherPredicate> ?name . "
                + "<http://example.org/chem/2918> <http://foo.com/aPredicate> <http://yetanother.com/-09824> ."
                + "<http://yetanother.com/-09824> <http://bar.org/predicate> ?value2 . "
                + "}";
    static String COMPLEX_CHAIN_QUERY_EXPECTED = "SELECT ?name ?value1 ?value2 "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?objectUri1 . "
                + "    FILTER (?subjectUri1 = <http://foo.info/1.1.1.1> || "
                + "            ?subjectUri1 = <http://bar.co.uk/998234>) "               
                + "    FILTER (?objectUri1 = <http://bar.com/9khd7> || "
                + "            ?objectUri1 = <http://hello.uk/87234> || "
                + "            ?objectUri1 = <http://example.org/chem/2918>) }"                
                + "{?subjectUri2 <http://foo.com/predicate> ?value1 . "                
                + "    FILTER (?subjectUri2 = <http://foo.info/1.1.1.1> || "
                + "            ?subjectUri2 = <http://bar.co.uk/998234>) }"                
                + "{?subjectUri3 <http://foo.com/anotherPredicate> ?name . "                
                + "    FILTER (?subjectUri3 = <http://bar.com/9khd7> || "
                + "            ?subjectUri3 = <http://hello.uk/87234> || "
                + "            ?subjectUri3 = <http://example.org/chem/2918>) }"                
                + "{?subjectUri4 <http://foo.com/aPredicate> ?objectUri4 . "                
                + "    FILTER (?subjectUri4 = <http://bar.com/9khd7> || "
                + "            ?subjectUri4 = <http://hello.uk/87234> || "
                + "            ?subjectUri4 = <http://example.org/chem/2918>) "
                + "    FILTER (?objectUri4 = <http://yetmore.info/872342> || "
                + "            ?objectUri4 = <http://ohboy.com/27393> || "
                + "            ?objectUri4 = <http://imborednow.co/akuhe8> || "
                + "            ?objectUri4 = <http://yetanother.com/-09824>) }"                
                + "{?subjectUri5 <http://bar.org/predicate> ?value2 . "
                + "    FILTER (?subjectUri5 = <http://yetmore.info/872342> || "
                + "            ?subjectUri5 = <http://ohboy.com/27393> || "
                + "            ?subjectUri5 = <http://imborednow.co/akuhe8> || "
                + "            ?subjectUri5 = <http://yetanother.com/-09824>) }"
                + "}";            
    /**
     * Test that a query with a several basic graph patterns which form a series
     * of chains based on the object URI of one BGP being the subject URI of 
     * another BGP.
     */
    @Test
    public void testBGPChainComplex() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://bar.co.uk/998234","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://bar.co.uk/998234","http://bar.co.uk/998234");
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://hello.uk/87234");
        dummyIRSMapper.addMapping("http://example.org/chem/2918","http://example.org/chem/2918");
        dummyIRSMapper.addMapping("http://yetanother.com/-09824","http://yetmore.info/872342");
        dummyIRSMapper.addMapping("http://yetanother.com/-09824","http://ohboy.com/27393");
        dummyIRSMapper.addMapping("http://yetanother.com/-09824","http://imborednow.co/akuhe8");
        dummyIRSMapper.addMapping("http://yetanother.com/-09824","http://yetanother.com/-09824");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(COMPLEX_CHAIN_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(COMPLEX_CHAIN_QUERY_EXPECTED, query.toString()));
    }

    
    
    
    static String REPEATED_SUBJECT_SHORTHAND_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein ; "
                + "<http://foo.com/anotherPredicate> ?name ; "
                + "<http://bar.org/relation> ?enzyme . "
                + "}"; 
    static String REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "    FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "            ?subjectUri1 = <http://example.org/chem/2918> || "
                + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "    FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "            ?subjectUri2 = <http://example.org/chem/2918> || "
                + "            ?subjectUri2 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri3 <http://bar.org/relation> ?enzyme . "
                + "    FILTER (?subjectUri3 = <http://bar.com/9khd7> || "
                + "            ?subjectUri3 = <http://example.org/chem/2918> || "
                + "            ?subjectUri3 = <http://foo.info/1.1.1.1>) }"
                + "}";    
    /**
     * Test that a query written using the shorthand for repeated subject URI
     * gets expanded correctly.
     */
    @Test
    public void testRepeatedSubjectShorthandQuery() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException{
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://example.org/chem/2918");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(REPEATED_SUBJECT_SHORTHAND_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    static String REPEATED_SUBJECT_PREDICATE_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein , ?name . "
                + "}";   
    static String REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED =  "SELECT ?protein ?name "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "    FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "            ?subjectUri1 = <http://example.org/chem/2918> || "
                + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
                + "{?subjectUri2 <http://foo.com/somePredicate> ?name . "
                + "    FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "            ?subjectUri2 = <http://example.org/chem/2918> || "
                + "            ?subjectUri2 = <http://foo.info/1.1.1.1>) }"
                + "}";     
    /**
     * Test that a query written using the shorthand for repeated subject and
     * predicate URIs gets expanded correctly.
     */
    @Test
    public void testRepeatedSubjectPredicateShorthandQuery() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException{
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://example.org/chem/2918");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(REPEATED_SUBJECT_PREDICATE_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED, query.toString()));
    }
   
    //TODO: Write test for OPTIONAL BGP expansion
    //TODO: Write test for OPTIONAL set of BGPs expansion
    
    
    
    static String SIMPLE_OPTIONAL_QUERY = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
    static String SIMPLE_OPTIONAL_QUERY_EXPECTED = "SELECT ?protein ?name "
                + "WHERE {"
                + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "    FILTER (?subjectUri1 = <http://example.com/9khd7> || "
                + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
                + "OPTIONAL {{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "    FILTER (?subjectUri2 = <http://another.org/82374> || "
                + "            ?subjectUri2 = <http://bar.com/ijdu>) }} "
                + "}";
    /**
     * Test that a query involving an optional clause is output correctly.
     * 
     * OPTIONALS can be dealt with in the same way as BGPs, but need appropriate
     * query string generation
     */
    @Test
    public void testOptionalQuery_Simple() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://example.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://bar.com/ijdu","http://another.org/82374");
        dummyIRSMapper.addMapping("http://bar.com/ijdu","http://bar.com/ijdu");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SIMPLE_OPTIONAL_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SIMPLE_OPTIONAL_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    
    static String ONLY_OPTIONAL_STATEMENTS_QUERY = "SELECT ?protein ?name "
            + "WHERE { "
            + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . }"
            + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
            + "}";
    static String ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED = "SELECT ?protein ?name "
            + "WHERE {"
            + "OPTIONAL {{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
            + "    FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
            + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }} "
            + "OPTIONAL {{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
            + "    FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
            + "            ?subjectUri2 = <http://foo.info/1.1.1.1>) }} "
            + "}";
    /**
     * Test that a query involving only optional clauses is output correctly.
     */
    @Test
    public void testOptionalQuery_BothOptional() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://bar.com/ijdu","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://bar.com/ijdu","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONLY_OPTIONAL_STATEMENTS_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println(query.toString());
        assertTrue(QueryUtils.sameTupleExpr(ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED, query.toString()));
    }
    
    
    
    
    String OPTIONAL_REPEATED_SUBJECT_QUERY = "SELECT ?protein ?name "
            + "WHERE { "
            + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
            + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name .} "
            + "}";
    String OPTIONAL_REPEATED_SUBJECT_QUERY_EXPECTED = "SELECT ?protein ?name "
            + "WHERE {"
            + "{?subjectUri1 <http://foo.com/somePredicate> ?protein . "
            + "    FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
            + "            ?subjectUri1 = <http://foo.info/1.1.1.1>) }"
            + "OPTIONAL {{?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
            + "    FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
            + "            ?subjectUri2 = <http://foo.info/1.1.1.1>)}} "
            + "}";
    /**
     * Test that a query with a repeated subject URI involving an optional clause 
     * is output correctly.
     * 
     */
    @Test
    public void testOptionalQuery_repeatedSubjectUri() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
    
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(OPTIONAL_REPEATED_SUBJECT_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(OPTIONAL_REPEATED_SUBJECT_QUERY_EXPECTED, query.toString()));
    }
    
    String OPTIONAL_WITH_FILTER_QUERY = "SELECT ?protein ?name "
            + "WHERE { "
            + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
            + "OPTIONAL {?protein <http://foo.com/anotherPredicate> ?name ."
            + "         FILTER (?name == <htpp://bar.com/oneName> || ?name == <http://mike.org/anotherName>)} "
            + "}";
    String OPTIONAL_WITH_FILTER_QUERY_EXPECTED = "SELECT ?protein ?name "
            + "WHERE { "
            + "      {?subjectUri1 <http://foo.com/somePredicate> ?protein . "
            + "      FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
            + "              ?subjectUri1 = <http://foo.info/1.1.1.1>)} "
            + "      OPTIONAL {{?protein <http://foo.com/anotherPredicate> ?name ."
            + "               FILTER (?name == <htpp://bar.com/oneName> || ?name == <htpp://nano.com/JohnSmith> "
            + "                       || ?name == <http://us.gov.org/MikeBrown> "
            + "                       || ?name == <http://mike.org/anotherName>)}} "
            + "}";
    /**
     * Test that a query with a repeated subject URI involving an optional clause 
     * is output correctly.
     * 
     */
    @Test
    public void testOptionalWithFilterQuery() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://bar.com/9khd7");
        dummyIRSMapper.addMapping("http://foo.info/1.1.1.1","http://foo.info/1.1.1.1");
        dummyIRSMapper.addMapping("htpp://bar.com/oneName","htpp://bar.com/oneName");
        dummyIRSMapper.addMapping("htpp://bar.com/oneName","htpp://nano.com/JohnSmith");
        dummyIRSMapper.addMapping("http://mike.org/anotherName","http://us.gov.org/MikeBrown");
        dummyIRSMapper.addMapping("http://mike.org/anotherName","http://mike.org/anotherName");
    
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(OPTIONAL_REPEATED_SUBJECT_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(OPTIONAL_REPEATED_SUBJECT_QUERY_EXPECTED, query.toString()));
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
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI for which there 
     * are multiple matches
     */
    @Test
    public void testMeet_oneBgpObjectUriMultipleMatches() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_EXPECTED, query.toString()));
    }

    
    
    
    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "FILTER (?protein = <http://something.org>) . "
                + "}"; 
    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY_EXPECTED = "SELECT ?protein "
                + "WHERE {"
                + "FILTER (?protein = <http://something.org>) . "
                + "{?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 . "
                + "    FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "            ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) }. "
                + "}";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI and an existing
     * FILTER clause
     */
    @Test
    public void testMeet_oneBgpObjectUriWithFilter() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://something.org","http://something.org");
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BGP_OBJECT_WITH_FILTER_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BGP_OBJECT_WITH_FILTER_QUERY_EXPECTED, query.toString()));
    }

    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY_EXPECTED_FILTER_EXPANDED = "SELECT ?protein "
                + "WHERE {"
                + "FILTER (?protein = <http://www.another.org> || ?protein = <http://something.org>) . "
                + "{?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 . "
                + "    FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "            ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) }. "
                + "}";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI and an existing
     * FILTER clause
     */
    @Test
    public void testObjectAndFilter() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://something.org","http://www.another.org");
        dummyIRSMapper.addMapping("http://something.org","http://something.org");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BGP_OBJECT_WITH_FILTER_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BGP_OBJECT_WITH_FILTER_QUERY_EXPECTED_FILTER_EXPANDED, query.toString()));
    }
        
    static String ONE_BGP_SUBJECT_QUERY = "SELECT ?protein "
            + "WHERE {"
            + "<http://brenda-enzymes.info/1.1.1.1> <http://www.foo.com/predicate> ?protein . "
            + "}";
    static String ONE_BGP_SUBJECT_QUERY_EXPECTED = "SELECT ?protein "
            + "WHERE {"
            + "?subjectUri1 <http://www.foo.com/predicate> ?protein . "
            + "FILTER (?subjectUri1 = <http://example.com/983juy> || "
            + "?subjectUri1 = <http://bar.co.uk/liuw> || "
            + "?subjectUri1 = <http://brenda-enzymes.info/1.1.1.1>) . "
            + "}";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with a subject URI for which there 
     * are multiple matching URIs
     */
    @Test
    public void testMeet_oneBgpSubjectUriMultipleMatches() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://bar.co.uk/liuw");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BGP_SUBJECT_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BGP_SUBJECT_QUERY_EXPECTED, query.toString()));
    }

    static String DOUBLE_OR_QUERY = "SELECT ?stuff ?protein "
            + "WHERE {"
            + "?stuff <http://www.foo.com/predicate> ?protein . "
            + "FILTER (?stuff = <http://brenda-enzymes.info/1.1.1.1> || <http://Fishlink/123> = ?stuff)"
            + "}";
    static String DOUBLE_OR_QUERY_EXPECTED = "SELECT ?stuff ?protein "
            + "WHERE {"
            + "FILTER ((?stuff = <http://example.com/983juy> || ?stuff = <http://manchester.com/983juy> ||"
            + "?stuff = <http://brenda-enzymes.info/1.1.1.1>) ||"
            + "(?stuff = <http://Fishlink/456> || ?stuff = <http://Fishlink/123>))"
            + "?stuff <http://www.foo.com/predicate> ?protein . "
            + "}";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with a subject URI for which there 
     * are multiple matching URIs
     */
    @Test
    public void testMeet_DoubleOr() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://manchester.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://Fishlink/123","http://Fishlink/456");
        dummyIRSMapper.addMapping("http://Fishlink/123","http://Fishlink/123");

        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(DOUBLE_OR_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(DOUBLE_OR_QUERY_EXPECTED, query.toString()));
    }
    
    static String ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "FILTER (?protein != <http://my.org>) . "
                + "}"; 
    static String ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY_EXPECTED = "SELECT ?protein "
                + "WHERE {"
                + "FILTER (?protein != <http://my.org> && ?protein != <http://their.org> ) . "
                + "{?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 . "
                + "    FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "            ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) }. "
                + "}";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI and an existing
     * FILTER clause
     */
    @Test
    public void testMeet_oneBgpObjectUriWithNotFilter() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://my.org","http://my.org");
        dummyIRSMapper.addMapping("http://my.org","http://their.org");
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY_EXPECTED, query.toString()));
    }

    static String ONE_BGP_OBJECT_WITH_ANDDED_NOT_FILTER_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "FILTER (?protein != <http://my.org> && ?protein != <http://mars.com>) . "
                + "}"; 
    static String ONE_BGP_OBJECT_WITH_ANDDED_NOT_FILTER_QUERY_EXPECTED = "SELECT ?protein "
                + "WHERE {"
                + "FILTER ((?protein != <http://my.org> && ?protein != <http://their.org> ) . "
                + "(?protein != <http://earth.com> && ?protein != <http://mars.com> && ?protein != <http://war.com> ) . "
                + "{?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 . "
                + "    FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "            ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) }. "
                + "}";

    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI and an existing
     * FILTER clause
     */
    @Test
    public void testMeet_oneBgpObjectUriWithAnddedNotFilter() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://example.com/983juy");
        dummyIRSMapper.addMapping("http://brenda-enzymes.info/1.1.1.1","http://brenda-enzymes.info/1.1.1.1");
        dummyIRSMapper.addMapping("http://my.org","http://my.org");
        dummyIRSMapper.addMapping("http://my.org","http://their.org");
        dummyIRSMapper.addMapping("http://mars.com","http://earth.com");
        dummyIRSMapper.addMapping("http://mars.com","http://mars.com");
        dummyIRSMapper.addMapping("http://mars.com","http://war.com");
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(ONE_BGP_OBJECT_WITH_NOT_FILTER_QUERY_EXPECTED, query.toString()));
    }

    
    static String SIMPLE_UNION_QUERY = "PREFIX dc10:  <http://purl.org/dc/elements/1.0/> "
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?title "
            + "WHERE  { { ?book dc10:title ?title .  "
            + "?book dc10:creator <http://www.amazon.com/3432455> } "
            + "         UNION "
            + "         { ?book dc11:title ?title .  "
            + "?book dc11:creator <http://www.amazon.com/3445355> "
            + "} "
            + "        }";
    static String SIMPLE_UNION_QUERY_EXPECTED = "PREFIX dc10:  <http://purl.org/dc/elements/1.0/> " 
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?title "
            + "WHERE  { { ?book dc10:title ?title .  "
            + "{?book dc10:creator ?objectUri2 " 
            + "    FILTER (?objectUri2 = <http://barnes.com/983juy> || "
            + "            ?objectUri2 = <http://www.amazon.com/3432455>) }. "
            + "} "
            + "UNION {"
            + "?book dc11:title  ?title . "
            + "{?book dc11:creator ?objectUri4 . "
            + "     FILTER (?objectUri4 = <http://barnes.com/ku78s2w> || "
            + "             ?objectUri4 = <http://www.amazon.com/3445355>) }"
            +  "}}";

    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a union 
     */
    @Test
    public void testMeet_SimpleUnion() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.amazon.com/3432455","http://barnes.com/983juy");
        dummyIRSMapper.addMapping("http://www.amazon.com/3432455","http://www.amazon.com/3432455");
        dummyIRSMapper.addMapping("http://www.amazon.com/3445355","http://barnes.com/ku78s2w");
        dummyIRSMapper.addMapping("http://www.amazon.com/3445355","http://www.amazon.com/3445355");
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SIMPLE_UNION_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(SIMPLE_UNION_QUERY_EXPECTED, query.toString()));
    }

    static String SIMPLE_STAR_QUERY = "SELECT * "
            + "WHERE  {"
            + "   ?s ?p ?o"
            + "        }";
    static String SIMPLE_STAR_QUERY_EXPECTED = "SELECT ?s ?p ?o " 
            + "WHERE  {"
            + "   ?s ?p ?o"
            + "        }";
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a union 
     */
    @Test
    public void testMeet_SimpleStar() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(SIMPLE_STAR_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        //As itself
        assertTrue(QueryUtils.sameTupleExpr(SIMPLE_STAR_QUERY, query.toString()));
        //Same as expanded as the parser expands it.
        assertTrue(QueryUtils.sameTupleExpr(SIMPLE_STAR_QUERY_EXPECTED, query.toString()));
    }

    static String STAR_BOTH_URI_QUERY = "SELECT * "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "}";
    static String STAR_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri1 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri1 = <http://bar.info/u83hs> || "
                + "?objectUri1 = <http://foo.com/1.1.1.1>) "
                + "}";
    //In this case the star would be wrong as it brings in the filter variables.
    static String STAR_BOTH_URI_QUERY_NOT_EXPECTED_SINGLE_MATCH_EACH = "SELECT *"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri1 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri1 = <http://bar.info/u83hs> || "
                + "?objectUri1 = <http://foo.com/1.1.1.1>) "
                + "}";                      
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testStarSubjectOneObjectURIOneMatchEach() 
            throws MalformedQueryException, QueryModelExpanderException, UnexpectedQueryException {
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://result.com/90");
        dummyIRSMapper.addMapping("http://example.org/chem/8j392","http://example.org/chem/8j392");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://bar.info/u83hs");
        dummyIRSMapper.addMapping("http://foo.com/1.1.1.1","http://foo.com/1.1.1.1");
        
        IRSSPARQLExpand1 s = new IRSSPARQLExpand1(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternalWithExceptions(
                new SPARQLQueryImpl(STAR_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(STAR_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH, query.toString()));
        assertFalse(QueryUtils.sameTupleExpr(STAR_BOTH_URI_QUERY_NOT_EXPECTED_SINGLE_MATCH_EACH, query.toString()));
    }
}
