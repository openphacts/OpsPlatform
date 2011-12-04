package eu.larkc.plugin.SailRdfFileReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.AttributeValueMap;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.plugin.Plugin;
/**
 * Abstract Plugin to ease integration with Hadoop.
 * @author spyros
 *
 */
public abstract class GenericHadoopPlugin extends Plugin {
	
	public static final String FSSERVER = GenericHadoopPlugin.class.getName()+"fs.default.name";
	public static final String JOBTRACKER = GenericHadoopPlugin.class.getName()+"mapred.job.tracker";
	public static final String SOCKSPROXY = GenericHadoopPlugin.class.getName()+"hadoop.socks.server";
	public static final String HDFSUSERNAME = GenericHadoopPlugin.class.getName()+"hadoop.job.ugi";
	
	public static final String MAPTASKS = GenericHadoopPlugin.class.getName()+"-Maptasks";
	public static final String REDUCETASKS = GenericHadoopPlugin.class.getName()+"-Reducetasks";

	public static final String TOOLNAME = GenericHadoopPlugin.class.getName()+"toolname";
	public static final String ARGS = GenericHadoopPlugin.class.getName()+"arguments";
	private static final String DRYRUN = GenericHadoopPlugin.class.getName()+"-DryRun";	
	
	private static Logger logger = LoggerFactory.getLogger(GenericHadoopPlugin.class);
	protected Configuration config;
	private String toolName;
	private String[] args;
	protected int noReduceTasks=2;
	protected int noMapTasks=2;
	protected boolean dryRun=false;
	
	public GenericHadoopPlugin(URI pluginName) {
		super(pluginName);
	}

	protected Configuration configHadoop(SetOfStatements input) {
		AttributeValueMap attval=DataFactory.INSTANCE.createAttributeValueList(input);
		
		Configuration config=new Configuration();
		
		String mapTasks=attval.get(MAPTASKS);
		if (mapTasks!=null) {
			this.noMapTasks=Integer.parseInt(mapTasks);
		}
		
		String reduceTasks=attval.get(REDUCETASKS);
		if (reduceTasks!=null) {
			this.noReduceTasks=Integer.parseInt(mapTasks);
		}
		
		String proxy=attval.get(SOCKSPROXY);
		if (proxy!=null) {
			config.set("hadoop.socks.server", proxy);
			config.set("hadoop.rpc.socket.factory.class.default", "org.apache.hadoop.net.SocksSocketFactory");
		}
		
		String tracker=attval.get(JOBTRACKER);
		if (tracker!=null) {
			config.set("mapred.job.tracker", tracker);
			logger.debug("mapred.job.tracker=" + config.get("mapred.job.tracker"));
		}
		
		String fs=attval.get(FSSERVER);
		if (fs!=null) {
			config.set("fs.default.name", fs);
			logger.debug("fs.default.name=" + config.get("fs.default.name"));
		}
				
		String fsUser=attval.get(HDFSUSERNAME);
		if (fsUser!=null)
			config.set("hadoop.job.ugi", fsUser);
		return config;
	}
	
	/**
	 * Run the tool with the given configuration, name and arguments
	 * @param config
	 * @param toolName
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	protected void runTool(Configuration config, String toolName, String[] args)
		throws ClassNotFoundException, InstantiationException,
		IllegalAccessException, NoSuchMethodException,
		InvocationTargetException {
		
		
		String[] newArgs=Arrays.copyOf(args,args.length+4);
		newArgs[newArgs.length-2]="--reducetasks";
		newArgs[newArgs.length-1]=this.noReduceTasks+"";
		newArgs[newArgs.length-4]="--maptasks";
		newArgs[newArgs.length-3]=this.noMapTasks+"";
		
		logger.info("Running tool " + toolName + " with arguments " + Arrays.toString(args));
		
		logger.debug("mapred.job.tracker=" + config.get("mapred.job.tracker"));
		logger.debug("fs.default.name=" + config.get("fs.default.name"));

		
		// Get tool for the job
		Class<?> toolClass=Class.forName(toolName);
		if (toolClass==null)
			throw new InstantiationException("Class for tool not found");
		Tool tool=(Tool) toolClass.newInstance();
		Configured configured=(Configured) tool;
		Method runMethod=toolClass.getMethod("run", String[].class);
		
		Method configureMethod=toolClass.getMethod("setConf", Configuration.class);
		configureMethod.invoke(configured, config);
		
		if (!dryRun)
			runMethod.invoke(tool, (Object)args);
	}
	
	protected Path ensurePathIsOnDFS(Configuration config, Path inputPath) throws IOException {
		
		FileSystem hdfs=FileSystem.get(config);
		
		// If the input is on the local filesystem, upload it to the cluster
		LocalFileSystem localFS = FileSystem.getLocal(config);
		FileSystem inputFS = inputPath.getFileSystem(config);
		if ((inputFS.equals(localFS) && !hdfs.equals(localFS)) || !hdfs.exists(inputPath)) {
			Path remotePath=new Path(inputPath.getName()).makeQualified(hdfs);
			if (!dryRun)
				Util.copyFileOrDirectory(localFS,inputPath, hdfs, remotePath, config);
			//hdfs.copyFromLocalFile(inputPath, remotePath);
			logger.info("Copied files from local " + inputPath + " to remote " + remotePath);
			inputPath=remotePath;
		}
		else {
			logger.info("Path " + inputPath + " is on HDFS");
		}
		return inputPath;
	}
	
	protected Path ensurePathIsLocal(Configuration config, Path inputPath) throws IOException {
		
		FileSystem hdfs=FileSystem.get(config);
		
		// If the input is on the cluster, upload it to the local filesysten
		LocalFileSystem localFS = FileSystem.getLocal(config);
		FileSystem inputFS = inputPath.getFileSystem(config);
		// Unless path is local or hdfs is the local filesystem
		if (!(inputFS.equals(localFS) || hdfs.equals(localFS))) {
			Path localPath=new Path(inputPath.getName()).makeQualified(localFS);
			if (!dryRun)
				Util.copyFileOrDirectory(inputFS,inputPath, localFS, localPath, config);
			//hdfs.copyFromLocalFile(inputPath, remotePath);
			logger.info("Copied files from remote " + inputPath + " to local " + localPath);
			inputPath=localPath;
		}
		else {
			logger.info("Path " + inputPath + " is local");
		}
		return inputPath;
	}
	
	

	@Override
	protected void initialiseInternal(SetOfStatements params) {
		AttributeValueMap attVal=DataFactory.INSTANCE.createAttributeValueList(params);
		
		String dryRun=attVal.get(DRYRUN);
		if (dryRun!=null && (dryRun.equalsIgnoreCase("true") || dryRun.equalsIgnoreCase("yes"))) {
			this.dryRun=true;
		}
		
		this.toolName=attVal.get(TOOLNAME);
		String hadoopArgs = attVal.get(ARGS);
		if (hadoopArgs!=null)
			this.args=hadoopArgs.split(" "); // FIXME handle splitting arguments properly (i.e. deal with spaces)
		this.config=configHadoop(params);
		
	}

	@Override
	protected SetOfStatements invokeInternal(SetOfStatements input) {

		try {
			runTool(config, toolName, args);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return input; // FIXME not sure what to return here
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
