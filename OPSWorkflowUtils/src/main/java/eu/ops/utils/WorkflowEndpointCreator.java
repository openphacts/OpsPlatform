package eu.ops.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class WorkflowEndpointCreator {
	private static String LARKC_URL = "http://localhost:8182";
	private static OPSClient runner;
	private static String larkcUrl;
	private static boolean runTest = false;
	private static Options options;
	private static String TEST_QUERY = "Select * WHERE {?s ?p ?q} LIMIT 10";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = processCommandLine(args);
		if (filename == null) {
			return;
		}
		File workflowFile = new File(filename);
		
		if (!workflowFile.isAbsolute()) {
			workflowFile = new File(new File("").getAbsoluteFile(), filename);
		}
 
		runner = new OPSClient();
		try {
			runner.setServiceUrl(new URI(larkcUrl));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(workflowFile));
			String line = br.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = br.readLine();
			}
			try {
				String workflowId = runner.loadWorkflowDefinition(sb.toString());
				if (workflowId == null) {
					System.err.println("Failed to load workflow");
				} else {
					String endpoint = runner.getSparqlEndpoint(workflowId);
					if (runTest) {
						String result = runner.runQuery(endpoint, TEST_QUERY);
						System.out.println(result);
					}
					System.out.println(endpoint);
				}
			} catch (ConnectException e) {
				System.err.println("Failed to connect: "+e.getMessage());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static String processCommandLine(String[] args) {
		// create the command line parser
		CommandLineParser parser = new GnuParser();

		// create the Options
		options = new Options();
		options.addOption( "larkc", true, "base URL for LarKC server" );
		options.addOption( "test", false, "run a SPARQL test query" );

		try {
		    // parse the command line arguments
		    CommandLine line = parser.parse( options, args );

		    if(!line.hasOption( "larkc" ) ) {
		    	larkcUrl = LARKC_URL;
		    } else {
		    	larkcUrl = line.getOptionValue("larkc");
		    }
		    larkcUrl = larkcUrl+"/rdf/workflows";
		    
		    if(line.hasOption( "test" ) ) {
		    	runTest = true;
		    }
		    
		    String[] remaining = line.getArgs();
		    if (remaining.length == 0) {
		    	usage();
		    } else {
		    	return remaining[0];
		    }
		    
		}
		catch( ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		    usage();
		} 

    	return null;
		
	}
	
	private static void usage() {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( "OPSClient [OPTIONS] WORKFLOW", options );
	}

}
