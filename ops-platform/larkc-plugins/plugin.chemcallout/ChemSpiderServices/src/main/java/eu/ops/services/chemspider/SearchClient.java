package eu.ops.services.chemspider;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.ops.services.chemspider.model.ArrayOfInt;
import eu.ops.services.chemspider.model.ERequestStatus;

public class SearchClient {
	private final Log log = LogFactory.getLog(SearchClient.class);
	private URI serviceUrl;
	private HttpClient httpclient = new DefaultHttpClient();
	private String token;
	private JAXBContext jaxbContext;
	private ClientConfig clientConfig = new DefaultClientConfig();
	private static String MOCK_RID = "abcd1234";
	private int mockWaitCount = 0;
	
	public SearchClient() {
//		clientConfig.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 1000);
//		clientConfig.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 3000);
	}
	
	private static String SOAP_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
		"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+ 
		" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+
		" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
		"<soap:Body>";
	private static String SOAP_SUFFIX = "</soap:Body></soap:Envelope>";
	
	public String similaritySearch(String molecule, String similarityType, double threshold) {
		return similaritySearch(molecule, similarityType, threshold, null, null, null, null);
	}
	
	public String similaritySearch(String molecule, String similarityType, double threshold,
			String complexity, String isotopic, String hasSpectra,
			String hasPatents) {	
		log.info("ORIGINAL STRING = "+molecule);
		try{
			//molecule=URLEncoder.encode(molecule, "UTF-8");
			log.info("ENCODED STRING = "+molecule);
		}
		catch (Exception e){
			e.printStackTrace();
			log.error("This should never happen.");
		}
		StringBuilder soapRequest = new StringBuilder();
		soapRequest.append(SOAP_PREFIX);
		soapRequest.append("<SimilaritySearch xmlns=\"http://www.chemspider.com/\">");
		soapRequest.append("<options>");
		addElement(soapRequest, "Molecule", molecule);
		addElement(soapRequest, "SearchType", "Similarity");
		addElement(soapRequest, "SimilarityType", similarityType);
		addElement(soapRequest, "Threshold", String.valueOf(threshold));
		soapRequest.append("</options>");
		if (complexity != null || isotopic != null || hasSpectra!=null || hasPatents!=null) {
			soapRequest.append("<commonOptions>");
			if (complexity!=null)
				addElement(soapRequest, "Complexity", complexity);
			if (isotopic!=null)
				addElement(soapRequest, "Isotopic", isotopic);
			if (hasSpectra!=null)
				addElement(soapRequest, "HasSpectra", hasSpectra);
			if (hasPatents!=null)
				addElement(soapRequest, "HasPatents", hasPatents);
			soapRequest.append("</commonOptions>");
		}
		addElement(soapRequest, "token", token);
		soapRequest.append("</SimilaritySearch>");
		soapRequest.append(SOAP_SUFFIX);

		StringEntity entity;
		try {
			entity = new StringEntity(soapRequest.toString(), "UTF-8");
			entity.setContentType("text/xml");
		} catch (UnsupportedEncodingException e) {
			// should not be possible to fail
			e.printStackTrace();
			return null;
		}

		HttpPost httppost = new HttpPost(serviceUrl);
		httppost.setEntity(entity);
		httppost.addHeader("SOAPAction", "\"http://www.chemspider.com/SimilaritySearch\"");

		try {
			HttpResponse response = httpclient.execute(httppost);
			log.debug("Protocol="+response.getProtocolVersion());
			log.debug("StatusCode="+response.getStatusLine().getStatusCode());
			log.debug("ReasonPhrase="+response.getStatusLine().getReasonPhrase());
			log.debug("StatusLine="+response.getStatusLine().toString());

			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String res = EntityUtils.toString(responseEntity);
				int start = res.indexOf("<SimilaritySearchResult>");
				int end = res.indexOf("<", start+24);
				String rid = res.substring(start+24, end);
				if (!rid.contains("="))
					return rid;
				log.error("Invalid repsonse from ChemSpider: "+ res);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public String substructureSearch(String molecule, boolean tautomers) {
		return substructureSearch(molecule, tautomers, null, null, null, null);
	}
	
	public String substructureSearch(String molecule, boolean tautomers,
			String complexity, String isotopic, String hasSpectra,
			String hasPatents) {	
		log.info("ORIGINAL STRING = "+molecule);
		try{
			//molecule=URLEncoder.encode(molecule, "UTF-8");
			log.info("ENCODED STRING = "+molecule);
		}
		catch (Exception e){
			e.printStackTrace();
			log.error("This should never happen.");
		}
		StringBuilder soapRequest = new StringBuilder();
		soapRequest.append(SOAP_PREFIX);
		soapRequest.append("<SubstructureSearch xmlns=\"http://www.chemspider.com/\">");
		soapRequest.append("<options>");
		addElement(soapRequest, "Molecule", molecule);
		addElement(soapRequest, "SearchType", "Substructure");
		addElement(soapRequest, "MatchTautomers", tautomers?"true":"false");
		soapRequest.append("</options>");
		if (complexity != null || isotopic != null || hasSpectra!=null || hasPatents!=null) {
			soapRequest.append("<commonOptions>");
			if (complexity!=null)
				addElement(soapRequest, "Complexity", complexity);
			if (isotopic!=null)
				addElement(soapRequest, "Isotopic", isotopic);
			if (hasSpectra!=null)
				addElement(soapRequest, "HasSpectra", hasSpectra);
			if (hasPatents!=null)
				addElement(soapRequest, "HasPatents", hasPatents);
			soapRequest.append("</commonOptions>");
		}
		addElement(soapRequest, "token", token);
		soapRequest.append("</SubstructureSearch>");
		soapRequest.append(SOAP_SUFFIX);

		StringEntity entity;
		try {
			entity = new StringEntity(soapRequest.toString(), "UTF-8");
			entity.setContentType("text/xml");
		} catch (UnsupportedEncodingException e) {
			// should not be possible to fail
			e.printStackTrace();
			return null;
		}

		HttpPost httppost = new HttpPost(serviceUrl);
		httppost.setEntity(entity);
		httppost.addHeader("SOAPAction", "\"http://www.chemspider.com/SubstructureSearch\"");

		try {
			HttpResponse response = httpclient.execute(httppost);
			log.debug("Protocol="+response.getProtocolVersion());
			log.debug("StatusCode="+response.getStatusLine().getStatusCode());
			log.debug("ReasonPhrase="+response.getStatusLine().getReasonPhrase());
			log.debug("StatusLine="+response.getStatusLine().toString());

			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String res = EntityUtils.toString(responseEntity);
				int start = res.indexOf("<SubstructureSearchResult>");
				int end = res.indexOf("<", start+26);
				String rid = res.substring(start+26, end);
				if (!rid.contains("="))
					return rid;
				log.error("Invalid repsonse from ChemSpider: "+ res);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public String structureSearch(String molecule, String type) {
		return structureSearch(molecule, type, null, null, null, null);
	}
	
	public String structureSearch(String molecule, String type,
			String complexity, String isotopic, String hasSpectra,
			String hasPatents) {	
		log.info("ORIGINAL STRING = "+molecule);
		try{
			//molecule=URLEncoder.encode(molecule, "UTF-8");
			log.info("ENCODED STRING = "+molecule);
		}
		catch (Exception e){
			e.printStackTrace();
			log.error("This should never happen.");
		}
		StringBuilder soapRequest = new StringBuilder();
		soapRequest.append(SOAP_PREFIX);
		soapRequest.append("<StructureSearch xmlns=\"http://www.chemspider.com/\">");
		soapRequest.append("<options>");
		addElement(soapRequest, "Molecule", molecule);
		addElement(soapRequest, "SearchType", "Structure");
		addElement(soapRequest, "MatchType", type);
		soapRequest.append("</options>");
		if (complexity != null || isotopic != null || hasSpectra!=null || hasPatents!=null) {
			soapRequest.append("<commonOptions>");
			if (complexity!=null)
				addElement(soapRequest, "Complexity", complexity);
			if (isotopic!=null)
				addElement(soapRequest, "Isotopic", isotopic);
			if (hasSpectra!=null)
				addElement(soapRequest, "HasSpectra", hasSpectra);
			if (hasPatents!=null)
				addElement(soapRequest, "HasPatents", hasPatents);
			soapRequest.append("</commonOptions>");
		}
		addElement(soapRequest, "token", token);
		soapRequest.append("</StructureSearch>");
		soapRequest.append(SOAP_SUFFIX);

		StringEntity entity;
		try {
			entity = new StringEntity(soapRequest.toString(), "UTF-8");
			entity.setContentType("text/xml");
		} catch (UnsupportedEncodingException e) {
			// should not be possible to fail
			e.printStackTrace();
			return null;
		}

		HttpPost httppost = new HttpPost(serviceUrl);
		httppost.setEntity(entity);
		httppost.addHeader("SOAPAction", "\"http://www.chemspider.com/StructureSearch\"");
		try {
			HttpResponse response = httpclient.execute(httppost);
			log.debug("Protocol="+response.getProtocolVersion());
			log.debug("StatusCode="+response.getStatusLine().getStatusCode());
			log.debug("ReasonPhrase="+response.getStatusLine().getReasonPhrase());
			log.debug("StatusLine="+response.getStatusLine().toString());

			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String res = EntityUtils.toString(responseEntity);
				int start = res.indexOf("<StructureSearchResult>");
				int end = res.indexOf("<", start+23);
				String rid = res.substring(start+23, end);
				if (!rid.contains("="))
					return rid;
				log.error("Invalid repsonse from ChemSpider: "+ res);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	public ERequestStatus getAsyncSearchStatus(String rid) {
		if (rid.equals(MOCK_RID)) {
			if (mockWaitCount++ == 5) {
				return ERequestStatus.RESULT_READY;
			} else {
				return ERequestStatus.PROCESSING;
			}
		}
		StringBuilder url = new StringBuilder(serviceUrl.toString());
		url.append("/GetAsyncSearchStatus?rid=").append(rid);
		url.append("&token=").append(token);
		
		try{
			String data = callService(url.toString());
			if (data != null) {
				try {
					ERequestStatus status = (ERequestStatus) createUnmarshaller().unmarshal(new StreamSource(new StringReader(data)));
					return status;
				} catch (Exception e) {
					log.error("getAsyncSearchStatus failed", e);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
			log.error("Invalid response form chemspider: " +rid);
		}
		return null;
	}

	public List<Integer> getAsyncSearchResult(String rid) {
		if (rid.equals(MOCK_RID)) {
			List<Integer> res = new ArrayList<Integer> ();
			res.add(1234);
			res.add(5678);
			return res;
		}
		StringBuilder url = new StringBuilder(serviceUrl.toString());
		url.append("/GetAsyncSearchResult?rid=").append(rid);
		url.append("&token=").append(token);
		
		String data = callService(url.toString());
		if (data != null) {
			try {
				ArrayOfInt results = (ArrayOfInt) createUnmarshaller().unmarshal(new StreamSource(new StringReader(data)));
				return results.getIntegers();
			} catch (Exception e) {
				log.error("getAsyncSearchStatus failed", e);
			}
		}
		return null;
	}

	public String similaritySearchMock(String molecule, String similarityType, double threshold) {
		mockWaitCount = 0;
		return MOCK_RID;
	}
	

	private StringBuilder addElement(StringBuilder sb, String name, String value) {
		sb.append("<").append(name).append(">");
		if (value!=null) {
			sb.append(value);
		}
		sb.append("</").append(name).append(">");
		return sb;
	}

	private String callService(String serviceURL){
		Client client = Client.create(clientConfig);
		WebResource res = client.resource(serviceURL);
		String result = null;
		try {
			result = res.get(String.class);
		} catch (ClientHandlerException e) {
			log.error("Service failed: "+serviceURL, e);
		}
		return result;
	}

	public void setServiceUrl(URI serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public URI getServiceUrl() {
		return serviceUrl;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	private Unmarshaller createUnmarshaller() throws JAXBException {
		return jaxbContext.createUnmarshaller();
	}
	
	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}
}
