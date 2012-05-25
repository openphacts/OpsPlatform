package uk.ac.man.cs.openphacts.queryexpander;

/**
 *
 * @author Christian
 */
public interface QueryExpander {
    
    /**
     * Expands the query using the loaed mappings.
     * 
     * Default implementation is to call the  expand(String originalQuery, boolean verbose) with verbise set to false.
     * 
     * @param originalQuery Query in String Format
     * @return Expanded query
     * @throws QueryExpansionException 
     */
    public String expand(String originalQuery) throws QueryExpansionException;

    /**
     * Expands the query using the loaed mappings.
     * 
     * @param originalQuery Query in String Format
     * @param verbose If set to true the expander will output debug information
     * @return Expanded query
     * @throws QueryExpansionException 
     */
    public String expand(String originalQuery, boolean verbose) throws QueryExpansionException;

}
