/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.utils;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Christian
 */
public class tester {
    public static void main(String[] args) throws ParseException, IOException {
        String graphQuery = "select distinct ?g "
                + "{"
                + "  ?g "
                + "    { ?s ?p ?o }"
                + "}"
                + "Limit 100";
        String query = "select ?s ?p ?o "
                + "{ ?s ?p ?o }"
                + "Limit 10";
        
        String endpoint = "http://ops.few.vu.nl:8183/opsapi"; //production
        //String endpoint = "http://localhost:8183/opsapi";
        
        OPSClient  client = new  OPSClient();
        String response = client.runQuery(endpoint, query);
        System.out.println (response);
    }

}
