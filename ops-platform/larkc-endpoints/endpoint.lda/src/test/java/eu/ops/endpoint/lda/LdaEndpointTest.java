package eu.ops.endpoint.lda;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for your LarKC plug-in.
 */
public class LdaEndpointTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LdaEndpointTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LdaEndpointTest.class );
    }

    /**
     * Rigorous Test
     */
    public void testLdaEndpoint()
    {
        assertTrue( true );
    }
}