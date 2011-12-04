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
    
    /**
     * Test that a query with a single basic graph pattern with a URI in the 
     * object is expanded.
     */
    @Test
    public void testOneBGPOneURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<Match> mockList = createMock(List.class);
        Iterator<Match> mockIterator = createMock(Iterator.class);
        Match mockMatch = createMock(Match.class);
        expect(mockIRS.getMatchesForURI("http://brenda-enzymes.info/1.1.1.1")).andReturn(mockList);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(mockMatch);
        expect(mockMatch.getMatchUri()).andReturn("http://equivalent.uri");
        replayAll();

        String expectedResult = "SELECT ?protein"
                + " WHERE { {"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> .  }  UNION  { "
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://equivalent.uri> .  }  }";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?protein"
                + " WHERE {"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + " <http://brenda-enzymes.info/1.1.1.1> . "
                + "}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }

    /**
     * Test that a query with a two basic graph patterns with a single URI in the 
     * object is expanded.
     */
    @Test
    public void testTwoBGPOneURI() {
        final IRSClient mockIRS = createMock(IRSClient.class);
        List<Match> mockList = createMock(List.class);
        Iterator<Match> mockIterator = createMock(Iterator.class);
        Match mockMatch = createMock(Match.class);
        expect(mockIRS.getMatchesForURI("http://brenda-enzymes.info/1.1.1.1")).andReturn(mockList);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext()).andReturn(Boolean.TRUE).andReturn(Boolean.FALSE);
        expect(mockIterator.next()).andReturn(mockMatch);
        expect(mockMatch.getMatchUri()).andReturn("http://equivalent.uri");
        replayAll();

        String expectedResult = "SELECT ?protein"
                + " WHERE { {"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name .  }  UNION  { "
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://equivalent.uri> . "
                + "?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name .  }  }";
        
        IRSSPARQLExpand s = new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand")) {
            protected IRSClient instantiateIRSClient() {
                return mockIRS;
            }
        };
        s.initialiseInternal(null);
        String qStr = "SELECT ?protein"
                + " WHERE {"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + " <http://brenda-enzymes.info/1.1.1.1> . "
                + " ?protein <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "}";
        SetOfStatements eQuery = s.invokeInternal(new SPARQLQueryImpl(qStr).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertEquals(expectedResult, query.toString());
    }
    
}
