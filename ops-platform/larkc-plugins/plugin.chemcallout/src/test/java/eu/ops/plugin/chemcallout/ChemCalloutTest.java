package eu.ops.plugin.chemcallout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import ch.qos.logback.classic.Logger;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.ops.services.chemspider.SearchClient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for your LarKC plug-in.
 */
public class ChemCalloutTest 
    extends TestCase
{
	private final Log log = LogFactory.getLog(ChemCalloutTest.class);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ChemCalloutTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ChemCalloutTest.class );
    }

    /**
     * Rigorous Test
     */
    public void testMyPlugin()
    {
        assertTrue( true );
    }
    
    public void testInitialisation() {
    	URI uri = new URIImpl("http://eu.ops.plugin.chemcallout.ChemCallout");
    	ChemCallout cc = new ChemCallout(uri);
    	assertNotNull(cc);
    	cc.initialise(null);
    	SetOfStatements statements = new SPARQLQueryImpl(TEST_QUERY).toRDF();
		SetOfStatements results = cc.invokeInternal(statements);
		final RdfStoreConnection myStore=DataFactory.INSTANCE.createRdfStoreConnection();
		log.info(myStore.toString());
		//assertEquals(statements, results);
    }
    
	public static String TEST_QUERY = "PREFIX func: <http://wiki.openphacts.org/index.php/ext_function#>\n"
		+ "SELECT ?csid where {\n"
		+ "?csid func:has_similar \"CNC(=O)C1=NC=CC(=C1)OC2=CC=C(C=C2)NC(=O)NC3=CC(=C(C=C3)Cl)C(F)(F)F\" } "+
		"LIMIT 10";

}
