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
import org.junit.Ignore;
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
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
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
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        
        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
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
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
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
    public void testSpacing() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>())).andReturn(new HashMap<URI, List<URI>>());
        replayAll();

        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(MINIMAL_SPACING_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(MINIMAL_SPACING_QUERY, query.toString());
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
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>()))
                .andReturn(new HashMap<URI, List<URI>>());
        replayAll();
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(PREFIX_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(PREFIX_QUERY, query.toString());
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
        expect(mockIRS.getMatchesForURIs(new HashSet<URI>()))
                .andReturn(new HashMap<URI, List<URI>>());
        replayAll();
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = NO_URI_QUERY;
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(NO_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(NO_URI_QUERY, query.toString());
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

        IRSSPARQLExpand s = new IRSSPARQLExpand(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SINGLE_OBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SINGLE_OBJECT_URI_QUERY_EXPECTED, query.toString());
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
                + "FILTER (?subjectUriLine1 = <http://bar.co.uk/346579> || "
                + "?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "?subjectUri1 = <http://foo.com/45273>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SINGLE_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SINGLE_SUBJECT_URI_QUERY_EXPECTED, query.toString());
    }

    
    
    
    static String SINGLE_BOTH_URI_QUERY = "SELECT ?p "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "}";
    static String SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri2 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri2 = <http://bar.info/u83hs> || "
                + "?objectUri2 = <http://foo.com/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(
                new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SINGLE_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SINGLE_BOTH_URI_QUERY_EXPECTED_SINGLE_MATCH_EACH, query.toString());
    }

    
    
    
    static String SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES = "SELECT ?p"
                + " WHERE {"
                + "?subjectUri1 ?p ?objectUri2 . "
                + "FILTER (?subjectUri1 = <http://result.com/90> || "
                + "?subjectUri1 = <http://somewhere.com/chebi/7s82> || "
                + "?subjectUri1 = <http://another.com/maps/hsjnc> || "
                + "?subjectUri1 = <http://example.org/chem/8j392>) "
                + "FILTER (?objectUri2 = <http://bar.info/u83hs> || "
                + "?objectUri2 = <http://onemore.co.uk/892k3> || "
                + "?objectUri2 = <http://foo.com/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SINGLE_BOTH_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SINGLE_BOTH_URI_QUERY_EXPECTED_MULTIPLE_MATCHES, query.toString());
    }

    
    
    
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> <http://foo.info/1.1.1.1> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED =  "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> ?objectUri1 . "
                + "FILTER (?objectUri1 = <http://bar.com/9khd7> || "
                + "?objectUri1 = <http://foo.info/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(TWO_STATEMENTS_ONE_OBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(TWO_STATEMENTS_ONE_OBJECT_URI_QUERY_EXPECTED, query.toString());
    }
    
    
    
    
    static String SHARED2_SUBJECT_URI_QUERY = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "}";    
    static String SHARED2_SUBJECT_URI_QUERY_EXPECTED = "SELECT ?protein ?name "
                + "WHERE {"
                + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "?subjectUri2 = <http://foo.info/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SHARED2_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SHARED2_SUBJECT_URI_QUERY_EXPECTED, query.toString());
    }
    
    
    
    
    static String SHARED3_SUBJECT_URI_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name . "
                + "<http://foo.info/1.1.1.1> <http://bar.org/relation> ?enzyme . "
                + "}";    
    static String SHARED3_SUBJECT_URI_QUERY_EXPECTED = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "?subjectUri1 = <http://example.ac.uk/89ke> || "
                + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "?subjectUri2 = <http://example.ac.uk/89ke> || "
                + "?subjectUri2 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri3 <http://bar.org/relation> ?enzyme . "
                + "FILTER (?subjectUri3 = <http://bar.com/9khd7> || "
                + "?subjectUri3 = <http://example.ac.uk/89ke> || "
                + "?subjectUri3 = <http://foo.info/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SHARED3_SUBJECT_URI_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SHARED3_SUBJECT_URI_QUERY_EXPECTED, query.toString());
    }

    
    
    
    static String SIMPLE_CHAIN_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "?protein <http://foo.com/somePredicate> <http://example.org/chem/2918> . "
                + "<http://example.org/chem/2918> <http://foo.com/anotherPredicate> ?name . "
                + "}";
    static String SIMPLE_CHAIN_QUERY_EXPECTED = "SELECT ?protein ?name "
                + "WHERE {"
                + "?protein <http://foo.com/somePredicate> ?objectUri1 . "
                + "FILTER (?objectUri1 = <http://bar.com/9khd7> || "
                + "?objectUri1 = <http://foo.info/1.1.1.1> || "
                + "?objectUri1 = <http://example.org/chem/2918>) "
                + "?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "?subjectUri2 = <http://foo.info/1.1.1.1> || "
                + "?subjectUri2 = <http://example.org/chem/2918>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SIMPLE_CHAIN_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SIMPLE_CHAIN_QUERY_EXPECTED, query.toString());
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
                + "?subjectUri1 <http://foo.com/somePredicate> ?objectUri2 . "
                + "FILTER (?subjectUri1 = <http://foo.info/1.1.1.1> || "
                + "?subjectUri1 = <http://bar.co.uk/998234>) "               
                + "FILTER (?objectUri2 = <http://bar.com/9khd7> || "
                + "?objectUri2 = <http://hello.uk/87234> || "
                + "?objectUri2 = <http://example.org/chem/2918>) "                
                + "?subjectUri3 <http://foo.com/predicate> ?value1 . "                
                + "FILTER (?subjectUri3 = <http://foo.info/1.1.1.1> || "
                + "?subjectUri3 = <http://bar.co.uk/998234>) "                
                + "?subjectUri4 <http://foo.com/anotherPredicate> ?name . "                
                + "FILTER (?subjectUri4 = <http://bar.com/9khd7> || "
                + "?subjectUri4 = <http://hello.uk/87234> || "
                + "?subjectUri4 = <http://example.org/chem/2918>) "                
                + "?subjectUri5 <http://foo.com/aPredicate> ?objectUri4 . "                
                + "FILTER (?subjectUri5 = <http://bar.com/9khd7> || "
                + "?subjectUri5 = <http://hello.uk/87234> || "
                + "?subjectUri5 = <http://example.org/chem/2918>) "
                + "FILTER (?objectUri6 = <http://yetmore.info/872342> || "
                + "?objectUri6 = <http://ohboy.com/27393> || "
                + "?objectUri6 = <http://imborednow.co/akuhe8> || "
                + "?objectUri6 = <http://yetanother.com/-09824>) "                
                + "?subjectUri7 <http://bar.org/predicate> ?value2 . "
                + "FILTER (?subjectUri7 = <http://yetmore.info/872342> || "
                + "?subjectUri7 = <http://ohboy.com/27393> || "
                + "?subjectUri7 = <http://imborednow.co/akuhe8> || "
                + "?subjectUri7 = <http://yetanother.com/-09824>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(COMPLEX_CHAIN_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(COMPLEX_CHAIN_QUERY_EXPECTED, query.toString());
    }

    
    
    
    static String REPEATED_SUBJECT_SHORTHAND_QUERY = "SELECT ?protein ?name ?enzyme "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein ; "
                + "<http://foo.com/anotherPredicate> ?name ; "
                + "<http://bar.org/relation> ?enzyme . "
                + "}"; 
    static String REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED = "SELECT ?protein ?name ?enzyme "
                + "WHERE {"
                + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "?subjectUri1 = <http://example.org/chem/2918> || "
                + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "?subjectUri2 = <http://example.org/chem/2918> || "
                + "?subjectUri2 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri3 <http://bar.org/relation> ?enzyme . "
                + "FILTER (?subjectUri3 = <http://bar.com/9khd7> || "
                + "?subjectUri3 = <http://example.org/chem/2918> || "
                + "?subjectUri3 = <http://foo.info/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(REPEATED_SUBJECT_SHORTHAND_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(REPEATED_SUBJECT_SHORTHAND_QUERY_EXPECTED, query.toString());
    }
    
    
    
    static String REPEATED_SUBJECT_PREDICATE_QUERY =  "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein , ?name . "
                + "}";   
    static String REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED =  "SELECT ?protein ?name "
                + "WHERE {"
                + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
                + "?subjectUri1 = <http://example.org/chem/2918> || "
                + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
                + "?subjectUri2 <http://foo.com/somePredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
                + "?subjectUri2 = <http://example.org/chem/2918> || "
                + "?subjectUri2 = <http://foo.info/1.1.1.1>) "
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(REPEATED_SUBJECT_PREDICATE_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(REPEATED_SUBJECT_PREDICATE_QUERY_EXPECTED, query.toString());
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
                + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://example.com/9khd7> || "
                + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
                + "OPTIONAL {?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
                + "FILTER (?subjectUri2 = <http://another.org/82374> || "
                + "?subjectUri2 = <http://bar.com/ijdu>) } "
                + "}";
    /**
     * Test that a query involving an optional clause is output correctly.
     * 
     * OPTIONALS can be dealt with in the same way as BGPs, but need appropriate
     * query string generation
     */
    @Test
    public void testOptionalQuery_Simple() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        final Map<URI, List<URI>> mockMappings = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURIs(EasyMock.isA(Set.class))).andReturn(mockMappings);
        expect(mockMappings.isEmpty()).andReturn(Boolean.FALSE);
        expect(mockMappings.get(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(Boolean.TRUE);
        expect(mockMappings.get(new URIImpl("http://bar.com/ijdu"))).andReturn(mockList);
        expect(mockList.add(new URIImpl("http://bar.com/ijdu"))).andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://example.com/9khd7"))
                .andReturn(new URIImpl("http://foo.info/1.1.1.1"))
                .andReturn(new URIImpl("http://another.org/82374"))
                .andReturn(new URIImpl("http://bar.com/ijdu"));
        replayAll();
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(SIMPLE_OPTIONAL_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(SIMPLE_OPTIONAL_QUERY, query.toString());
    }
    
    
    
    
    static String ONLY_OPTIONAL_STATEMENTS_QUERY = "SELECT ?protein ?name "
            + "WHERE { "
            + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . }"
            + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
            + "}";
    static String ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED = "SELECT ?protein ?name "
            + "WHERE {"
            + "OPTIONAL {?subjectUri1 <http://foo.com/somePredicate> ?protein . "
            + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
            + "?subjectUri1 = <http://foo.info/1.1.1.1>) } "
            + "OPTIONAL {?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
            + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
            + "?subjectUri2 = <http://foo.info/1.1.1.1>) } "
            + "}";
    /**
     * Test that a query involving only optional clauses is output correctly.
     */
    @Test@Ignore
    public void testOptionalQuery_BothOptional() {
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
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(ONLY_OPTIONAL_STATEMENTS_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(ONLY_OPTIONAL_STATEMENTS_QUERY_EXPECTED, query.toString());
    }
    
    
    
    
    String OPTIONAL_REPEATED_SUBJECT_QUERY = "SELECT ?protein ?name "
            + "WHERE { "
            + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
            + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/anotherPredicate> ?name .} "
            + "}";
    String OPTIONAL_REPEATED_SUBJECT_QUERY_EXPECTED = "SELECT ?protein ?name "
            + "WHERE {"
            + "?subjectUri1 <http://foo.com/somePredicate> ?protein . "
            + "FILTER (?subjectUri1 = <http://bar.com/9khd7> || "
            + "?subjectUri1 = <http://foo.info/1.1.1.1>) "
            + "OPTIONAL {?subjectUri2 <http://foo.com/anotherPredicate> ?name . "
            + "FILTER (?subjectUri2 = <http://bar.com/9khd7> || "
            + "?subjectUri2 = <http://foo.info/1.1.1.1>)} "
            + "}";
    /**
     * Test that a query with a repeated subject URI involving an optional clause 
     * is output correctly.
     * 
     */
    @Test@Ignore
    public void testOptionalQuery_repeatedSubjectUri() {
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
    
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            @Override
            IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        SetOfStatements eQuery = s.invokeInternal(
                new SPARQLQueryImpl(OPTIONAL_REPEATED_SUBJECT_QUERY).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(OPTIONAL_REPEATED_SUBJECT_QUERY_EXPECTED, query.toString());
    }
    
    //TODO: Test queries with FILTERS already in them

}
