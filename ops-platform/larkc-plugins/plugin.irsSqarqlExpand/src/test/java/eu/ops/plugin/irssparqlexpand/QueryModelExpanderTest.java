/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.query.SPARQLQueryImpl;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.easymock.EasyMock.expect;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class QueryModelExpanderTest extends EasyMockSupport {

    private static Logger logger = LoggerFactory.getLogger(QueryModelExpanderTest.class);
    
    public QueryModelExpanderTest() {
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

    static String ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "}";

    static String ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_PLUS_FILTER =  "SELECT ?protein "
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
    @Ignore
    public void testMeet_oneBgpObjectUriMultipleMatches() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final Map<URI, List<URI>> mockMap = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockMap.get(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(mockList);
        expect(mockList.size()).andReturn(2);
        expect(mockList.add(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://example.com/983juy"))
                .andReturn(new URIImpl("http://brenda-enzymes.info/1.1.1.1"));
        replayAll();
        String expectedQuery = ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY_PLUS_FILTER;
        String query = ONE_BPG_OBJECT_MULTIPLE_MATCHES_QUERY;
        final ParsedQuery parsedQuery = new SPARQLQueryImpl(query).getParsedQuery();
        final TupleExpr tupleExpr = parsedQuery.getTupleExpr();
        QueryModelExpander qme = new QueryModelExpander(mockMap);
        tupleExpr.visit(qme);
System.out.println("**Tuple Expr:\n" + tupleExpr);
        String expandedQuery = QueryUtils.tupleExprToQueryString(tupleExpr);
System.out.println("**Expanded query:\n" + expandedQuery);        
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, expandedQuery));
    }

    static String ONE_BGP_OBJECT_WITH_FILTER_QUERY = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "FILTER (?protein = <http://something.org>) . "
                + "}"; 
    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with an object URI and an existing
     * FILTER clause
     */
    @Test@Ignore
    public void testMeet_oneBgpObjectUriWithFilter() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final Map<URI, List<URI>> mockMap = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockMap.get(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(mockList);
        expect(mockList.size()).andReturn(2);
        expect(mockList.add(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(2).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://example.com/983juy"))
                .andReturn(new URIImpl("http://brenda-enzymes.info/1.1.1.1"));
        replayAll();
        String expectedQuery = "SELECT ?protein "
                + "WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "?objectUri1 . "
                + "FILTER (?objectUri1 = <http://example.com/983juy> || "
                + "?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>) . "
                + "FILTER (?protein = <http://something.org>) . "
                + "}";
        String query = ONE_BGP_OBJECT_WITH_FILTER_QUERY;
        final ParsedQuery parsedQuery = new SPARQLQueryImpl(query).getParsedQuery();
        final TupleExpr tupleExpr = parsedQuery.getTupleExpr();
        QueryModelExpander qme = new QueryModelExpander(mockMap);
        tupleExpr.visit(qme);
System.out.println("**Tuple Expr:\n" + tupleExpr);
        String expandedQuery = QueryUtils.tupleExprToQueryString(tupleExpr);
System.out.println("**Expanded query:\n" + expandedQuery);        
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, expandedQuery));
    }

    /**
     * Test of meet method, of class QueryModelExpander.
     * 
     * Test a query involving a single BGP with a subject URI for which there 
     * are multiple matching URIs
     */
    @Test
    public void testMeet_oneBgpSubjectUriMultipleMatches() 
            throws QueryModelExpanderException, UnexpectedQueryException, MalformedQueryException {
        final Map<URI, List<URI>> mockMap = createMock(Map.class);
        final List<URI> mockList = createMock(List.class);
        final Iterator<URI> mockIterator = createMock(Iterator.class);
        expect(mockMap.get(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(mockList);
        expect(mockList.size()).andReturn(3);
        expect(mockList.add(new URIImpl("http://brenda-enzymes.info/1.1.1.1")))
                .andReturn(Boolean.TRUE);
        expect(mockList.iterator()).andReturn(mockIterator);
        expect(mockIterator.hasNext())
                .andReturn(Boolean.TRUE).times(3).andReturn(Boolean.FALSE);
        expect(mockIterator.next())
                .andReturn(new URIImpl("http://example.com/983juy"))
                .andReturn(new URIImpl("http://bar.co.uk/liuw"))
                .andReturn(new URIImpl("http://brenda-enzymes.info/1.1.1.1"));
        replayAll();
        String expectedQuery = "SELECT ?protein "
                + "WHERE {"
                + "?subjectUri1 <http://www.foo.com/predicate> ?protein . "
                + "FILTER (?subjectUri1 = <http://example.com/983juy> || "
                + "?subjectUri1 = <http://bar.co.uk/liuw> || "
                + "?subjectUri1 = <http://brenda-enzymes.info/1.1.1.1>) . "
                + "}";
        String query = "SELECT ?protein "
                + "WHERE {"
                + "<http://brenda-enzymes.info/1.1.1.1> <http://www.foo.com/predicate> ?protein . "
                + "}";
        final ParsedQuery parsedQuery = new SPARQLQueryImpl(query).getParsedQuery();
        final TupleExpr tupleExpr = parsedQuery.getTupleExpr();
        QueryModelExpander qme = new QueryModelExpander(mockMap);
        tupleExpr.visit(qme);
System.out.println("**Tuple Expr:\n" + tupleExpr);
        String expandedQuery = QueryUtils.tupleExprToQueryString(tupleExpr);
System.out.println("**Expanded query:\n" + expandedQuery);        
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, expandedQuery));
    }
//TODO: Complete testing of this class    
}
