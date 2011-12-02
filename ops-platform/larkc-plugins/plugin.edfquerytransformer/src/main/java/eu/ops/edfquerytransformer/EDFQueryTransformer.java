package eu.ops.edfquerytransformer;

import org.openrdf.model.URI;

import eu.larkc.core.data.SetOfStatements;
import eu.larkc.plugin.Plugin;

/**
 * <p>Generated LarKC plug-in skeleton for <code>eu.ops.edfquerytransformer.EDFQueryTransformer</code>.
 * Use this class as an entry point for your plug-in development.</p>
 */
public class EDFQueryTransformer extends Plugin
{

	/**
	 * Constructor.
	 * 
	 * @param pluginUri 
	 * 		a URI representing the plug-in type, e.g. 
	 * 		<code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public EDFQueryTransformer(URI pluginUri) {
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
		logger.info("EDFQueryTransformer initialized. Hello World!");
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
		logger.info("EDFQueryTransformer working.");
		return null;
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
