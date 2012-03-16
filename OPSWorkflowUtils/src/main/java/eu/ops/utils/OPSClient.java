package eu.ops.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class OPSClient {
	private URI serviceUrl;
	private HttpClient httpclient = new DefaultHttpClient();


	public void setServiceUrl(URI serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public URI getServiceUrl() {
		return serviceUrl;
	}

	public String loadWorkflowDefinition(String workflow) throws ParseException, IOException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("workflow", workflow));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httppost = new HttpPost(serviceUrl);
		httppost.setEntity(entity);
		
		HttpResponse response = httpclient.execute(httppost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode >= 200 && statusCode < 300) {
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				return EntityUtils.toString(responseEntity);
			}
		} else {
			System.err.println(response.getStatusLine().getReasonPhrase());
		}
		return null;
	}

	public String getSparqlEndpoint(String uid) throws ClientProtocolException, IOException {
		StringBuilder req = new StringBuilder();
		req.append(serviceUrl);
		req.append("/").append(uid).append("/endpoint?urn=urn:eu.larkc.endpoint.sparql.ep1");
		HttpGet httpget = new HttpGet(req.toString());
		
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			return EntityUtils.toString(responseEntity);
		}
		return null;
	}
	
	public String runQuery(String endpoint, String sparql) throws ParseException, IOException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("query", sparql));
		formparams.add(new BasicNameValuePair("method", "sparql"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httppost = new HttpPost(endpoint);
		httppost.setEntity(entity);
		
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			return EntityUtils.toString(responseEntity);
		}
		return null;
	}
}
