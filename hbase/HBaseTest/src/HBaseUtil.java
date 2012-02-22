import java.util.ArrayList;
import java.util.Iterator;
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


public class HBaseUtil {	
	public static void createTableStruct(String table, ArrayList<String> columns)  throws IOException {
		HBaseConfiguration conf = new HBaseConfiguration();
	    conf.set("hbase.master","localhost:60000");
	    HBaseAdmin hbase = new HBaseAdmin(conf);
	    
	    HTableDescriptor desc;
	    
	    if (hbase.tableExists(table) == false) {
	    	desc = new HTableDescriptor(table);
	    	HColumnDescriptor literal = new HColumnDescriptor("literal".getBytes());
	    	desc.addFamily(literal);
	    }
	    else {
		     desc = hbase.getTableDescriptor(table.getBytes());
	    }
	    
		for (Iterator iter = columns.iterator(); iter.hasNext();) {
			String columnName = ((String)iter.next()).replaceAll("[^A-Za-z0-9 ]", "");
//			System.out.println("COLUMN: " + columnName);
			
			HColumnDescriptor c = new HColumnDescriptor(columnName.getBytes());
			if (desc.hasFamily(columnName.getBytes()) == false) {
				desc.addFamily(c);
			}
		}

	    if (hbase.tableExists(table) == false) {
	    	hbase.createTable(desc);
	    }
	}
	
	public static void addRow(HTable table, String key, String columnFam, String columnName, String val) throws IOException {
	    Put row = new Put(Bytes.toBytes(key));
	    row.add(Bytes.toBytes(columnFam.replaceAll("[^A-Za-z0-9 ]", "")), Bytes.toBytes(columnName.replaceAll("[^A-Za-z0-9 ]", "")), Bytes.toBytes(val));
	    table.put(row);
	}
}
