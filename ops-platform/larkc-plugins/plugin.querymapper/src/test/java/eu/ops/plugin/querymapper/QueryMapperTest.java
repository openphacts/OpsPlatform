package eu.ops.plugin.querymapper;

import eu.larkc.core.query.SPARQLQueryImpl;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for your LarKC plug-in.
 */
public class QueryMapperTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public QueryMapperTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( QueryMapperTest.class );
    }

    /**
     * Rigorous Test
     */
    public void testMyPlugin()
    {
        assertTrue( true );
    }
    
    //Removed December 8 2011 by Christian
    //This test fails as QueryMapper constructor can not be called with a null.
//	public void testQueryMapper() {
//		QueryMapper queryMapper = new QueryMapper(null);
//	//	queryMapper.initialiseInternal(null);
//
//		queryMapper.invokeInternal(new SPARQLQueryImpl(
//				"SELECT * where {<http://chem2bio2rdf.org/chebi/resource/chebi/CHEBI%3A242117> ?p ?o} LIMIT 10").toRDF());
//		assert(true);
//	}


}
