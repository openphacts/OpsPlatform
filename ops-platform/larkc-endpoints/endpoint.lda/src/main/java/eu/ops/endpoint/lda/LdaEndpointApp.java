package eu.ops.endpoint.lda;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.endpoint.Endpoint;

/**
 * This class implements the application for the endpoint.
 * 
 * -----------------------------------
 * No changes are needed in this class
 * -----------------------------------
 */
public class LdaEndpointApp extends Application {

	private static Logger logger = LoggerFactory.getLogger(LdaEndpoint.class);
	private final Endpoint endpoint;

	/**
	 * Constructor that takes the executor as parameter.
	 * 
	 * @param endpoint
	 *            the executor that is responsible for this endpoint
	 */
	public LdaEndpointApp(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {
		logger.debug("Endpoint createInboundRoot called ...");
		Router router = new Router(getContext());
		router.attachDefault(LdaEndpointResource.class);
		return router;
	}

	/**
	 * Getter. Retrieves the endpoint.
	 * 
	 * @return the endpoint
	 */
	public Endpoint getEndpoint() {
		return endpoint;
	}

}
