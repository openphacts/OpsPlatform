package eu.ops.plugin.conceptwiki;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.query.SPARQLQueryImpl;

/**
 * Unit test for your LarKC plug-in.
 */
public class ConceptWikiTest 
{
	private final Logger log = LoggerFactory.getLogger(ConceptWiki.class);
	private ConceptWiki cc;
	public static String TESTQ_SEARCH = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
		+ "SELECT ?x where {\n"
		+ "?x func:conceptwiki_search \"water\" } "+
		"LIMIT 10";
	public static String TESTQ_GET_CONCEPT = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
			+ "SELECT ?x where {\n"
			+ "?x func:conceptwiki_get_concept \"d19a73ff-579c-46c0-af47-52290ae06186\" } "+
			"LIMIT 10";
	public static String TESTQ_SEARCH_BY_TAG = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
			+ "PREFIX cw: <http://www.concepwiki.nl/test#>\n"
			+ "SELECT ?x where {\n"
			+ "?x func:conceptwiki_search_by_tag \"plasmo\";\n "
			+ "func:conceptwiki_semantic_type \"b946958d-b46f-4de3-aa55-63684b301cf1\" } " +
			"LIMIT 10";
	public static String TESTQ_SEARCH_BY_TAG_REVERSE = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
			+ "PREFIX cw: <http://www.concepwiki.nl/test#>\n"
			+ "SELECT ?x where {\n"
			+ "?x func:conceptwiki_semantic_type \"b946958d-b46f-4de3-aa55-63684b301cf1\";\n "
			+ "func:conceptwiki_search_by_tag \"plasmo\" } " +
			"LIMIT 10";
	public static String TESTQ_SEARCH_URL = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
			+ "SELECT ?x where {\n"
			+ "?x func:conceptwiki_search_url \"malaria\" } "+
			"LIMIT 10";
	
	@Before
	public void setUp() {
    	URI uri = new URIImpl("http://eu.ops.plugin.conceptwiki.ConceptWiki");
    	cc = new ConceptWiki(uri);
    	Assert.assertNotNull(cc);
    	cc.initialiseInternal(new SetOfStatementsImpl());
	}
	
	@After
	public void tearDown() {
		
	}
    
    @Test
    public void test() {
    	String[] tests = {TESTQ_SEARCH, TESTQ_GET_CONCEPT, TESTQ_SEARCH_BY_TAG, TESTQ_SEARCH_BY_TAG_REVERSE, TESTQ_SEARCH_URL};
    	for(String TEST: tests) {
        	SetOfStatements statements = new SPARQLQueryImpl(TEST).toRDF();
        	// print sparql query here
        	System.out.println("TEST SPARQL");
        	System.out.println(statements.toString());
    		SetOfStatements results = cc.invokeInternal(statements);
    		Assert.assertTrue(results!=null);
    		int count=0;
    		CloseableIterator<Statement> it=results.getStatements();
    		log.info("Start print result statements");
    		while (it.hasNext()) {
    			log.info(it.next().toString());
    			count++;
    		}
    		Assert.assertTrue(count>0);
    	}
    }
}
