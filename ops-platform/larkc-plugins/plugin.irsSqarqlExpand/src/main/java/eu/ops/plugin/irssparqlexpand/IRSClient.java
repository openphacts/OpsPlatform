package eu.ops.plugin.irssparqlexpand;

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
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import uk.ac.manchester.cs.ims.beans.Match;

/**
 * Client for interacting with the IRS service
 */
public class IRSClient implements IRSMapper{

    String serviceAddress = 
//            "http://localhost:8080/OPS-IRS-Prototype/";
            "http://ondex2.cs.man.ac.uk:9090/OPS-IRS-Prototype/";

    private final WebResource webResource;
    
    public IRSClient() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
    }

    public List<Match> getMatchesForURI(String uri) {
        //Configure parameters
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("uri", uri);
        //Make service call
        List<Match> matches = 
                webResource.path("getMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Match>>() {});
        return matches;
    }

    List<URI> getMatchesForURI(URI uri) {
        //Configure parameters
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("uri", uri.stringValue());
        //Make service call
        List<Match> matches = 
                webResource.path("getMappings")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<Match>>() {});
System.out.println("***********Number of matches for " + uri + ": " + matches.size());        
        return extractMatches(matches);
    }

    private List<URI> extractMatches(List<Match> matches) {
        List<URI> uriList = new ArrayList<URI>();
        for (Match match : matches) {
            URI uri = new URIImpl(match.getMatchUri());
            uriList.add(uri);
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
        IRSClient irsClient = new IRSClient();
        List<Match> matches = irsClient.getMatchesForURI("http://brenda-enzymes.info/1.1.1.1");
        StringBuilder response = new StringBuilder("Response:\n");
        for (Match match : matches) {
            response.append("\t").append(match.getId())
                    .append("\t").append(match.getMatchUri()).append("\n");
        }
        System.out.println(response.toString());
    }
    
}
