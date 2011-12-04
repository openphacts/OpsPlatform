package eu.larkc.plugin.SailRdfFileReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.ntriples.NTriplesParserFactory;
import org.openrdf.rio.rdfxml.RDFXMLParserFactory;
import org.openrdf.rio.trig.TriGParserFactory;
import org.openrdf.rio.turtle.TurtleParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.RdfStoreConnection;
import eu.larkc.core.data.SAILRdfStoreConnectionImpl;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.util.RDFConstants;

/**
 * Reads files from HDFS and loads them onto the data layer. Can also read files from the local filesystem.
 * It takes the file or directory path as input and output the label of the graph. Optionally, the label can be
 * specified as a parameter. If no label is specified, an arbitrary label is used.
 * 
 * PARAMETERS- _: larkc:defaultoutputname <label uri> (optional)
 * INPUT- _:larkc:filePath <literal pointing to a file>
 * OUTPUT- _: larkc:defaultoutputname <label uri>
 * 
 * @author spyros
 *
 */
public class SailRdfFileReader extends GenericHadoopPlugin {

	public static final URI fixedContext=new URIImpl("http://larkc.eu#Fixedcontext");
	private URI outputGraphName;
	
	/** The logger. */
	protected final Logger logger = LoggerFactory
			.getLogger(SailRdfFileReader.class);
	
	/**
	 * filter to ignore hidden files
	 */
	public static final PathFilter FILTER_ONLY_HIDDEN = new PathFilter() {
		public boolean accept(Path p) {
			String name = p.getName();
			return !name.startsWith("_") && !name.startsWith(".");
		}
	};
	
	/**
	 * 
	 * @param pluginName
	 */
	public SailRdfFileReader(URI pluginName) {
		super(pluginName);
	}


	@Override
	protected SetOfStatements invokeInternal(SetOfStatements input) {
		// Get the files to be read
		List<String> paths=DataFactory.INSTANCE.extractObjectsForPredicate(input, Constants.FILEPATH);
		
		// Open a connection to the data layer
		final RdfStoreConnection connection=DataFactory.INSTANCE.createRdfStoreConnection();
		
		// If no label is given, use an arbitrary one
		final URI label;
		if (outputGraphName==null)
			label=new URIImpl("http://larkc.eu#arbitraryGraphLabel" + UUID.randomUUID());
		else
			label=outputGraphName;
		
		logger.info("Will write to labelled graph:" + label);
		
		for (String path: paths) {
			logger.info("Reading from  " + path);
			try {
				Path inPath=new Path(path);
				FileSystem dfs=FileSystem.get(config);
				FileStatus[] files=dfs.listStatus(inPath, FILTER_ONLY_HIDDEN);
		
				for (FileStatus f : files) {
					FSDataInputStream is = dfs.open(f.getPath());
					InputStream inStream=null;
					if (f.getPath().getName().endsWith(".gz"))
						inStream=new GZIPInputStream(is);
					else if (f.getPath().getName().endsWith(".zip"))
						inStream=new ZipInputStream(is);
					else
						inStream=is;
					RDFFormat format;
					// Set up the parser
					//RDFParser parser=null;
					if (f.getPath().getName().contains(".xml")) {
						//parser = new RDFXMLParser();
						RDFParserRegistry.getInstance().add(new RDFXMLParserFactory());
						format=RDFFormat.RDFXML;
					}
					//else if (f.getPath().getName().contains(".nq"))
						//parser = new net.fortytwo.sesametools.nquads.NQuadsParser();
						//format=RDFFormat.
					else if (f.getPath().getName().contains(".nt")){
						//parser = new NTriplesParser();
						RDFParserRegistry.getInstance().add(new NTriplesParserFactory());
						format=RDFFormat.NTRIPLES;
					}
					else if (f.getPath().getName().contains(".ttl")){
						//parser = new TurtleParser();
						RDFParserRegistry.getInstance().add(new TurtleParserFactory());
						format=RDFFormat.TURTLE;
					}
					else if (f.getPath().getName().contains(".trig")){
						RDFParserRegistry.getInstance().add(new TriGParserFactory());
						format=RDFFormat.TRIG;
						//parser = new TriGParser();
					}
					else {
						logger.warn("Unrecognised file extension. Ignoring file: " + f.getPath());
						inStream.close();
						continue;
					}
					/*parser.setStopAtFirstError(false);
					parser.setVerifyData(false);
					parser.setDatatypeHandling(DatatypeHandling.IGNORE);
					parser.setRDFHandler(new RDFHandlerBase() {
						public void handleStatement(Statement s) {
							if (s.getContext()!=null)
								connection.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), (URI)s.getContext());//, label);
							else
								connection.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), fixedContext);//, label);
						}
					});					
					
					parser.parse(inStream, outputGraphName.toString());
					inStream.close();*/
					((SAILRdfStoreConnectionImpl) connection).addFile(inStream,format,outputGraphName.toString(),fixedContext);
					logger.info("Did read from file  " + f.getPath());
				}
			}
			catch (Exception e) {
				throw new IllegalStateException("Error loading file " + path,e);
			}
		}
		
		connection.close();
		// Create the metadata for the output
		ArrayList<Statement> l=new ArrayList<Statement>();
		l.add(new StatementImpl(new BNodeImpl(UUID.randomUUID()+""), RDFConstants.DEFAULTOUTPUTNAME, label));
		
		return new SetOfStatementsImpl(l);
	}
	





	/* (non-Javadoc)
	 * @see eu.larkc.plugin.hadoop.GenericHadoopPlugin#initialiseInternal(eu.larkc.core.data.SetOfStatements)
	 */
	@Override
	protected void initialiseInternal(SetOfStatements params) {
		super.initialiseInternal(params);
		// Get the label of the graph
		outputGraphName=super.getNamedGraphFromParameters(params, RDFConstants.DEFAULTOUTPUTNAME);
	}


	@Override
	protected void shutdownInternal() {
	}
	
	/**
	 * For testing
	 * @param args
	 */
	public static void main(String[] args) {
		String path="/Users/spyros/Documents/Papers/www11tut/timbl-data/1301337846705pool/output-decompressed1301337898753/triples";
		
		DataFactory.INSTANCE.createRdfStoreConnection().removeStatement(null, null, null, null);
		
		SailRdfFileReader r=new SailRdfFileReader(new URIImpl("http://boo"));
		r.initialiseInternal(new SetOfStatementsImpl());
		
		List<Statement> sts=new ArrayList<Statement>();
		sts.add(new StatementImpl(new BNodeImpl("blahblah"), new URIImpl("http://larkc.eu/schema#filePath"), new LiteralImpl(path)));
		
		r.invoke(new SetOfStatementsImpl(sts));
	}
	
}
