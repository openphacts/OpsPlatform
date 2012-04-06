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
public class HardCodedGraphToNameSpaces implements GraphToNameSpaces{

    private HashMap<String,List<String>> allowedNamespaces;
    
    public HardCodedGraphToNameSpaces(){
        setupHardCodedFilter();
    }
    
    private void setupHardCodedFilter() {
        allowedNamespaces = new HashMap<String,List<String>>();
        ArrayList<String> nameSpaces;
        
        //One from My test to be changed.
        //http://PDSP_DB/Data
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://wiki.openphacts.org/index.php/PDSP_DB#");
        allowedNamespaces.put("http://PDSP_DB/Data", nameSpaces);
        
        //http://rdf.chemspider.com/data
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://rdf.chemspider.com/");
        allowedNamespaces.put("http://rdf.chemspider.com/data", nameSpaces);
        
        //http://chem2bio2rdf.org/data
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/");
        allowedNamespaces.put("http://chem2bio2rdf.org/data", nameSpaces);

        //http://www4.wiwiss.fu-berlin.de/data
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/");
        allowedNamespaces.put("http://www4.wiwiss.fu-berlin.de/data", nameSpaces);
        
        //http://larkc.eu#Fixedcontext
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://www.conceptwiki.org/wiki/concept/");
        allowedNamespaces.put("http://larkc.eu#Fixedcontext", nameSpaces);

        //file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://rdf.chemspider.com/");
        allowedNamespaces.put("file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl", nameSpaces);

        //file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/");
        allowedNamespaces.put("file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt", nameSpaces);

        //http://linkedlifedata.com/resource/drugbank
        nameSpaces = new ArrayList<String>();
        nameSpaces.add("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/");
        allowedNamespaces.put("http://linkedlifedata.com/resource/drugbank", nameSpaces);
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
