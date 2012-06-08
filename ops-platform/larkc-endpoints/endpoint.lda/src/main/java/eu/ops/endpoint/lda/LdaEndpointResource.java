package eu.ops.endpoint.lda;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
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

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.core.executor.Executor;
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
	public Representation executeQuery(Representation entity) throws Exception {
		// ---------------------------------------------------------
		// TODO: Sample code how a HTTP POST command can be handled.
		// ---------------------------------------------------------

		logger.debug("HTTP POST called ...");
		
		// get the query out of the HTTP POST command.
		Form form = new Form(entity);
		final String q = form.getFirstValue("query");
		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new Exception("Empty query!");
		}

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
		Resource subject = new BNodeImpl("query");
		URI predicate = WorkflowDescriptionPredicates.PLUGIN_PARAMETER_QUERY;
		Literal object = ValueFactoryImpl.getInstance().createLiteral(q);
		statementSet.add(new StatementImpl(subject, predicate, object));

		// Pass a set of statements containing the query to the executor.
		SetOfStatements setOfStatementsImpl = new SetOfStatementsImpl(
				statementSet);
		ex.execute(setOfStatementsImpl, ep.getPathId());

		// Prepare the Representation that is returned (and e.g. displayed in the browser).
		StringBuilder sb = new StringBuilder();
		sb.append("<p>Query submitted to endpoint <b>");
		sb.append(ep.getURI());
		sb.append("</b>.</p>");
		// set response
		setStatus(Status.SUCCESS_CREATED);

		Representation rep = new StringRepresentation(sb.toString(),
				MediaType.TEXT_HTML);
		return rep;
	}

	/**
	 * Returns the results of the workflow.
	 * 
	 * @return the results
	 */
	@Get
	public Representation getResults() {
		// --------------------------------------------------------
		// TODO: Sample code how a HTTP GET command can be handled.
		// --------------------------------------------------------

		logger.debug("HTTP GET called ...");

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
}
