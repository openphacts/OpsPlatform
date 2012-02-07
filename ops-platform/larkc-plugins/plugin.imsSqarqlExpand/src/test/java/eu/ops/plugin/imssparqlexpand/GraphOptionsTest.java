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
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test all the queries found in https://wiki.openphacts.org/index.php/Core_API
 * 
 * @author Christian
 * @version Jan 4 2012
 */
public class GraphOptionsTest {
        
    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);
    private IMSSPARQLExpand expander;
    
    public GraphOptionsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://input.com#1" ,
                                  "http://replace.org#1");
        dummyIMSMapper.addMapping("http://input.com#1" ,
                                  "http://replace.org#2");

        expander = new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
     }

    @After
    public void tearDown() {
    }
     
    @Test
    public void testSimple() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?first "
                + "WHERE"
                + "  { <http://input.com#1> foaf:name ?name ."
                + "    <http://input.com#1> foaf:mbox ?mbox ."
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n"
                + "    ?replacedURI2 foaf:mbox ?mbox. \n "
                + "    FILTER (?replacedURI2 = <http://replace.org#1> "
                + "         || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNoneOptionalNone() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?first "
                + "WHERE"
                + "  { <http://input.com#1> foaf:name ?name ."
                + "Optional {"
                + "    <http://input.com#1> foaf:mbox ?mbox . "
                + "}"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "         FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
 
    @Test
    public void testNoneGraphNone() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?first "
                + "WHERE {"
                + "  <http://input.com#1> foaf:name ?name ."
                + "  GRAPH  <http://foo.com> {"
                + "    <http://input.com#1> foaf:mbox ?mbox . "
                + "}"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> {"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "         FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNoneGraphOptionalNone() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?first "
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        Optional {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "        }"
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery1 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> \n{"
                + "        OPTIONAL { \n"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  
        //optional and graph can also be the other way around.
        String expectedQuery2 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL { \n"
                + "        GRAPH <http://foo.com> \n{"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(query.toString(), expectedQuery1, expectedQuery2, true));
    }

        @Test
    public void testNoneOptionalGraphNone() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?first "
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    Optional {"
                + "        GRAPH <http://foo.com> {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "    }   }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {"
                + "        GRAPH <http://foo.com> {"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNoneOptional2None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first "
                + "WHERE"
                + "  { <http://input.com#1> foaf:name ?name ."
                + "Optional {"
                + "    <http://input.com#1> foaf:mbox ?mbox . "
                + "    <http://input.com#1> foaf:email ?email . "
                + "}"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "         FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        ?replacedURI3 foaf:email ?email . "
                + "         FILTER (?replacedURI3 = <http://replace.org#1> "
                + "             || ?replacedURI3 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI4 foaf:first ?first. \n "
                + "    FILTER (?replacedURI4 = <http://replace.org#1> "
                + "         || ?replacedURI4 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
 
    @Test
    public void testNoneGraph2None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first "
                + "WHERE {"
                + "  <http://input.com#1> foaf:name ?name ."
                + "  GRAPH  <http://foo.com> {"
                + "    <http://input.com#1> foaf:mbox ?mbox . "
                + "    <http://input.com#1> foaf:email ?email . "
                + "}"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> {"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "        ?replacedURI2  foaf:email ?email . "
                + "        FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNoneGraphOptional2None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        Optional {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "            <http://input.com#1> foaf:email ?email . "
                + "        }"
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery1 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> \n{"
                + "        OPTIONAL { \n"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            ?replacedURI2 foaf:email ?email . "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  
        //optional and graph can also be the other way around.
        String expectedQuery2 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL { \n"
                + "        GRAPH <http://foo.com> \n{"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            ?replacedURI2 foaf:email ?email . "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(query.toString(), expectedQuery1, expectedQuery2, true));
    }

    @Test
    public void testNoneOptionalGraph2None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    Optional {"
                + "        GRAPH <http://foo.com> {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "            <http://input.com#1> foaf:email ?email . "
                + "    }   }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {"
                + "        GRAPH <http://foo.com> {"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            ?replacedURI2  foaf:email ?email . "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNoneOptionalGraphGraphNone() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name . \n"
                + "    Optional { \n"
                + "        GRAPH <http://foo.com> {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "        }"
                + "        GRAPH <http://bar.com> {"
                + "            <http://input.com#1> foaf:email ?email . "
                + "    }   }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {\n"
                + "        GRAPH <http://foo.com> {"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }\n"
                + "        GRAPH <http://bar.com> { \n"
                + "            ?replacedURI3  foaf:email ?email . \n"
                + "            FILTER (?replacedURI3 = <http://replace.org#1> "
                + "                 || ?replacedURI3 = <http://replace.org#2>) \n"
                + "        } \n"
                + "    }\n"
                + "    ?replacedURI4 foaf:first ?first. \n "
                + "    FILTER (?replacedURI4 = <http://replace.org#1> "
                + "         || ?replacedURI4 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNone_OptionalGraphNone_None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    Optional {"
                + "        GRAPH <http://foo.com> {"
                + "            <http://input.com#1> foaf:mbox ?mbox . "
                + "        }"
                + "        <http://input.com#1> foaf:email ?email . "
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    OPTIONAL {"
                + "        GRAPH <http://foo.com> {"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "            FILTER (?replacedURI2 = <http://replace.org#1> "
                + "                 || ?replacedURI2 = <http://replace.org#2>) \n"
                + "        }"
                + "        ?replacedURI3  foaf:email ?email . "
                + "        FILTER (?replacedURI3 = <http://replace.org#1> "
                + "             || ?replacedURI3 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI4 foaf:first ?first. \n "
                + "    FILTER (?replacedURI4 = <http://replace.org#1> "
                + "         || ?replacedURI4 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    @Test
    public void testNone_GraphNoneOptional_None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        <http://input.com#1> foaf:mbox ?mbox . "
                + "        Optional {"
                + "            <http://input.com#1> foaf:email ?email . "
                + "        }"
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> {"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "        OPTIONAL {"
                + "            ?replacedURI2  foaf:email ?email . "
                + "        }"
                + "        FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    @Test
    public void testNone_GraphNoneOptionalNone_None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first ?second \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        <http://input.com#1> foaf:mbox ?mbox . "
                + "        Optional {"
                + "            <http://input.com#1> foaf:email ?email . "
                + "        }"
                + "        <http://input.com#1> foaf:second ?second . "
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first ?second \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> { \n"
                + "        ?replacedURI2 foaf:mbox ?mbox. \n "
                + "        OPTIONAL { \n"
                + "            ?replacedURI2  foaf:email ?email . \n"
                + "        } \n"
                + "        ?replacedURI2 foaf:second ?second . \n"
                + "        FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    } \n"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    @Test
    public void testNone_GraphOptionalOptional_None() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        Optional {"
                + "           <http://input.com#1> foaf:mbox ?mbox . "
                + "        }"
                + "        Optional {"
                + "            <http://input.com#1> foaf:email ?email . "
                + "        }"
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> {"
                + "        OPTIONAL {"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "        }"
                + "        OPTIONAL {"
                + "            ?replacedURI2  foaf:email ?email . "
                + "        }"
                + "        FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
    @Test
    public void testNone_GraphOptionalNone_None() throws MalformedQueryException, QueryExpansionException {
        System.out.println("testNone_GraphOptionalNone_None");
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE {"
                + "    <http://input.com#1> foaf:name ?name ."
                + "    GRAPH <http://foo.com> {"
                + "        Optional {"
                + "           <http://input.com#1> foaf:mbox ?mbox . #test \n"
                + "        }"
                + "        <http://input.com#1> foaf:email ?email . "
                + "    }"
                + "    <http://input.com#1> foaf:first ?first ."
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> \n"
                + "SELECT ?name ?mbox ?email ?first \n"
                + "WHERE { \n"
                + "    ?replacedURI1 foaf:name ?name . \n"
                + "    FILTER (?replacedURI1 = <http://replace.org#1> "
                + "         || ?replacedURI1 = <http://replace.org#2>) \n "
                + "    GRAPH <http://foo.com> \n{"
                + "        OPTIONAL { \n"
                + "            ?replacedURI2 foaf:mbox ?mbox. \n "
                + "        }\n"
                + "        ?replacedURI2  foaf:email ?email . \n"
                + "        FILTER (?replacedURI2 = <http://replace.org#1> "
                + "             || ?replacedURI2 = <http://replace.org#2>) \n"
                + "    }"
                + "    ?replacedURI3 foaf:first ?first. \n "
                + "    FILTER (?replacedURI3 = <http://replace.org#1> "
                + "         || ?replacedURI3 = <http://replace.org#2>) \n"
                + "}";  

        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
    
}
