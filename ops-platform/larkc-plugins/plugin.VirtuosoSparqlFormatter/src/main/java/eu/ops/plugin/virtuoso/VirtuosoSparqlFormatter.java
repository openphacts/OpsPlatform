package eu.ops.plugin.virtuoso;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.util.RDFConstants;
import eu.larkc.plugin.Plugin;

/**
 * <p>Generated LarKC plug-in skeleton for <code>eu.ops.plugin.virtuoso.VirtuosoSparqlFormatter</code>.
 * Use this class as an entry point for your plug-in development.</p>
 */
public class VirtuosoSparqlFormatter extends Plugin
{

	/**
	 * Constructor.
	 * 
	 * @param pluginUri 
	 * 		a URI representing the plug-in type, e.g. 
	 * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public VirtuosoSparqlFormatter(URI pluginUri) {
		super(pluginUri);
		// TODO Auto-generated constructor stub
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
	protected void initialiseInternal(SetOfStatements workflowDescription) {
		// TODO Auto-generated method stub
		logger.info("VirtuosoSparqlFormatter initialized. Hello World!");
	}

	/**
	 * Called on plug-in invokation. The actual "work" should be done in this method.
	 * 
	 * @param input 
	 * 		a set of statements containing the input for this plug-in
	 * 
	 * @return a set of statements containing the output of this plug-in
	 */
	@Override
	protected SetOfStatements invokeInternal(SetOfStatements input) {
		logger.info("VirtuosoSparqlFormatter working.");
		Set<Statement> output = new HashSet<Statement>();
		ValueFactory rdfval = new ValueFactoryImpl();
		BNode bnode = rdfval.createBNode();
		CloseableIterator<Statement> iter = input.getStatements();
		while (iter.hasNext()){
			Statement s = iter.next();
			if (s.getPredicate().equals(RDFConstants.LARKC_HASSERIALIZEDFORM)) {
				output.add(new StatementImpl(bnode, RDFConstants.RDF_TYPE, 	RDFConstants.LARKC_SPARQLQUERY));
				output.add(new StatementImpl(bnode, RDFConstants.LARKC_HASSERIALIZEDFORM, 
						new LiteralImpl(formatQuery(s.getObject().stringValue()))));
			}
			else
				output.add(s);
		}
		return new SetOfStatementsImpl(output);
	}

	private String formatQuery(String sparql) {
		return sparql.replaceAll("(?i)\\([ ]*GROUP_CONCAT[ ]*\\([ ]*DISTINCT", "( sql:GROUP_DIGEST (")
				.replaceAll("(?i)GROUP_CONCAT", "sql:GROUP_CONCAT")
				.replaceAll("(?i);[ ]*SEPARATOR[ ]*=[ ]*", ", ")
				.replaceAll("(?i) sql:GROUP_DIGEST \\([ a-z?]*, \" , \"", "$0 , 1000 , 1");
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
