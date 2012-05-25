/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.man.cs.openphacts.queryexpander;

import uk.ac.man.cs.openphacts.queryexpander.QueryExpansionException;
import uk.ac.man.cs.openphacts.queryexpander.ExpanderBean;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpander;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import uk.ac.man.cs.openphacts.queryexpander.ExpanderBean;
import uk.ac.man.cs.openphacts.queryexpander.ExpanderBean;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpander;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpander;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpansionException;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpansionException;

/**
 *
 * @author Christian
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
    public String expand(String originalQuery) throws QueryExpansionException {
        return expand(originalQuery, false);
    }

    @Override
    public String expand(String originalQuery, boolean verbose) throws QueryExpansionException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("query", originalQuery);
        ExpanderBean bean = 
                webResource.path("expand")
                .queryParams(params)
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get(new GenericType<ExpanderBean>() {});
        return bean.getExpandedQuery();
    }
    
}
