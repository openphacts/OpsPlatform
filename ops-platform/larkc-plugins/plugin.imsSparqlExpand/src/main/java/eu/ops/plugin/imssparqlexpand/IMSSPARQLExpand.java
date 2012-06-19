package eu.ops.plugin.imssparqlexpand;

import java.util.ArrayList;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpanderWSClient;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpander;
import uk.ac.man.cs.openphacts.queryexpander.QueryExpansionException;

/**
 * The <code>eu.ops.plugin.imssparqlexpand.IMSSPARQLExpand</code> is a LarKC
 * plugin. It expands a given SPARQL query into a UNION query where each
 * sub-query uses different, but equivalent URIs. The equivalent URIs are
 * retrieved from the the prototype IMS service.
 */
public class IMSSPARQLExpand extends Plugin {

    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);
    static final String EXPANDER_SERVICE_PARAM = "http://larkc.eu/schema#ExpanderService";
    //Default is used if no no EXPANDER_SERVICE_PARAM found;
    static final String DEFAULT_EXPANDER_SERVICE_ADDRESS = "http://rpc466.cs.man.ac.uk:8080/QueryExpander";
    static final String EXPANDER_PARAMETER = "http://www.openphacts.org/api#variableForExpansion";
	static final String EXPANDER_INPUT = "http://www.openphacts.org/api#inputForExpansion";
    private String expanderServiceAddress;
    private QueryExpander queryExpander;
    
    /**
     * Constructor.
     * 
     * @param pluginUri 
     * 		a URI representing the plug-in type, e.g. 
     * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
     */
    public IMSSPARQLExpand(URI pluginUri) {
        super(pluginUri);
    }

    /**
     * Called on plug-in initialisation. The plug-in instances are initialised on
     * workflow initialisation.
     * 
     * @param workflowDescription 
     * 		set of statements containing plug-in specific 
     * 		information which might be needed for initialization (e.g. plug-in parameters).
     */
    @Override
    protected void initialiseInternal(SetOfStatements params) {
        expanderServiceAddress = DEFAULT_EXPANDER_SERVICE_ADDRESS;
        if (params != null) {
            CloseableIterator<Statement> parameters = params.getStatements();
            while (parameters.hasNext()) {
                Statement stmt = parameters.next();
                if (stmt.getPredicate().equals(new URIImpl(EXPANDER_SERVICE_PARAM))) {
                    String proposedExpanderServiceAddress = stmt.getObject().stringValue();
                    if (proposedExpanderServiceAddress != null){
                        expanderServiceAddress = proposedExpanderServiceAddress;
                    }
                }
            }
        }
        queryExpander = instantiateQueryExpander();
        if (logger.isDebugEnabled()) {
            logger.debug("expanderServiceAddress=" + expanderServiceAddress);
        }
        logger.info("IMSSPARQLExpand initialized.");
        //ystem.out.println("*********************Initialised!!!");
    }

    QueryExpander instantiateQueryExpander() {
        //ystem.out.println("*********************");
        return new QueryExpanderWSClient(expanderServiceAddress);
    }

    /**
     * Called on plug-in invokation. The actual "work" should be done in this method.
     * <p>
     * For testing and none Larkc use it is better to throw the exceptions.
     * @param input 
     * 		a set of statements containing the input for this plug-in
     * 
     * @return a set of statements containing the output of this plug-in
     */
    public final SetOfStatements invokeInternalWithExceptions(SetOfStatements input)
            throws QueryExpansionException {
        logger.info("SPARQLExpand working.");
        //ystem.out.println("*********************Invoked!!!");
        if (logger.isDebugEnabled()) {
            logger.debug("Input: " + input.getStatements().toString());
        }
        //ystem.out.println("Input: " + input.getStatements().toString());
        // Does not care about the input name since it has a single argument, use any named graph
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);
        ArrayList<String> parameters = new ArrayList<String>();
        String inputURI = null;
        CloseableIterator<Statement> iter = input.getStatements();
        while (iter.hasNext()){
        	Statement statement=iter.next();
        	if (statement.getPredicate().equals(new URIImpl(EXPANDER_PARAMETER))){
        		parameters.add(statement.getObject().stringValue());
        	}
        	if (statement.getPredicate().equals(new URIImpl(EXPANDER_INPUT))) {
        		inputURI = statement.getObject().stringValue();
        	}
        }
        SetOfStatements output=input;
        if (!parameters.isEmpty() && inputURI != null){
        	String expandedQueryString = queryExpander.expand(query.toString(),parameters,inputURI);
        	logger.debug("Expanded query: "+expandedQueryString);
        	if (expandedQueryString.trim().endsWith("}")) {
        		expandedQueryString=expandedQueryString.replaceAll("} \n\nLIMIT", "\n\nLIMIT") + "}";
        		logger.debug("Bad limit replaced");
        	}
        	output = new SPARQLQueryImpl(expandedQueryString).toRDF();
        }
        return output;
    }

    /**
     * Called on plug-in invokation. The actual "work" should be done in this method.
     * <p>
     * Larkc can not handle exceptions so best to catch and log them and just return input.
     * @param input 
     * 		a set of statements containing the input for this plug-in
     * 
     * @return a set of statements containing the output of this plug-in
     */
    @Override
    protected SetOfStatements invokeInternal(SetOfStatements input) {
        try {
            SetOfStatements result = invokeInternalWithExceptions(input);
            logger.info("Query expansion successful: " + result.toString());
            return result;
        } catch (QueryExpansionException ex) {
            logger.warn("Problem writing expanded query.", ex);
        }
        //Failed so return input
        logger.info("IMSSPARQLExpand: ERROR: Returning input");
        return input;
    }

    /**
     * Called on plug-in destruction. Plug-ins are destroyed on workflow deletion.
     * Free an resources you might have allocated here.
     */
    @Override
    protected void shutdownInternal() {
        // TODO Auto-generated method stub
    }

 }
