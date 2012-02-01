package eu.ops.plugin.imssparqlexpand.ims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author Christian
 */
public class DummyIMSMapper implements IMSMapper{

    Map<URI, List<URI>> uriMappings = new HashMap<URI, List<URI>>();
    ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    
    public void addMapping(String fromString, String toString){
       URI fromURI = valueFactory.createURI(fromString);
       URI toURI = valueFactory.createURI(toString);
       List<URI> uriList = uriMappings.get(fromURI);
       if (uriList == null){
           uriList = new ArrayList<URI>();
       }
       if (!uriList.contains(toURI)){
           uriList.add(toURI);
       }
       uriMappings.put(fromURI, uriList);
    }
    
    @Override
    public Map<URI, List<URI>> getMatchesForURIs(Set<URI> uriSet) {
       return uriMappings;
    }

    @Override
    public List<URI> getMatchesForURI(URI uri) {
        return uriMappings.get(uri);
    }

    @Override
    public List<URI> getSpecificMatchesForURI(URI uri, String graph) {
        return uriMappings.get(uri);
    }
    
}
