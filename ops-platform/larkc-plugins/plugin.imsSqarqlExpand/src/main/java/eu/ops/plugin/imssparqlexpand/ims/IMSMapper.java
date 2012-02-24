package eu.ops.plugin.imssparqlexpand.ims;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;

/**
 * Interface for the mthods a Mapper service should include.
 * 
 * @author Christian
 */
public interface IMSMapper {
    
    /**
     * Maps each URI in a Set to an individual list of URI.
     * <p>
     * Does not take the context/graph into conisderation.
     * <p>
     * Intended to be faster by reducing the number of webservice calls.
     * Other implementation may answer this by repeatedly calling getMatchesForURI(URI);
     * <p>
     * There is no guarantee that very URI in the set will be mapped to a List,
     * However if there is a List mapped it should not be null.
     * There is also no guarantee that the Map will only include URIs in the List.
     * <p>
     * Currently not used.
     * 
     * @param uriSet (Possibly empty) Set of URIs to Map.
     * @return (Possibly empty) Map of each URI to a (Possibly empty) List (none null) of URIs.
     */
    Map<URI, Set<URI>> getMatchesForURIs(Set<URI> uriSet);

    /**
     * Maps an URI to a list of URI.
     * <p>
     * Does not take the context/graph into conisderation.
     * <p>
     * @param uri A URI to Map.
     * @return (Possibly empty) List of URIs or even a null.
     */
    Set<URI> getMatchesForURI(URI uri);

    /**
     * Maps an URI to a list of URI.
     * <p>
     * May take the context/graph into conisderation. 
     * In which case URIs that are known not to be in the context/Graph will be removed.
     * For unknown graphs all possible URIs are returned.
     * <p>
     * NOTE: Implementations that do not have the information about which URIs are in which Graph 
     * will ignore the graph and just call getMatchesForURI(URI);
     * <p>
     * @param uri A URI to Map.
     * @return (Possibly empty) List of URIs (or null, that map to the URI and may be in the GRAPH. 
     *    There is no guarantee that every URI in the List will be in the GRAPH, 
     *    only that it Maps and there is not enough available information to say it can not be in the GRAPH.
     */
    Set<URI> getSpecificMatchesForURI(URI uri, String graph) throws QueryExpansionException;
}
