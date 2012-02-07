package eu.ops.plugin.imssparqlexpand.ims;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public interface IMSMapper {
    Map<URI, List<URI>> getMatchesForURIs(Set<URI> uriSet);

    List<URI> getMatchesForURI(URI uri);

    List<URI> getSpecificMatchesForURI(URI uri, String graph) throws QueryExpansionException;
}
