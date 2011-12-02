package eu.larkc.endpoint.opsapi;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for your LarKC plug-in.
 */
public class OPSAPIEndpointTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OPSAPIEndpointTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( OPSAPIEndpointTest.class );
    }

    /**
     * Rigorous Test
     */
    public void testOPSAPIEndpoint()
    {
        assertTrue( true );
    }
}
