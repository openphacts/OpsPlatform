package eu.ops.plugin.chemcallout;

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
public class ChemCalloutTest 
{
	private final Logger log = LoggerFactory.getLogger(ChemCallout.class);
	private ChemCallout cc;
	public static String TEST_QUERY = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
		+ "SELECT ?csid where {\n"
		+ "?csid func:has_similar \"CNC(=O)C1=NC=CC(=C1)OC2=CC=C(C=C2)NC(=O)NC3=CC(=C(C=C3)Cl)C(F)(F)F\" } "+
		"LIMIT 10";
	
	@Before
	public void setUp() {
    	URI uri = new URIImpl("http://eu.ops.plugin.chemcallout.ChemCallout");
    	cc = new ChemCallout(uri);
    	Assert.assertNotNull(cc);
    	cc.initialiseInternal(new SetOfStatementsImpl());
	}
	
	@After
	public void tearDown() {
		
	}
    
    @Test
    public void test() {
    	SetOfStatements statements = new SPARQLQueryImpl(TEST_QUERY).toRDF();
		SetOfStatements results = cc.invokeInternal(statements);
		Assert.assertTrue(results!=null);
		int count=0;
		CloseableIterator<Statement> it=results.getStatements();
		while (it.hasNext()) {
			log.info(it.next().toString());
			count++;
		}
		Assert.assertTrue(count>0);
    }
    
	

}
