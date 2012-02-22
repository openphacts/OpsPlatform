import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;

import org.openrdf.model.Resource;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.turtle.TurtleParser;

public class NTripleParser {
	
	public static void parse(String file) {
		try {
			FileInputStream is = new FileInputStream(file);
			RDFParser rdfParser = new TurtleParser();
			
			ArrayList myList = new ArrayList();
			StatementCollector collector = new StatementCollector(myList);
			rdfParser.setRDFHandler(collector);
			
			try {
			   rdfParser.parse(is, "");
			} 
			catch (IOException e) {
			  // handle IO problems (e.g. the file could not be read)
			}
			catch (RDFParseException e) {
			  // handle unrecoverable parse error
			}
			catch (RDFHandlerException e) {
			  // handle a problem encountered by the RDFHandler
			}
			
//			for (Iterator iter = myList.iterator(); iter.hasNext();) {
//				Statement s = (Statement)iter.next();
//				
//				System.out.println(s.getSubject().stringValue() + " " + s.getPredicate().stringValue() + " " + s.getObject());
//				if (s.getObject() instanceof Literal) {
//					System.out.println("LITERAL");
//				}
//				else if (s.getObject() instanceof Resource) {
//					System.out.println("RESOURCE");
//				}
//				else {
//					System.out.println("SOMETHING ELSE");
//				}
//			}
//			System.out.println(myList.size());
			
			// create table
			HBaseTableCreate.createTable(FilenameUtils.removeExtension(file));
			
			// create table column families
			ArrayList<String> predicates = new ArrayList();
			for (Iterator iter = myList.iterator(); iter.hasNext();) {
				Statement s = (Statement)iter.next();
				predicates.add(s.getPredicate().stringValue());
			}
			HBaseTableCreate.createColumnFamilies(FilenameUtils.removeExtension(file), predicates);
			
			// populate table
			for (Iterator iter = myList.iterator(); iter.hasNext();) {
				Statement s = (Statement)iter.next();
			}
		}
		catch (Exception e) {
			// FileInputStream exception
		}
	}
	
	public static void main(String[] args) {
		parse("/home/anca/Documents/OPS/trials/pdsp-v2.ttl");
	}

}