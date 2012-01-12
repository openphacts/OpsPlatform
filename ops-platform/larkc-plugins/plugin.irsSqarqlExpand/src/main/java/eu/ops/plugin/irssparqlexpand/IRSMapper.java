package eu.ops.plugin.irssparqlexpand;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public interface IRSMapper {
    Map<URI, List<URI>> getMatchesForURIs(Set<URI> uriSet);
}
