/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand.ims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Christian
 */
public class TestGraphToNameSpaces implements GraphToNameSpaces{

    private HashMap<String,List<String>> allowedNamespaces;
    
    public TestGraphToNameSpaces(){
        setupHardCodedFilter();
    }
    
    private void setupHardCodedFilter() {
        allowedNamespaces = new HashMap<String,List<String>>();
        ArrayList<String> nameSpaces;
        
        //One from My test to be changed.
        //http://PDSP_DB/Data
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://www.example.org#");
        allowedNamespaces.put("testGraph", nameSpaces);
        
    }

    @Override
    public List<String> getNameSpacesInGraph(String graph) {
        List<String> nameSpaces = allowedNamespaces.get(graph);
        if (nameSpaces != null){
            return nameSpaces;
        } else {
            return nameSpaces;
        }
    }
    
}
