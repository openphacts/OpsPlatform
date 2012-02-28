package eu.ops.plugin.imssparqlexpand.ims;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.openrdf.model.URI;

/*
 * Quick fix implmentation for the March Release.
 * 
 * Must be kept up to date with
 * https://wiki.openphacts.org/index.php/Available_Information_-_Compounds
 *
 * @author Christian
 */
public class LarkcHardCodedFilterMapper extends IMSFilterMapper{

    private HashMap<String,Set<String>> allowedNamespaces;
    
    public LarkcHardCodedFilterMapper(IMSMapper fullMapper){
        super(fullMapper);
        setupHardCodedFilter();
    }
    
    private void setupHardCodedFilter() {
        allowedNamespaces = new HashMap<String,Set<String>>();
        //Use a LinkedHashSet to guarantee order for testing
        LinkedHashSet<String> nameSpaces;
        
        //http://larkc.eu#Fixedcontext
        nameSpaces = new LinkedHashSet<String>();
        nameSpaces.add("http://www.conceptwiki.org/concept/");
        String graph = "http://larkc.eu#Fixedcontext";
        allowedNamespaces.put(graph, nameSpaces);
        
       //http://www.chemspider.com
        nameSpaces = new LinkedHashSet<String>();
        nameSpaces.add("http://rdf.chemspider.com/");
        graph = "http://www.chemspider.com";
        allowedNamespaces.put(graph, nameSpaces);
        
        //http://www.chem2bio2rdf.org/ChEMBL
        nameSpaces = new LinkedHashSet<String>();
        nameSpaces.add("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/");
        nameSpaces.add("http://chem2bio2rdf.org/chembl/resource/chembl_targets/");
        graph = "http://www.chem2bio2rdf.org/ChEMBL";
        allowedNamespaces.put(graph, nameSpaces);
        
        //http://linkedlifedata.com/resource/drugbank
        nameSpaces = new LinkedHashSet<String>();
        graph = "http://linkedlifedata.com/resource/drugbank";
        nameSpaces.add("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/");
        nameSpaces.add("http://www4.wiwiss.fu-berlin.de/drugbank/resource/targets/");
        allowedNamespaces.put(graph, nameSpaces);
        
    }

    @Override
    public Set<URI> stripoutURIs(Set<URI> fullList, String graph) throws QueryExpansionException{
        //Implmemted with a LinkedHashSet to keep order for testing.
        LinkedHashSet<URI> strippedSet = new LinkedHashSet<URI>();
        Set<String> nameSpaces = allowedNamespaces.get(graph);
        if (nameSpaces == null) {
            throw new QueryExpansionException("unexpected graph " + graph);
        }
        for (URI uri:fullList){
            String nameSpace = uri.getNamespace();
            if (nameSpaces.contains(nameSpace)){
                strippedSet.add(uri);
            }
        }
        return strippedSet;
    }
    
}
