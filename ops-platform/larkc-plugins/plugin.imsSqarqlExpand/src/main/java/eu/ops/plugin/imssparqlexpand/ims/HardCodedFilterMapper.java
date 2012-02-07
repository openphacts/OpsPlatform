/*
 * Must be kept up to date with
 * https://wiki.openphacts.org/index.php/Available_Information_-_Compounds
 */
package eu.ops.plugin.imssparqlexpand.ims;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public class HardCodedFilterMapper extends IMSFilterMapper{

    private HashMap<String,List<String>> allowedNamespaces;
    
    public HardCodedFilterMapper(IMSMapper fullMapper){
        super(fullMapper);
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
        
        //Real ones 
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
    public List<URI> stripoutURIs(List<URI> fullList, String graph) throws QueryExpansionException{
        ArrayList<URI> strippedList = new ArrayList<URI>();
        List<String> nameSpaces = allowedNamespaces.get(graph);
        if (nameSpaces == null) {
            throw new QueryExpansionException("unexpected graph " + graph);
        }
        for (URI uri:fullList){
            String nameSpace = uri.getNamespace();
            if (nameSpaces.contains(nameSpace)){
                strippedList.add(uri);
            }
        }
        return strippedList;
    }
    
}
