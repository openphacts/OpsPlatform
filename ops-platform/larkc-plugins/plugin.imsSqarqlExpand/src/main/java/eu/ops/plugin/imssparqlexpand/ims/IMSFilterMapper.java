package eu.ops.plugin.imssparqlexpand.ims;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;

/**
 * Abstract class that allows a Filter to be placed on top of another IMSMapper to take graph ito consideration.
 * <p>
 *  getMatchesForURIs(Set<URI>) and getMatchesForURI(URI) are implmented as pass through methods.
 * <p>
 * getSpecificMatchesForURI(URI, String) is implemented by calling getMatchesForURI(URI) 
 *   and then calling the abstract method stripoutURIs(List<URI>, String)
 * @author Christian
 */
public abstract class IMSFilterMapper implements IMSMapper{

    private IMSMapper fullMapper;
    
    /**
     * Constructs a IMSFilterMapper which wraps another IMSMapper.
     * 
     * @param fullMapper Service to get the Mappings ignoring graph from. 
     */
    IMSFilterMapper(IMSMapper fullMapper){
        this.fullMapper = fullMapper;
    }
    
    @Override
    public Map<URI, Set<URI>> getMatchesForURIs(Set<URI> uriSet) {
        return fullMapper.getMatchesForURIs(uriSet);
    }

    @Override
    public Set<URI> getMatchesForURI(URI uri) {
        return fullMapper.getMatchesForURI(uri);
    }

    @Override
    public Set<URI> getSpecificMatchesForURI(URI uri, String graph) throws QueryExpansionException{
        Set<URI> fullList = fullMapper.getMatchesForURI(uri);
        return stripoutURIs(fullList, graph);
    }

    /**
     * Method to remove URIs that are known not to be in the graph.
     * <p>
     * Should only remove URI if it known the graph, otheriwse return the whole list.
     * <p>
     * There is no guarantee that every URI in the List will be in the GRAPH, 
     * Implementation may just do some pattern matching on the URIs. 
     * 
     * @param fullList
     * @param graph
     * @return
     * @throws QueryExpansionException 
     */
    abstract Set<URI> stripoutURIs(Set<URI> fullList, String graph) throws QueryExpansionException;
    
}
