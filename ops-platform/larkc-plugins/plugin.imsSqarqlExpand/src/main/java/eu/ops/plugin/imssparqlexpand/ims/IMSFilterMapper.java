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
public abstract class IMSFilterMapper implements IMSMapper{

    private IMSMapper fullMapper;
    
    IMSFilterMapper(IMSMapper fullMapper){
        this.fullMapper = fullMapper;
    }
    
    @Override
    public Map<URI, List<URI>> getMatchesForURIs(Set<URI> uriSet) {
        return fullMapper.getMatchesForURIs(uriSet);
    }

    @Override
    public List<URI> getMatchesForURI(URI uri) {
        return fullMapper.getMatchesForURI(uri);
    }

    @Override
    public List<URI> getSpecificMatchesForURI(URI uri, String graph) throws QueryExpansionException{
        List<URI> fullList = fullMapper.getMatchesForURI(uri);
        return stripoutURIs(fullList, graph);
    }

    abstract List<URI> stripoutURIs(List<URI> fullList, String graph) throws QueryExpansionException;
    
}
