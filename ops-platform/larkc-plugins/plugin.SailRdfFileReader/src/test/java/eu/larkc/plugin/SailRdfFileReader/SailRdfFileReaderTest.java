package eu.larkc.plugin.SailRdfFileReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for your LarKC plug-in.
 */
public class SailRdfFileReaderTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SailRdfFileReaderTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SailRdfFileReaderTest.class );
    }

    /**
     * Rigorous Test
     */
    public void testMyPlugin()
    {
        assertTrue( true );
    }
}
