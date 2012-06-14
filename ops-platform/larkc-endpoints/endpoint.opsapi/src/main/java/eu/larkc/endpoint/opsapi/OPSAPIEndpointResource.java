package eu.larkc.endpoint.opsapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.endpoint.opsapi.exceptions.APIException;
import eu.larkc.endpoint.sparql.exceptions.MalformedSparqlQueryException;
import eu.larkc.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.util.RDFConstants;


public class OPSAPIEndpointResource extends ServerResource {

	static final String EXPANDER_PARAMETER = "http://www.openphacts.org/api#variableForExpansion";
	static final String EXPANDER_INPUT = "http://www.openphacts.org/api#inputForExpansion";
	private static Logger logger = LoggerFactory.getLogger(OPSAPIEndpointResource.class);

	/**
	 * This method executes a query.
	 * 
	 * @param entity
	 *            the entity
	 * @return web page
	 * @throws Exception
	 *             if the query is empty
	 */
	@Post
	public Representation executeQueryPost(Representation entity)
			throws Exception {
		logger.debug("HTTP POST called ...");
	
		// check content type
		logger.debug("ContentType " + entity.getMediaType());
		String contentType = entity.getMediaType().toString();
		if (!contentType.contains("application/x-www-form-urlencoded")) {
			throw new MalformedSparqlQueryException(
					"Unsupported Content-Type in the HTTP request: \""
							+ contentType
							+ "\".  Only application/x-www-form-urlencoded is supported.");
		}
	
		String query="";
		// Read the body of the request.
		InputStream is = entity.getStream();
		if (is != null){
			StringBuilder reqBody = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			final int capacity = 8192;
			char[] buf = new char[capacity];
			int count;
			while ((count = isr.read(buf, 0, capacity)) > 0) {
				reqBody.append(buf, 0, count);
			}
			isr.close();
			query = reqBody.toString();
		}
		else
		{
			// get the query out of the HTTP POST command.
			Form form = new Form(entity);
			final String q = form.getFirstValue("method");
			if (q == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				throw new Exception("No method variable provided");
			}
			query=q;
		}
	
		
	
		java.net.URI uri = getRequest().getOriginalRef().toUri();
	
		ParsedRequest request = parseRequest(uri, query);
		logger.debug("Will parse the following request: "+query);
		return handleSparqlQuery(request.getSparql(),request.getParameters(),request.getInputURI());
	}

	/**
	 * Returns the results of the workflow.
	 * 
	 * @param entity
	 * 
	 * @return the results
	 * @throws Exception
	 */
	@Get
	public Representation executeQueryGet()
			throws Exception {
		logger.debug("HTTP GET called ...");
		Request r = getRequest();		
		// get the query out of the HTTP GET command.
		Form form = getQuery(); 
		final String q = form.getFirstValue("method");
		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new Exception("No method variable provided.");
		}
		java.net.URI uri = new URI(r.getRootRef().toString());
		ParsedRequest request = parseRequest(uri, form.getQueryString());
		return handleSparqlQuery(request.getSparql(),request.getParameters(),request.getInputURI());
	}

	protected ParsedRequest parseRequest(java.net.URI url, String query)
			throws APIException {
		logger.debug("Query String: "+query);
		ParsedRequest request=null;
        int methodIndexStart = query.indexOf("method=");
		if (methodIndexStart==-1) {
			throw new APIException("The variable \"method\" is required.");
		}
        int methodIndexEnd =  query.indexOf("&",methodIndexStart);
        if (methodIndexEnd==-1){
                methodIndexEnd = query.length();
        }
		String[] parts = query.split("&");
		if (parts == null) {
			throw new APIException(
					"The query part of the URL of the GET request is empty.");
		}
		String method="";
        method=query.substring(methodIndexStart+7,methodIndexEnd);
        logger.debug("Method:"+method);
        if (method.equals("compoundInfo")){
			request=compoundInfo(parts);				
		} else if (method.equals("proteinInfo")){
			request=proteinInfo(parts);
		} else if (method.equals("compoundPharmacology")){
			request=compoundPharmacology(parts);
		} else if (method.equals("proteinPharmacology")){
			request=proteinPharmacology(parts);		
		} else if (method.equals("enzymeClassPharmacology")){
			request=enzymeClassPharmacology(parts);
		} else {
			throw new APIException("Unknown method name: "+method+". Use one of: " );
		}
		try {
			QueryParserUtil.parseQuery(QueryLanguage.SPARQL,request.getSparql(), null);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			throw new APIException("Malformed query." );
		} catch (UnsupportedQueryLanguageException e) {
			e.printStackTrace();
			throw new APIException("Should never happen" );
		}
		return request;
	}
	
	private Representation handleSparqlQuery(String sparql, List<String> parameters, String inputURI ) {
		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof OPSAPIEndpointApp) {
			ep = ((OPSAPIEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());
		logger.debug("Query being processed by endpoint: " + ep.getURI());
		Set<Statement> statements = new HashSet<Statement>();
		ValueFactory rdfval = new ValueFactoryImpl();
		BNode bnode = rdfval.createBNode();
		statements.add(new StatementImpl(bnode, RDFConstants.RDF_TYPE, 	RDFConstants.LARKC_SPARQLQUERY));
		statements.add(new StatementImpl(bnode, RDFConstants.LARKC_HASSERIALIZEDFORM, new LiteralImpl(sparql)));
		for (String parameter : parameters) {
			statements.add(new StatementImpl(bnode, rdfval.createURI(EXPANDER_PARAMETER), new LiteralImpl(parameter)));
		}
		statements.add(new StatementImpl(bnode, rdfval.createURI(EXPANDER_INPUT), rdfval.createURI(inputURI) ));
		ex.execute(new SetOfStatementsImpl(statements), ep.getPathId());
		SetOfStatements resultsSetOfStatements = ex.getNextResults(ep.getPathId());
		/*CloseableIterator<Statement> stmtIter = resultsSetOfStatements.getStatements();
		StringWriter ttl=new StringWriter();
		RDFWriter rdfWriter=new TurtleWriterFactory().getWriter(ttl);
		try {
			rdfWriter.startRDF();
			while(stmtIter.hasNext()){
					rdfWriter.handleStatement(stmtIter.next());
				
			}
			rdfWriter.endRDF();
		} catch (RDFHandlerException e) {
				e.printStackTrace();
			}*/
		VariableBinding results = DataFactory.INSTANCE
				.createVariableBinding(resultsSetOfStatements);
		// prepare XML response
		SparqlResultFormatter formatter;
		String xmlResult="";
		try {
			formatter = new SparqlResultFormatter();
			formatter.buildSparqlRoot();
			formatter.buildSelectResults(results);
			xmlResult = xmlToString(sparql, formatter.getDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setStatus(Status.SUCCESS_OK);
		Representation rep = new StringRepresentation(xmlResult,
				MediaType.register("application/sparql-results+xml",
						"SPARQL endpoint response"));
		return rep;
	}
	
	private String xmlToString(String query, Document doc)
			throws SparqlException {
		try {
			TransformerFactory transFac = TransformerFactory.newInstance();
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446
			transFac.setAttribute("indent-number", new Integer(2));
			Transformer trans = transFac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			trans.transform(domSource, streamResult);
			String xmlResult = stringWriter.toString();
			return xmlResult;
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		} 
	}

	private ParsedRequest enzymeClassPharmacology(String[] parts) throws APIException {
		boolean hasMethod = false;
		String uri="";
		String sparql= "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
				"PREFIX chemspider: <http://rdf.chemspider.com/#> " +
				"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
				"PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
				"SELECT DISTINCT ?target_name ?target_class ?compound_name ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations " +
					"?std_type ?relation ?std_value ?std_unites ?assay_organism ?drug_name ?drug_type " +
				"WHERE { " +
					"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
						"?class rdfs:subClassOf ?class_uri  ; skos:prefLabel ?target_class . " +
						"?tid c2b2r_chembl:ec_number ?class . ?tid c2b2r_chembl:pref_name ?target_name . " +
						"?assay2target_uri c2b2r_chembl:tid ?tid ; " +
						"c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism . " +
						"?activity_uri farmbio:onAssay ?assay_uri ;  c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ; " +
						"c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ; " +
						"c2b2r_chembl:std_unites ?std_unites . ?csid_uri skos:exactMatch ?compound_uri " +
						"OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight } " +
						"OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
						"OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles } " +
						"OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi} " +
						"OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey} " +
					"} " +
					"GRAPH <http://larkc.eu#Fixedcontext> { " +
						"?compound_cw skos:exactMatch ?csid_uri ; skos:prefLabel ?compound_name " +
					"} " +
				"}" ;
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided");
				}
				hasMethod = true;
			} else if (name.equals("class")) {
				uri = "http://chem2bio2rdf.org/uniprot/resource/enzyme/"+value;
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
				}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		List<String> parameters=new ArrayList<String>();
		parameters.add("?class_uri");
		return new ParsedRequest(sparql, parameters, uri);
	}

	private ParsedRequest proteinPharmacology(String[] parts) throws APIException {
		boolean hasMethod = false;
		String uri="";
		String sparql = new Scanner(this.getClass().getResourceAsStream("/proteinPharmacology.sparql")).useDelimiter("\\Z").next();
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				uri=value.substring(1,value.length()-1);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
				}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		List<String> parameters=new ArrayList<String>();
		parameters.add("?cw_uri");
		parameters.add("?chembl_uri");
		return new ParsedRequest(sparql, parameters, uri);
	}


	private ParsedRequest compoundPharmacology(String[] parts) throws APIException {
		boolean hasMethod = false;
		String uri="";
		String sparql = new Scanner(this.getClass().getResourceAsStream("/compoundPharmacology.sparql")).useDelimiter("\\Z").next();
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				uri=value.substring(1,value.length()-1);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} else { throw new APIException( "Unknown parameter name: \""
						+ name + "\" for method compoundPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		List<String> parameters=new ArrayList<String>();
		parameters.add("?cw_uri");
		parameters.add("?csid_uri");
		parameters.add("?chembl_uri");
		return new ParsedRequest(sparql, parameters, uri);
	}


	private ParsedRequest proteinInfo(String[] parts) throws APIException {
		boolean hasMethod = false;
		String uri="";
		String sparql = new Scanner(this.getClass().getResourceAsStream("/proteinInfo.sparql")).useDelimiter("\\Z").next();
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				uri=value.substring(1,value.length()-1);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinInfo; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		List<String> parameters=new ArrayList<String>();
		parameters.add("?cw_uri");
		parameters.add("?chembl_uri");
		parameters.add("?db_uri");
		return new ParsedRequest(sparql, parameters, uri);
	}


	private ParsedRequest compoundInfo(String[] parts) throws APIException {
		boolean hasMethod = false;
		String uri="";
		String sparql = new Scanner(this.getClass().getResourceAsStream("/compoundInfo.sparql")).useDelimiter("\\Z").next();
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				uri=value.substring(1,value.length()-1);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method compoundInfo; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		List<String> parameters=new ArrayList<String>();
		parameters.add("?cw_uri");
		parameters.add("?csid_uri");
		parameters.add("?chembl_uri");
		parameters.add("?db_uri");
		return new ParsedRequest(sparql, parameters, uri);
	}
	
}