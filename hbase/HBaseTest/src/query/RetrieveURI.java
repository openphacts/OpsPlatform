package query;

import java.util.ArrayList;
import java.util.Iterator;

import util.*;

public class RetrieveURI {
	
	public static void retrieve(String URI, String namespace, String outFile) {
		try {
			ArrayList<ArrayList<String>> triples = HBaseUtil.getRow(URI, namespace);
			
			for (Iterator<ArrayList<String>> it = triples.iterator(); it.hasNext();) {
				ArrayList<String> triple = (ArrayList<String>)it.next();
				int index = 0;
				
				for (Iterator<String> jt = triple.iterator(); jt.hasNext();) {
					String res = (String)jt.next();
					index++;
				}
			}
		}
		catch (Exception e) {
		}
	}

}
