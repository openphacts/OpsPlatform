package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;


import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import java.util.Iterator;
import java.util.List;
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
import uk.ac.manchester.cs.irs.beans.Match;

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

    /**
     * Test that we do not do anything with CONSTRUCT queries
     */
    @Test
    public void testConstructQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = "CONSTRUCT { ?s ?p ?o } WHERE { ?o ?p ?s }";
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that we do not do anything with DESCRIBE queries
     */
    @Test
    public void testDescribeQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = "DESCRIBE <http://brenda-enzymes.info/1.1.1.1>";
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that we do not do anything with ASK queries
     */
    @Test
    public void testAskQuery() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        replayAll();
        String expectedResult = "ASK { ?s ?p ?o }";
        IRSSPARQLExpand expander = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invoke(new SPARQLQueryImpl(expectedResult).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    @Test
    public void testSpacing() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(
                new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(1).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/8hd83"));
        replayAll();

        String expectedResult = "SELECT ?book ?title WHERE{{?book <http://dc/title> ?title.}}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?book ?title WHERE{?book <http://dc/title> ?title.}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    @Test
    public void testPrefixes() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(
                new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(1).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/8hd83"));
        replayAll();

        String expectedResult = "PREFIX books: <http://example.org/book/> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT ?book ?title WHERE {{ ?book dc:title ?title . }}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "PREFIX books: <http://example.org/book/> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT ?book ?title WHERE { ?book dc:title ?title . }";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * object is expanded.
     */
    @Test
    public void testOneBGPOneObjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(
                new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(1).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/8hd83"));
        replayAll();

        String expectedResult = "SELECT ?protein"
                + " WHERE {{ "
                + "?protein <http://www.foo.org/somePredicate> "
                + "<http://foo.info/1.1.1.1> . } UNION { "
                + "?protein <http://www.foo.org/somePredicate> "
                + "<http://bar.com/8hd83> . }}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?protein"
                + " WHERE { "
                + "?protein <http://www.foo.org/somePredicate> <http://foo.info/1.1.1.1> . "
                + "}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject is expanded.
     */
    @Test
    public void testOneBGPOneSubjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        Match mockMatch = createMock(Match.class);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://foo.com/45273"))).andReturn(mockList);
        expect(mockList.size()).andReturn(2).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(2)
                .andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://bar.co.uk/346579"))
                .andReturn(new URIImpl("http://bar.ac.uk/19278"));
        replayAll();

        String expectedResult = "SELECT ?p ?o"
                + " WHERE {{"
                + " <http://foo.com/45273> "
                + " ?p "
                + " ?o . } UNION {"
                + " <http://bar.co.uk/346579> "
                + " ?p "
                + " ?o . } UNION {"
                + " <http://bar.ac.uk/19278> "
                + " ?p "
                + " ?o . }}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?p ?o"
                + " WHERE {"
                + " <http://foo.com/45273> "
                + " ?p "
                + " ?o . "
                + "}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        System.out.println("Expanded query:\n\t" + query.toString());
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIOneMatchEach() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://example.org/chem/8j392"))).andReturn(mockList);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(1).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://result.com/90"))
                .andReturn(new URIImpl("http://bar.info/u83hs"));
        replayAll();

        String expectedResult = "SELECT ?p"
                + " WHERE {{ "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION { "
                + "<http://result.com/90> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION { "
                + "<http://example.org/chem/8j392> ?p <http://bar.info/u83hs> . "
                + "} UNION { "
                + "<http://result.com/90> ?p <http://bar.info/u83hs> . "
                + "}}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?p "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . }";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * subject and object is expanded when there is one match for each URI.
     */
    @Test
    public void testOneBGPOneSubjectOneObjectURIMultipleMatches() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://example.org/chem/8j392"))).andReturn(mockList);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://foo.com/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(3).andReturn(2).times(0, 2);
        expect(mockList.iterator()).andReturn(mockIterator).times(2);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE)
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://result.com/90"))
                .andReturn(new URIImpl("http://somewhere.com/chebi/7s82"))
                .andReturn(new URIImpl("http://another.com/maps/hsjnc"))
                .andReturn(new URIImpl("http://bar.info/u83hs"))
                .andReturn(new URIImpl("http://onemore.co.uk/892k3"));
        replayAll();

        String expectedResult = "SELECT ?p"
                + " WHERE {{"
                + " <http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION {"
                + " <http://result.com/90> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION {"
                + " <http://somewhere.com/chebi/7s82> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION {"
                + " <http://another.com/maps/hsjnc> ?p <http://foo.com/1.1.1.1> . "
                + "} UNION {"
                + " <http://example.org/chem/8j392> ?p <http://bar.info/u83hs> . "
                + "} UNION {"
                + " <http://result.com/90> ?p <http://bar.info/u83hs> . "
                + "} UNION {"
                + " <http://somewhere.com/chebi/7s82> ?p <http://bar.info/u83hs> . "
                + "} UNION {"
                + " <http://another.com/maps/hsjnc> ?p <http://bar.info/u83hs> . "
                + "} UNION {"
                + " <http://example.org/chem/8j392> ?p <http://onemore.co.uk/892k3> . "
                + "} UNION {"
                + " <http://result.com/90> ?p <http://onemore.co.uk/892k3> . "
                + "} UNION {"
                + " <http://somewhere.com/chebi/7s82> ?p <http://onemore.co.uk/892k3> . "
                + "} UNION {"
                + " <http://another.com/maps/hsjnc> ?p <http://onemore.co.uk/892k3> . "
                + "}}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?p "
                + "WHERE { "
                + "<http://example.org/chem/8j392> ?p <http://foo.com/1.1.1.1> . }";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that a query with a two basic graph patterns with a single URI in the 
     * object is expanded.
     */
    @Test
    public void testTwoBGPOneObjectURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<URI> mockList = createMock(List.class);
        Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockIRS.getMatchesForURI(new URIImpl("http://foo.info/1.1.1.1"))).andReturn(mockList);
        expect(mockList.size()).andReturn(1).times(0, 1);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(new URIImpl("http://bar.com/9khd7"));
        replayAll();

        String expectedResult = "SELECT ?protein"
                + " WHERE {{"
                + "?protein <http://foo.com/somePredicate> <http://foo.info/1.1.1.1> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "} UNION {"
                + "?protein <http://foo.com/somePredicate> <http://bar.com/9khd7> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . }}";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?protein"
                + " WHERE {"
                + "?protein <http://foo.com/somePredicate> <http://foo.info/1.1.1.1> . "
                + "?protein <http://foo.com/anotherPredicate> ?name . "
                + "}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
}
