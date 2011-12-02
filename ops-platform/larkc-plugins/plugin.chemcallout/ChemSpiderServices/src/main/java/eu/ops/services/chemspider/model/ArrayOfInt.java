package eu.ops.services.chemspider.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Java class for ArrayOfString complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;ArrayOfInt xmlns="http://www.chemspider.com/">
 *   &lt;int>int&lt;/int>
 *   &lt;int>int&lt;/int>
 * &lt;/ArrayOfInt>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "ArrayOfInt", namespace="http://www.chemspider.com/")
public class ArrayOfInt {

    @XmlElement(name="int", namespace="http://www.chemspider.com/")
    protected List<Integer> integers;
    
    public List<Integer> getIntegers() {
    	return integers;
    }
}


