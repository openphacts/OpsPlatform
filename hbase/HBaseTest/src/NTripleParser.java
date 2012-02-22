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

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;


public class NTripleParser {
	
	public static void parse(String file) throws IOException {
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
			
			String tableName = FilenameUtils.removeExtension(FilenameUtils.getName(file));
			
			// create table column families
			ArrayList<String> predicates = new ArrayList();
			for (Iterator iter = myList.iterator(); iter.hasNext();) {
				Statement s = (Statement)iter.next();
				if (s.getObject() instanceof Resource) {
					predicates.add(s.getPredicate().stringValue());
				}
				else {
					// predicates.add("literal:" + s.getPredicate().stringValue());
				}
			}
			HBaseUtil.createTableStruct(tableName, predicates);
			
			// populate table
			HBaseConfiguration conf = new HBaseConfiguration();
		    conf.set("hbase.master","localhost:60000");
			for (Iterator iter = myList.iterator(); iter.hasNext();) {
				HTable table = new HTable(conf, tableName);
				
				Statement s = (Statement)iter.next();
				if (s.getObject() instanceof Resource){
					HBaseUtil.addRow(table, s.getSubject().toString(), s.getPredicate().toString(), "", s.getObject().toString());
				}
				else {
					HBaseUtil.addRow(table, s.getSubject().toString(), "literal", s.getPredicate().toString(), s.getObject().toString());
				}
			}
		}
		catch (Exception e) {
			// FileInputStream exception
		}
	}
	
	public static void main(String[] args) {
		try {
			parse(args[0]);
		}
		catch (Exception e) {
		}
	}

}
