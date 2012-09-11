package eu.ops.endpoint.lda;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.restlet.Application;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.VariableBinding;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.executor.Executor;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.shared.SerializationHelper;

/**
 * The current Endpoint implementation offers two basic methods:
 * <ul>
 * <li>
 * <code>HTTP POST http://host:port/endpoint?query=QUERY_AS_STRING</code> <br/>
 * passes any query to the corresponding path and starts execution,</li>
 * <li><code>HTTP GET http://host:port/endpoint</code><br/>
 * retrieves the results as RDF/XML.</li>
 * </ul>
 */
public class LdaEndpointResource extends ServerResource {

	private final String[] OPS_variables = {"?chembl_uri" , "?cw_uri" , "?db_uri" , "?cs_uri"};
		
	private static Logger logger = LoggerFactory
			.getLogger(LdaEndpointResource.class);

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
	public Representation executeQueryPost(Representation entity) throws Exception {
		logger.debug("HTTP POST called ...");
		
		// get the query out of the HTTP POST command.
		Form form = new Form(entity);
		String q = form.getFirstValue("query");
		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new Exception("Empty query!");
		}
		try {
			DataFactory.INSTANCE.createSPARQLQuery(q);
		} catch (Exception e) {
			setStatus(Status.CONNECTOR_ERROR_INTERNAL);
			logger.debug(q);
			e.printStackTrace();
			throw new Exception("Malformed query!");
		}
		logger.debug("Query in valid SPARQL");
		/*if(!q.toUpperCase().contains("LIMIT")){
			q+=" LIMIT 100";
		}*/

		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof LdaEndpointApp) {
			ep = ((LdaEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());

		// Prepare a triple which holds the query.
		Set<Statement> statementSet = new HashSet<Statement>();
		ValueFactory rdfval = new ValueFactoryImpl();
		BNode bnode = rdfval.createBNode();
		//Find out which varibles to expand
		for (String parameter : OPS_variables) {
			if (q.toLowerCase().contains(parameter)) {
				statementSet.add(new StatementImpl(
						bnode, rdfval.createURI("http://www.openphacts.org/api#variableForExpansion"), new LiteralImpl(parameter)));
				logger.debug("Added variable for expansion:" + parameter);
			}
		}
		//get the input uri - first URI in a CONSTRUCT from the LDA 
		if (q.toUpperCase().indexOf("CONSTRUCT")>0){
			int uriStart = q.indexOf("<", q.toUpperCase().indexOf("CONSTRUCT"))+1;
			String uri = q.substring(uriStart , q.indexOf(">",uriStart));
			statementSet.add(new StatementImpl(
					bnode, rdfval.createURI("http://www.openphacts.org/api#inputForExpansion"), rdfval.createURI(uri) ));
			logger.debug("Added input URI for expansion: " + uri);
		}
		else {
			//Look for ops:input property
			logger.debug(q);
			if (q.indexOf("ops:input") > 0){
				int uriStart = q.indexOf("<",q.indexOf("ops:input"))+1;
				String uri = q.substring(uriStart , q.indexOf(">",uriStart));
				statementSet.add(new StatementImpl(
						bnode, rdfval.createURI("http://www.openphacts.org/api#inputForExpansion"), rdfval.createURI(uri) ));
				logger.debug("Added input URI for expansion: " + uri);
				BufferedReader br = new BufferedReader(new StringReader(q));
				String line=br.readLine();
				String replacement="";
				while (line!=null){
					if (!line.contains("ops:input"))
						replacement+=line+"\n";
					else 
						logger.debug("Removed ops:input line");
					line=br.readLine();
				}
				q=replacement;
			}
		}
		// Pass a set of statements containing the query to the executor.
		Resource subject = new BNodeImpl("query");
		Literal sparql = ValueFactoryImpl.getInstance().createLiteral(q);
		statementSet.add(new StatementImpl(subject, RDFConstants.RDF_TYPE, 	RDFConstants.LARKC_SPARQLQUERY));
		statementSet.add(new StatementImpl(subject, RDFConstants.LARKC_HASSERIALIZEDFORM, sparql));
		SetOfStatements setOfStatementsImpl = new SetOfStatementsImpl(
				statementSet);
		ex.execute(setOfStatementsImpl, ep.getPathId());

		if (q.toUpperCase().contains("CONSTRUCT") || q.toUpperCase().contains("DESCRIBE")) {
			logger.debug("Getting RDF response");
			return getRDF();
		}
		logger.debug("Getting SPARQL/XML response");
		return getSPARQLXML(q);
	}

	/**
	 * Returns the results of the workflow.
	 * 
	 * @return the results
	 * @throws Exception 
	 */
	@Get
	public Representation executeQueryGet() throws Exception {

		logger.debug("HTTP GET called ...");
		// get the query out of the HTTP GET command.
		Form form = getQuery(); 
		String q = form.getFirstValue("query");
		try {
			DataFactory.INSTANCE.createSPARQLQuery(q);
		} catch (Exception e) {
			setStatus(Status.CONNECTOR_ERROR_INTERNAL);
			logger.debug(q);
			e.printStackTrace();
			throw new Exception("Malformed query!");
		}
		logger.debug("Query in valid SPARQL");
		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		/*if(!q.toUpperCase().contains("LIMIT")){
			q+=" LIMIT 100";
		}*/
		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof LdaEndpointApp) {
			ep = ((LdaEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());

		// Prepare a triple which holds the query.
		Set<Statement> statementSet = new HashSet<Statement>();
		ValueFactory rdfval = new ValueFactoryImpl();
		BNode bnode = rdfval.createBNode();
		//Find out which varaibles to expand
		for (String parameter : OPS_variables) {
			if (q.toLowerCase().contains(parameter)) {
				statementSet.add(new StatementImpl(
						bnode, rdfval.createURI("http://www.openphacts.org/api#variableForExpansion"), new LiteralImpl(parameter)));
				logger.debug("Added variable for expansion:" + parameter);
			}
		}
		//get the input uri - first URI in a CONSTRUCT from the LDA 
		if (q.toUpperCase().indexOf("CONSTRUCT") > 0){
			int uriStart = q.indexOf("<", q.toUpperCase().indexOf("CONSTRUCT"))+1;
			String uri = q.substring(uriStart , q.indexOf(">",uriStart));
			statementSet.add(new StatementImpl(
					bnode, rdfval.createURI("http://www.openphacts.org/api#inputForExpansion"), rdfval.createURI(uri) ));
			logger.debug("Added input URI for expansion: " + uri);
		}
		else {
			//Look for ops:input property
			if (q.indexOf("ops:input") > 0){
				int uriStart = q.indexOf("<",q.indexOf("ops:input"))+1;
				String uri = q.substring(uriStart , q.indexOf(">",uriStart));
				statementSet.add(new StatementImpl(
						bnode, rdfval.createURI("http://www.openphacts.org/api#inputForExpansion"), rdfval.createURI(uri) ));
				logger.debug("Added input URI for expansion: " + uri);
				BufferedReader br = new BufferedReader(new StringReader(q));
				String line=br.readLine();
				String replacement="";
				while (line!=null){
					if (!line.contains("ops:input"))
						replacement+=line+"\n";
					else 
						logger.debug("Removed ops:input line");
					line=br.readLine();
				}
				q=replacement;
			}
		}
		// Pass a set of statements containing the query to the executor.
		Resource subject = new BNodeImpl("query");
		Literal sparql = ValueFactoryImpl.getInstance().createLiteral(q);
		statementSet.add(new StatementImpl(subject, RDFConstants.RDF_TYPE, 	RDFConstants.LARKC_SPARQLQUERY));
		statementSet.add(new StatementImpl(subject, RDFConstants.LARKC_HASSERIALIZEDFORM, sparql));
		SetOfStatements setOfStatementsImpl = new SetOfStatementsImpl(
				statementSet);
		ex.execute(setOfStatementsImpl, ep.getPathId());
		
		if (q.toUpperCase().contains("CONSTRUCT") || q.toUpperCase().contains("DESCRIBE")) {
			logger.debug("Getting RDF response");
			return getRDF();
		}
		logger.debug("Getting SPARQL/XML response");
		return getSPARQLXML(q);
	}
	
	private Representation getSPARQLXML(String q) {
		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof LdaEndpointApp) {
			ep = ((LdaEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());
		
		// Get the next results from the executor.
		SetOfStatements resultsSetOfStatements = ex.getNextResults(ep.getPathId());
		logger.debug("Got next results ...");
		VariableBinding results = DataFactory.INSTANCE
				.createVariableBinding(resultsSetOfStatements);
		// prepare XML response
		SparqlResultFormatter formatter;
		String xmlResult="";
		try {
			formatter = new SparqlResultFormatter();
			formatter.buildSparqlRoot();
			formatter.buildSelectResults(results);
			xmlResult = xmlToString(q, formatter.getDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setStatus(Status.SUCCESS_OK);
		Representation rep = new StringRepresentation(xmlResult,
				MediaType.register("application/sparql-results+xml",
						"SPARQL endpoint response"));
		return rep;
	}
	
	private Representation getRDF() {
		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof LdaEndpointApp) {
			ep = ((LdaEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());

		// Get the next results from the executor.
		SetOfStatements nextResults = ex.getNextResults(ep.getPathId());

		logger.debug("Got next results ...");

		// If the result is empty, return an empty rdf/xml document.
		if (nextResults == null) {
			return new StringRepresentation("", MediaType.APPLICATION_RDF_XML);
		}
		// Transform the SetOfStatements in an RDF/XML string.
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		RDFXMLWriter writer = new RDFXMLWriter(byteStream);
		SerializationHelper.printSetOfStatements(nextResults, byteStream,
				writer);
		String serializedStatements = new String(byteStream.toByteArray());

		// Set mime-type to APP RDF/XML and return result.
		Representation rep = new StringRepresentation(serializedStatements,
				MediaType.APPLICATION_RDF_XML);

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
	
}
