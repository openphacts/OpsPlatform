package eu.ops.plugin.imssparqlexpand.ims;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.bridgedb.ws.bean.URLMappingBean;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Client for interacting with the IMS service
 * <p>
 * getMatchesForURIs(Set<URI>) is implementated by iterating over getMatchesForURI(URI uri)
 * <p>
 * getSpecificMatchesForURI(URI uri, String graph) ignores the graph and just calls getMatchesForURI(uri)
 */
public class IMSClient implements IMSMapper{

    String serviceAddress = 
        "http://localhost:8080/OPS-IMS/";
//Not yet up            "http://ondex2.cs.man.ac.uk:9090/OPS-IMS/";

    private final WebResource webResource;
    private final GraphToNameSpaces graphToNameSpaces;
    
    public IMSClient(GraphToNameSpaces graphToNameSpaces) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
        this.graphToNameSpaces = graphToNameSpaces;
    }

    @Override
    public List<URI> getMatchesForURI(URI uri) {
        //Configure parameters
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sourceURL", uri.stringValue());
        //Make service call
        List<URLMappingBean> matches = 
                webResource.path("getMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMappingBean>>() {});
System.out.println("***********Number of matches for " + uri + ": " + matches.size());        
        return extractMatches(matches);
    }

    @Override
    public List<URI> getSpecificMatchesForURI(URI uri, String graph) {
        List<String> nameSpaces = graphToNameSpaces.getNameSpacesInGraph(graph);
        //Configure parameters
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sourceURL", uri.stringValue());
        for (String nameSpace:nameSpaces){
            params.add("targetNameSpace", nameSpace);
        }
        //Make service call
        List<URLMappingBean> matches = 
                webResource.path("getMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URLMappingBean>>() {});
System.out.println("***********Number of matches for " + uri + ": " + matches.size());        
        return extractMatches(matches);
    }

    private List<URI> extractMatches(List<URLMappingBean> matches) {
        List<URI> uriList = new ArrayList<URI>();
        for (URLMappingBean match : matches) {
            if (match.getTargetURL() != null){
                URI uri = new URIImpl(match.getTargetURL());
                if (!uriList.contains(uri)){
                    uriList.add(uri);
                }
            } else {
                System.out.println(match.getError());
            }
        }
        return uriList;
    }

    /**
     * Retrieve the matching URIs for each URI in the provided set.
     * 
     * @param uriSet set of URIs
     * @return Map containing the matching URIs for each given URI in the provided set.
     */
    @Override
    public Map<URI, List<URI>> getMatchesForURIs(Set<URI> uriSet) {
        Map<URI, List<URI>> uriMappings = new HashMap<URI, List<URI>>();
        for (URI uri : uriSet) {
            List<URI> matchesForURI = getMatchesForURI(uri);
            uriMappings.put(uri, matchesForURI);
//System.out.println(matchesForURI.size() + " matches exist for " + uri);
        }
        return uriMappings;
    }

    public static void main(String[] args) {
        IMSClient imsClient = new IMSClient(new TestGraphToNameSpaces());
        List<URI> matches = imsClient.getSpecificMatchesForURI(new URIImpl("http://www.example.com/123"),"testGraph");
        for (URI match : matches) {
            System.out.println(match);
        }
    }

    
}
