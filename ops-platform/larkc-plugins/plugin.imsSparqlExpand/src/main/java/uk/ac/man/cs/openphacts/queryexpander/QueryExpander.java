package uk.ac.man.cs.openphacts.queryexpander;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface covers the possible ways that any outside application can call the QueryExpander.
 * 
 * The same interface is used by both the WebService Client and local implemention called by the WebServer.
 * This this way any applications built against the WebService can easily to swapped to a local service 
 *    with only a few lines of code.
 * @author Christian
 */
public interface QueryExpander {
    
    /**
     * Expands the query using the loaed mappings.
     * 
     * <p>
     * For each graph where any of the parameters is found a Filter statement is added to check if this var is equals 
     *    to one of the mapped URI. 
     * The Mapped URIs will be the ones that the mapping service returns as valid for this graph.
     * <p>
     * Currently it is allowed for both parameters to be empty (not null) AND inputURI to be null. 
     *   In this case the depricated expand(String originalQuery) method will be called. 
     * 
     * @param originalQuery Query in String Format
     * @param parameters List<String> of the parameters to add filters to. This is including the leading questionMark. 
     * @param inputURI URI in String format The Uri to expand and then use in the filters. 
     *     This must represent a Valid URI according to the OpenRDF's standards.
     *     Expected to include the http:// or equivelent start.
     *     Do not include the angle brackets.
     * @return Expanded query
     * @throws QueryExpansionException 
     * @param originalQuery Query in String Format
     * @return Expanded query
     * @throws QueryExpansionException 
     */
    public String expand(String originalQuery, List<String> parameters, String inputURI) throws QueryExpansionException;

    /**
     * This is a previous version of the query expander interface.
     * 
     * It will call the previous Query Expander service that looks up every URI and if requires replaces it.
     * There is no guarantee that this will continue to be supported, especially if the underlying parser changes.
     * <p>
     * It you wish to continue using this functionality medium term please contact the developers 
     *   so that it the depreciated status can be removed.
     * @param originalQuery
     * @return Expanded query
     * @deprecated 
     * @throws QueryExpansionException 
     */
    public String expand(String originalQuery) 
            throws QueryExpansionException;

    /**
     * Support function that asks the underlying mapping service which URISpace(s) it will include in each graph.
     * 
     * The map will be empty if the underlying mapping service does not support Mapping by graph.
     * 
     * If the query includes any gtraph not in this map (URISpace to graph)  all mapped URI will be used every time.
     * @return Map of known URISpaces in each graph.
     * @throws QueryExpansionException 
     */
    public Map<String, Set<String>> getURISpacesPerGraph() throws QueryExpansionException;
}
