package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for your LarKC plug-in.
 */
public class IRSSPARQLExpandTest
        extends EasyMockSupport {

    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);

    public IRSSPARQLExpandTest() {
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
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = CONSTRUCT_QUERY;
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String DESSCRIBE_QUERY = "DESCRIBE <http://brenda-enzymes.info/1.1.1.1>";
    /**
     * Test that we do not do anything with DESCRIBE queries
     */
    @Test
    public void testDescribeQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = DESSCRIBE_QUERY;
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String ASK_QUERY = "ASK { ?s ?p ?o }";
    /**
     * Test that we do not do anything with ASK queries
     */
    @Test
    public void testAskQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = ASK_QUERY;
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String MINIMAL_SPACING_QUERY = "SELECT ?book ?title WHERE{?book <http://dc/title> ?title.}";
    /**
     * Test that a query with minimal spacing in its text is processed correctly
     */
    @Test
    public void testSpacing() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>())).andReturn(new HashMap<URI, List<URI>>());
        replayAll();

        String expectedResult = MINIMAL_SPACING_QUERY;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = MINIMAL_SPACING_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String PREFIX_QUERY = "PREFIX books: <http://example.org/book/> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT ?book ?title WHERE { ?book dc:title ?title . }";
    
    /**
     * Test that a query involving prefixes is output correctly
     */
    @Test
    public void testPrefixes() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>())).andReturn(new HashMap<URI, List<URI>>());
        replayAll();

        String expectedResult = PREFIX_QUERY;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = PREFIX_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String OPTIONAL_QUERY = "SELECT ?book ?title ?author WHERE { "
                + "?book <http://dc.com/title> ?title . "
                + "OPTIONAL { ?book <http://dc.org/author> ?author .} "
                + "}";
    
    /**
     * Test that a query involving an optional clause is output correctly.
     * 
     * Currently we do nothing with these queries.
     */
    @Test
    public void testOptionalQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();

        String expectedResult = OPTIONAL_QUERY;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = OPTIONAL_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String NO_URI_QUERY = "SELECT ?book ?author WHERE { "
                + "?book <http://dc.org/author> ?author . "
                + "}";
    /**
     * Test that nothing happens to a query without any URIs present.
     */
    @Test
    public void testQueryWithoutURIs() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>())).andReturn(new HashMap<URI, List<URI>>());
        replayAll();

        String expectedResult = NO_URI_QUERY;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = NO_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String SINGLE_OBJECT_URI_QUERY = "SELECT ?protein"
                + " WHERE { "
                + "?protein <http://www.foo.org/somePredicate> <http://foo.info/1.1.1.1> . "
                + "}";
    static String SINGLE_OBJECT_URI_QUERY_PLUS_FILTER = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "}";
            
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * object is expanded.
     */
    @Test
    public void testOneBGPOneObjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        Map<URI, List<URI>> mockMappings = createMock(Map.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        List<URI> mockList = createMock(List.class);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.com/8hd83"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = SINGLE_OBJECT_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SINGLE_OBJECT_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String SINGLE_SUBJECT_URI_QUERY = "SELECT ?p ?o"
                + " WHERE {"
                + " <http://foo.com/45273> "
                + " ?p "
                + " ?o . "
                + "}";
    static String SINGLE_SUBJECT_URI_QUERY_PLUS_FILTER =  "SELECT ?p ?o"
                + " WHERE {"
                + "?subjectUriLine1 ?p ?o . "
                + "FILTER (?subjectUriLine1 = <http://bar.co.uk/346579> || "
                + "?subjectUriLine1 = <http://bar.ac.uk/19278> || "
                + "?subjectUriLine1 = <http://foo.com/45273>) "
                + "}"; 
    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject is expanded.
     */
    @Test
    public void testOneBGPOneSubjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        Map<URI, List<URI>> mockMappings = createMock(Map.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        List<URI> mockList = createMock(List.class);
        expect(mockMappings.get(new URIImpl("http://foo.com/45273"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.com/45273"))).andReturn(Boolean.TRUE);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(3)
                .andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.co.uk/346579"))
                .andReturn(new URIImpl("http://bar.ac.uk/19278"))
                .andReturn(new URIImpl("http://foo.com/45273"));
        replayAll();

        String expectedResult = SINGLE_SUBJECT_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SINGLE_SUBJECT_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String SINGLE_BOTH_URI_QUERY = "SELECT ?p "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "}";
    static String SINGLE_BOTH_URI_QUERY_PLUS_FILTER = "SELECT ?p"
                + " WHERE {"
                + "?subjectUriLine1 ?p ?objectUriLine1 . "
                + "FILTER (?subjectUriLine1 = <http://result.com/90> || "
                + "?subjectUriLine1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUriLine1 = <http://bar.info/u83hs> || "
                + "?objectUriLine1 = <http://foo.com/1.1.1.1>) "
                + "}";
            
            
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIOneMatchEach() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        final List<URI> mockList = createMock(List.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://example.org/chem/8j392"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://example.org/chem/8j392"))).andReturn(Boolean.TRUE);        
        expect(mockMappings.get(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(Boolean.TRUE);        
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://result.com/90"))
                .andReturn(new URIImpl("http://example.org/chem/8j392"))
                .andReturn(new URIImpl("http://bar.info/u83hs"))
                .andReturn(new URIImpl("http://foo.com/1.1.1.1"));
        replayAll();

        String expectedResult = SINGLE_BOTH_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SINGLE_BOTH_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String SINGLE_BOTH_URI_QUERY_PLUS_4FILTER = "SELECT ?p"
                + " WHERE {"
                + "?subjectUriLine1 ?p ?objectUriLine1 . "
                + "FILTER (?subjectUriLine1 = <http://result.com/90> || "
                + "?subjectUriLine1 = <http://somewhere.com/chebi/7s82> || "
                + "?subjectUriLine1 = <http://another.com/maps/hsjnc> || "
                + "?subjectUriLine1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUriLine1 = <http://bar.info/u83hs> || "
                + "?objectUriLine1 = <http://onemore.co.uk/892k3> || "
                + "?objectUriLine1 = <http://foo.com/1.1.1.1>) "
                + "}";
    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is more than one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIMultipleMatches() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://example.org/chem/8j392"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://example.org/chem/8j392"))).andReturn(Boolean.TRUE);
        expect(mockMappings.get(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(4).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://result.com/90"))
                .andReturn(new URIImpl("http://somewhere.com/chebi/7s82"))
                .andReturn(new URIImpl("http://another.com/maps/hsjnc"))
                .andReturn(new URIImpl("http://example.org/chem/8j392"))
                .andReturn(new URIImpl("http://bar.info/u83hs"))
                .andReturn(new URIImpl("http://onemore.co.uk/892k3"))
                .andReturn(new URIImpl("http://foo.com/1.1.1.1"));
        replayAll();

        String expectedResult = SINGLE_BOTH_URI_QUERY_PLUS_4FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SINGLE_BOTH_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> <http://foo.info/1.1.1.1> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_PLUS_FILTER =  "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> ?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/9khd7> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
    
    /**
     * Test that a query with a two basic graph patterns with a single URI in the 
     * object is expanded.
     */
    @Test
    public void testTwoBGPOneObjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = TWO_STATEMENTS_ONE_OBJECT_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String SHARED2_SUBJECT_URI_QUERY = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "}";    
    static String SHARED2_SUBJECT_URI_QUERY_PLUS_FILTER = "SELECT ?protein ?name "
                + "WHERE {"
                + "?subjectUriLine1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUriLine1 = <http://bar.com/9khd7> || "
                + "?subjectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUriLine2 = <http://bar.com/9khd7> || "
                + "?subjectUriLine2 = <http://foo.info/1.1.1.1>) "
                + "}";
    /**
     * Test that a query with a two basic graph patterns which share a single 
     * subject URI is expanded to every combination.
     */
    @Test
    public void testTwoBGPShareSubjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList).times(2);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE).times(2);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = SHARED2_SUBJECT_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SHARED2_SUBJECT_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String SHARED3_SUBJECT_URI_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "<http://foo.info/1.1.1.1> <http://bar.org/relation> ?enzyme . "
                + "}";    
    static String SHARED3_SUBJECT_URI_QUERY_PLUS_FILTER = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "?subjectUriLine1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUriLine1 = <http://bar.com/9khd7> || "
                + "?subjectUriLine1 = <http://example.ac.uk/89ke> || "
                + "?subjectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUriLine2 = <http://bar.com/9khd7> || "
                + "?subjectUriLine2 = <http://example.ac.uk/89ke> || "
                + "?subjectUriLine2 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine3 <http://bar.org/relation> ?enzyme . "
                + "FILTER (?subjectUriLine3 = <http://bar.com/9khd7> || "
                + "?subjectUriLine3 = <http://example.ac.uk/89ke> || "
                + "?subjectUriLine3 = <http://foo.info/1.1.1.1>) "
                + "}";

    /**
     * Test that a query with a three basic graph patterns which share a single 
     * subject URI is expanded to every combination.
     */
    @Test
    public void testThreeBGPShareSubjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList).times(3);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE).times(3);
        expect(mockList.iterator()).andReturn(mockIterator).times(3);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.ac.uk/89ke"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.ac.uk/89ke"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.ac.uk/89ke"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = SHARED3_SUBJECT_URI_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SHARED3_SUBJECT_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String SIMPLE_CHAIN_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "?protein <http://foo.com/somePredicate> <http://example.org/chem/2918> . "
                + "<http://example.org/chem/2918> <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String SIMPLE_CHAIN_QUERY_PLUS_FILTERS = "SELECT ?protein ?name "
                + "WHERE {"
                + "?protein <http://foo.com/somePredicate> ?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/9khd7> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1> || "
                + "?objectUriLine1 = <http://example.org/chem/2918>) "
                + "?subjectUriLine2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUriLine2 = <http://bar.com/9khd7> || "
                + "?subjectUriLine2 = <http://foo.info/1.1.1.1> || "
                + "?subjectUriLine2 = <http://example.org/chem/2918>) "
                + "}";
            
    /**
     * Test that a query with a two basic graph patterns which form a chain
     * based on the object URI of one with the subject URI of the second.
     */
    @Test
    public void testBGPChainSimple() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://example.org/chem/2918"))).andReturn(mockList).times(2);
        expect(mockList.add(new URIImpl("http://example.org/chem/2918"))).andReturn(Boolean.TRUE).times(2);
        expect(mockList.iterator()).andReturn(mockIterator).times(3);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://example.org/chem/2918"));        
        replayAll();

        String expectedResult = SIMPLE_CHAIN_QUERY_PLUS_FILTERS;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = SIMPLE_CHAIN_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String COMPLEX_CHAIN_QUERY = "SELECT ?name ?value1 ?value2 "
                + "WHERE { "
                + "<http://bar.co.uk/998234> <http://foo.com/somePredicate> <http://example.org/chem/2918> . "
                + "<http://bar.co.uk/998234> <http://foo.com/predicate> ?value1 . "
                + "<http://example.org/chem/2918> <http://foo.com/anotherPredicate> ?name . "
                + "<http://example.org/chem/2918> <http://foo.com/aPredicate> <http://yetanother.com/-09824> ."
                + "<http://yetanother.com/-09824> <http://bar.org/predicate> ?value2 . "
                + "}";
    static String COMPLEX_CHAIN_QUERY_PLUS_FILTER = "SELECT ?name ?value1 ?value2 "
                + "WHERE {"
                + "?subjectUriLine1 <http://foo.com/somePredicate> ?objectUriLine1 . "
                + "FILTER (?subjectUriLine1 = <http://foo.info/1.1.1.1> || "
                + "?subjectUriLine1 = <http://bar.co.uk/998234>) "               
                + "FILTER (?objectUriLine1 = <http://bar.com/9khd7> || "
                + "?objectUriLine1 = <http://hello.uk/87234> || "
                + "?objectUriLine1 = <http://example.org/chem/2918>) "                
                + "?subjectUriLine2 <http://foo.com/predicate> ?value1 . "                
                + "FILTER (?subjectUriLine2 = <http://foo.info/1.1.1.1> || "
                + "?subjectUriLine2 = <http://bar.co.uk/998234>) "                
                + "?subjectUriLine3 <http://foo.com/anotherPredicate> ?name . "                
                + "FILTER (?subjectUriLine3 = <http://bar.com/9khd7> || "
                + "?subjectUriLine3 = <http://hello.uk/87234> || "
                + "?subjectUriLine3 = <http://example.org/chem/2918>) "                
                + "?subjectUriLine4 <http://foo.com/aPredicate> ?objectUriLine4 . "                
                + "FILTER (?subjectUriLine4 = <http://bar.com/9khd7> || "
                + "?subjectUriLine4 = <http://hello.uk/87234> || "
                + "?subjectUriLine4 = <http://example.org/chem/2918>) "
                + "FILTER (?objectUriLine4 = <http://yetmore.info/872342> || "
                + "?objectUriLine4 = <http://ohboy.com/27393> || "
                + "?objectUriLine4 = <http://imborednow.co/akuhe8> || "
                + "?objectUriLine4 = <http://yetanother.com/-09824>) "                
                + "?subjectUriLine5 <http://bar.org/predicate> ?value2 . "
                + "FILTER (?subjectUriLine5 = <http://yetmore.info/872342> || "
                + "?subjectUriLine5 = <http://ohboy.com/27393> || "
                + "?subjectUriLine5 = <http://imborednow.co/akuhe8> || "
                + "?subjectUriLine5 = <http://yetanother.com/-09824>) "
                + "}";
            
    /**
     * Test that a query with a several basic graph patterns which form a series
     * of chains based on the object URI of one BGP being the subject URI of 
     * another BGP.
     */
    @Test
    public void testBGPChainComplex() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList1 = createMock(List.class);
        final List<URI> mockList2 = createMock(List.class);
        final List<URI> mockList3 = createMock(List.class);
        final Iterator<URI> mockIterator1 = createMock(Iterator.class);
        final Iterator<URI> mockIterator2 = createMock(Iterator.class);
        final Iterator<URI> mockIterator3 = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://bar.co.uk/998234"))).andReturn(mockList1).times(2);
        expect(mockList1.add(new URIImpl("http://bar.co.uk/998234"))).andReturn(Boolean.TRUE).times(2);
        expect(mockMappings.get(new URIImpl("http://example.org/chem/2918"))).andReturn(mockList2).times(3);
        expect(mockList2.add(new URIImpl("http://example.org/chem/2918"))).andReturn(Boolean.TRUE).times(3);
        expect(mockMappings.get(new URIImpl("http://yetanother.com/-09824"))).andReturn(mockList3).times(2);
        expect(mockList3.add(new URIImpl("http://yetanother.com/-09824"))).andReturn(Boolean.TRUE).times(2);
        expect(mockList1.iterator()).andReturn(mockIterator1).times(2);
        expect(mockList2.iterator()).andReturn(mockIterator2).times(3);
        expect(mockList3.iterator()).andReturn(mockIterator3).times(3);
        expect(mockIterator1.hasNext())
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator1.next())
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.co.uk/998234"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.co.uk/998234"));
        expect(mockIterator2.hasNext())
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator2.next())
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://hello.uk/87234"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://hello.uk/87234"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://hello.uk/87234"))
                .andReturn(new URIImpl("http://example.org/chem/2918"));
        expect(mockIterator3.hasNext())
                .andReturn(Boolean.TRUE).times(4).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(4).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(4).andReturn(Boolean.FALSE);
        expect(mockIterator3.next())
                .andReturn(new URIImpl("http://yetmore.info/872342"))
                .andReturn(new URIImpl("http://ohboy.com/27393"))
                .andReturn(new URIImpl("http://imborednow.co/akuhe8"))
                .andReturn(new URIImpl("http://yetanother.com/-09824"))
                .andReturn(new URIImpl("http://yetmore.info/872342"))
                .andReturn(new URIImpl("http://ohboy.com/27393"))
                .andReturn(new URIImpl("http://imborednow.co/akuhe8"))
                .andReturn(new URIImpl("http://yetanother.com/-09824"))
                .andReturn(new URIImpl("http://yetmore.info/872342"))
                .andReturn(new URIImpl("http://ohboy.com/27393"))
                .andReturn(new URIImpl("http://imborednow.co/akuhe8"))
                .andReturn(new URIImpl("http://yetanother.com/-09824"));
        replayAll();

        String expectedResult = COMPLEX_CHAIN_QUERY_PLUS_FILTER; 
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = COMPLEX_CHAIN_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    static String REPEATED_SUBJECT_SHORTHAND_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein ; "
                + "<http://foo.com/anotherPredicate> ?name ; "
                + "<http://bar.org/relation> ?enzyme . "
                + "}"; 
    static String REPEATED_SUBJECT_SHORTHAND_QUERY_PLUS_FILTER = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "?subjectUriLine1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUriLine1 = <http://bar.com/9khd7> || "
                + "?subjectUriLine1 = <http://example.org/chem/2918> || "
                + "?subjectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUriLine2 = <http://bar.com/9khd7> || "
                + "?subjectUriLine2 = <http://example.org/chem/2918> || "
                + "?subjectUriLine2 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine3 <http://bar.org/relation> ?enzyme . "
                + "FILTER (?subjectUriLine3 = <http://bar.com/9khd7> || "
                + "?subjectUriLine3 = <http://example.org/chem/2918> || "
                + "?subjectUriLine3 = <http://foo.info/1.1.1.1>) "
                + "}";
    
    /**
     * Test that a query written using the shorthand for repeated subject URI
     * gets expanded correctly.
     */
    @Test
    public void testRepeatedSubjectShorthandQuery(){
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList).times(3);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE).times(3);
        expect(mockList.iterator()).andReturn(mockIterator).times(3);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = REPEATED_SUBJECT_SHORTHAND_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = REPEATED_SUBJECT_SHORTHAND_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    static String REPEATED_SUBJECT_PREDICATE_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein , ?name . "
                + "}";   
    static String REPEATED_SUBJECT_PREDICATE_QUERY_PLUS_FILTER =  "SELECT ?protein ?name "
                + "WHERE {"
                + "?subjectUriLine1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUriLine1 = <http://bar.com/9khd7> || "
                + "?subjectUriLine1 = <http://example.org/chem/2918> || "
                + "?subjectUriLine1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUriLine2 <http://foo.com/somePredicate> ?name . "
                + "FILTER (?subjectUriLine2 = <http://bar.com/9khd7> || "
                + "?subjectUriLine2 = <http://example.org/chem/2918> || "
                + "?subjectUriLine2 = <http://foo.info/1.1.1.1>) "
                + "}"; 
    
    /**
     * Test that a query written using the shorthand for repeated subject and
     * predicate URIs gets expanded correctly.
     */
    @Test
    public void testRepeatedSubjectPredicateShorthandQuery(){
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList).times(2);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE).times(2);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://bar.com/9khd7"))
                .andReturn(new URIImpl("http://example.org/chem/2918"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"));
        replayAll();

        String expectedResult = REPEATED_SUBJECT_PREDICATE_QUERY_PLUS_FILTER;
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = REPEATED_SUBJECT_PREDICATE_QUERY;
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
   
    //TODO: Write test for OPTIONAL BGP expansion
    //TODO: Write test for OPTIONAL set of BGPs expansion
}
