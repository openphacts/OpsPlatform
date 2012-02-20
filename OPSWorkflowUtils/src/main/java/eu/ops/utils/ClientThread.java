/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Christian
 */
public class ClientThread implements Runnable {

    private static String query = "select ?s ?p ?o "
                + "{ ?s ?p ?o }"
                + "Limit 10";
    //private static String endpoint = "http://localhost:8183/opsapi";
    private String endpoint = "http://ops.few.vu.nl:8183/opsapi"; //production

    private UrlEncodedFormEntity entity;
    
    private int threadNumber;
    
    public ClientThread(int number) throws UnsupportedEncodingException{
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("query", query));
		formparams.add(new BasicNameValuePair("method", "sparql"));        
		entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        threadNumber = number;
    }
    
    @Override
    public void run() {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("query", query));
		formparams.add(new BasicNameValuePair("method", "sparql"));
		HttpPost httppost = new HttpPost(endpoint);
		httppost.setEntity(entity);
        Date start = new Date();
        for (int i = 0; i < 10 ; i++){
    		HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    System.out.println(EntityUtils.toString(responseEntity));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            System.out.println(threadNumber + ": " + i);
        }
        Date end = new Date();
        System.out.println ("done " + threadNumber);
        System.out.println(end.getTime() - start.getTime());
    }
    
    
}
