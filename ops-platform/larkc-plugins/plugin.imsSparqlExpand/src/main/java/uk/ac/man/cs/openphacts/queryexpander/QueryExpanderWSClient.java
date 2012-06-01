/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.man.cs.openphacts.queryexpander;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Implements QueryExpander where these methods are documented.
 * @author Christian
 * @see QueryExpander
 */
public class QueryExpanderWSClient implements QueryExpander{

    protected final String serviceAddress;

    protected final WebResource webResource;

    public QueryExpanderWSClient(String serviceAddress) {
        this.serviceAddress = serviceAddress;
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(serviceAddress);        
    }
    
    @Override
    public String expand(String originalQuery, List<String> parameters, String inputURI) throws QueryExpansionException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("query", originalQuery);
        for (String parameter:parameters){
            params.add("parameter", parameter);
        }
        if (inputURI == null) params.add("inputURI", inputURI);
        ExpanderBean bean = 
                webResource.path("expand")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ExpanderBean>() {});
        return bean.getExpandedQuery();
    }

    /**
     * @deprecated 
     */
    @Override
    public String expand(String originalQuery) throws QueryExpansionException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("query", originalQuery);
        ExpanderBean bean = 
                webResource.path("expand")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ExpanderBean>() {});
        return bean.getExpandedQuery();
    }

    @Override
    public Map<String, Set<String>> getURISpacesPerGraph() throws QueryExpansionException {
        List<URISpacesInGraphBean> beans = 
                webResource.path("URISpacesPerGraph")
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<List<URISpacesInGraphBean>>() {});
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (URISpacesInGraphBean bean:beans){
            results.put(bean.getGraph(), bean.getURISpace());
        }
        return results;
    }
    
}
