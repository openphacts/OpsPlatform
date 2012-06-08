package eu.larkc.endpoint.opsapi;

import java.util.List;

public class ParsedRequest {

	private  List<String> parameters;
	private String inputURI, sparql;
	
	public ParsedRequest(String sparql, List<String> parameters, String inputURI) {
		this.setParameters(parameters);
		this.setInputURI(inputURI);
		this.setSparql(sparql);
	}

	public List<String> getParameters() {
		return parameters;
	}

	private void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getInputURI() {
		return inputURI;
	}

	private void setInputURI(String inputURI) {
		this.inputURI = inputURI;
	}

	public String getSparql() {
		return sparql;
	}

	private void setSparql(String sparql) {
		this.sparql = sparql;
	}
	
	
	
}
