/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.ops.plugin.imssparqlexpand.ims.DummyIMSMapper;
import eu.ops.plugin.imssparqlexpand.ims.HardCodedFilterMapper;
import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;

/**
 *
 * @author Christian
 */
public class HardCodedFilterMapperTest {
    
    private static ValueFactory valueFactory;
    
    public HardCodedFilterMapperTest() {
        valueFactory = ValueFactoryImpl.getInstance();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private List<URI> getFullURIList(){
        ArrayList<URI> fullList = new ArrayList<URI>();
         
        fullList.add(valueFactory.createURI("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c"));
        fullList.add(valueFactory.createURI("http://rdf.chemspider.com/3914"));
        fullList.add(valueFactory.createURI("http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523"));
        fullList.add(valueFactory.createURI("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#23597"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36322"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36328"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36332"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36339"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36340"));
        fullList.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36341"));
        return fullList;
    }
    
    private HardCodedFilterMapper getHardCodedFilterMapper(){
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://rdf.chemspider.com/3914");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#23597");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36322");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36328");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36332");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36339");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36340");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");
        return new HardCodedFilterMapper(dummyIMSMapper);      
    }
    
    /**
     * Test of stripoutURIs method, of class HardCodedFilterMapper.
     */
    @Test
    public void testStripoutURIsChemspider() throws QueryExpansionException {
        System.out.println("stripoutURIs");
        List<URI> fullList = getFullURIList();
        String graph = "http://rdf.chemspider.com/data";
        HardCodedFilterMapper instance = getHardCodedFilterMapper();
        List expResult = new ArrayList<URI>();
        expResult.add(valueFactory.createURI("http://rdf.chemspider.com/3914"));
        
        List result = instance.stripoutURIs(fullList, graph);
        assertEquals(expResult, result);
    }

    /**
     * Test of stripoutURIs method, of class HardCodedFilterMapper.
     */
    @Test
    public void testStripoutURIsPDSP_DB() throws QueryExpansionException {
        System.out.println("stripoutURIs");
        List<URI> fullList = getFullURIList();
        String graph = "http://PDSP_DB/Data";
        HardCodedFilterMapper instance = getHardCodedFilterMapper();
        List expResult = new ArrayList<URI>();
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#23597"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36322"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36328"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36332"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36339"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36340"));
        expResult.add(valueFactory.createURI("http://wiki.openphacts.org/index.php/PDSP_DB#36341"));
        
        List result = instance.stripoutURIs(fullList, graph);
        assertEquals(expResult, result);
    }

    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testBigCompoundLookup() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?csid_uri ?ligand_name ?ligand_displaced ?cas ?receptor_name "
                + "?pdsp_species ?pdsp_source ?smiles ?inchi ?inchi_key ?med_chem_friendly ?molweight ?hhd ?alogp "
                + "?mw_freebase ?psa ?molformula ?molregno ?num_ro5_violations ?ro3_pass ?hha ?rtb "
                + "?predictedWaterSolubility ?experimentalWaterSolubility ?molecularWeightAverage ?description "
                + "?halfLife ?state ?predictedLogs ?brandName ?predictedLogpHydrophobicity "
                + "?experimentalLogpHydrophobicity ?drugCategoryLabel ?targetLabel "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num>  ?cas ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_receptor_name>  ?receptor_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#species>  ?pdsp_species ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "  } OPTIONAL {"
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ;"
                + "           <http://rdf.chemspider.com/#inchikey>  ?inchi_key ."
                + "      ?csid_uri <http://rdf.chemspider.com/#inchikey> ?inchi_key ."
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://chem2bio2rdf.org/data> {"
                + "       <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://chem2bio2rdf.org/chembl/resource/med_chem_friendly> ?med_chem_friendly ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molweight> ?molweight ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/hhd> ?hhd ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/alogp> ?alogp ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/mw_freebase> ?mw_freebase ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/psa> ?psa ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molformula> ?molformula ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molregno> ?molregno ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/num_ro5_violations> ?num_ro5_violations ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/ro3_pass> ?ro3_pass;"
                + "           <http://chem2bio2rdf.org/chembl/resource/hha> ?hha ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/rtb> ?rtb;"
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://www4.wiwiss.fu-berlin.de/data> {"
                + "       <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedWaterSolubility> "
                + "                ?predictedWaterSolubility ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/experimentalWaterSolubility> "
                + "                ?experimentalWaterSolubility ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeightAverage> "
                + "                ?molecularWeightAverage ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> ?cat_uri ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?description ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/halfLife> ?halfLife ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/state> ?state ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedLogs> ?predictedLogs ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/brandName> ?brandName ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?target_uri ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedLogpHydrophobicity> "
                + "                ?predictedLogpHydrophobicity ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/experimentalLogpHydrophobicity> "
                + "                ?experimentalLogpHydrophobicity ."
                + "       ?cat_uri <http://www.w3.org/2000/01/rdf-schema#label> ?drugCategoryLabel ."
                + "       ?target_uri <http://www.w3.org/2000/01/rdf-schema#label> ?targetLabel"
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?csid_uri ?ligand_name ?ligand_displaced ?cas ?receptor_name "
                + "?pdsp_species ?pdsp_source ?smiles ?inchi ?inchi_key ?med_chem_friendly ?molweight ?hhd ?alogp "
                + "?mw_freebase ?psa ?molformula ?molregno ?num_ro5_violations ?ro3_pass ?hha ?rtb "
                + "?predictedWaterSolubility ?experimentalWaterSolubility ?molecularWeightAverage ?description "
                + "?halfLife ?state ?predictedLogs ?brandName ?predictedLogpHydrophobicity "
                + "?experimentalLogpHydrophobicity ?drugCategoryLabel ?targetLabel \n"
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/Data> \n{"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num>  ?cas ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_receptor_name>  ?receptor_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#species>  ?pdsp_species ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "      FILTER (?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  } OPTIONAL {"
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      <http://rdf.chemspider.com/3914> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ;"
                + "           <http://rdf.chemspider.com/#inchikey>  ?inchi_key ."
                + "      ?csid_uri <http://rdf.chemspider.com/#inchikey> ?inchi_key ."
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://chem2bio2rdf.org/data> {"
                + "       <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           <http://chem2bio2rdf.org/chembl/resource/med_chem_friendly> ?med_chem_friendly ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molweight> ?molweight ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/hhd> ?hhd ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/alogp> ?alogp ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/mw_freebase> ?mw_freebase ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/psa> ?psa ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molformula> ?molformula ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/molregno> ?molregno ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/num_ro5_violations> ?num_ro5_violations ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/ro3_pass> ?ro3_pass;"
                + "           <http://chem2bio2rdf.org/chembl/resource/hha> ?hha ;"
                + "           <http://chem2bio2rdf.org/chembl/resource/rtb> ?rtb;"
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://www4.wiwiss.fu-berlin.de/data> {"
                + "       <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedWaterSolubility> "
                + "                ?predictedWaterSolubility ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/experimentalWaterSolubility> "
                + "                ?experimentalWaterSolubility ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/molecularWeightAverage> "
                + "                ?molecularWeightAverage ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> ?cat_uri ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?description ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/halfLife> ?halfLife ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/state> ?state ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedLogs> ?predictedLogs ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/brandName> ?brandName ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/target> ?target_uri ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/predictedLogpHydrophobicity> "
                + "                ?predictedLogpHydrophobicity ;"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/experimentalLogpHydrophobicity> "
                + "                ?experimentalLogpHydrophobicity ."
                + "       ?cat_uri <http://www.w3.org/2000/01/rdf-schema#label> ?drugCategoryLabel ."
                + "       ?target_uri <http://www.w3.org/2000/01/rdf-schema#label> ?targetLabel"
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://rdf.chemspider.com/3914");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#23597");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36322");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36328");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36332");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36339");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36340");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return getHardCodedFilterMapper();
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testCompoundInfo() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase "
                + "?num_ro5_violations ?psa ?rtb ?affectedOrganism ?biotransformation ?description ?indication "
                + "?meltingPoint ?proteinBinding ?toxicity "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                                          chemspider:smiles ?smiles ;"
                + "                                          chemspider:inchi ?inchi ; "
                + "                                          chemspider:inchikey ?inchiKey ."
                + "       ?csid_uri chemspider:inchi ?inchi"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:alogp ?alogp }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:hha ?hha }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:hhd ?hhd }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:molformula ?molformula }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:molweight ?molweight }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:mw_freebase ?mw_freebase }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:psa ?psa }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      c2b2r_chembl:rtb ?rtb }"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:affectedOrganism ?affectedOrganism }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:biotransformation ?biotransformation }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:description ?description }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:indication ?indication }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:proteinBinding ?proteinBinding }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:toxicity ?toxicity }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                       drugbank:meltingPoint ?meltingPoint}"
                + "   }"
                + "}";     
        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase "
                + "?num_ro5_violations ?psa ?rtb ?affectedOrganism ?biotransformation ?description ?indication "
                + "?meltingPoint ?proteinBinding ?toxicity "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl> {"
                + "       <http://rdf.chemspider.com/187440> chemspider:smiles ?smiles ;"
                + "                                          chemspider:inchi ?inchi ; "
                + "                                          chemspider:inchikey ?inchiKey ."
                + "       ?csid_uri chemspider:inchi ?inchi"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:alogp ?alogp }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:hha ?hha }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:hhd ?hhd }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:molformula ?molformula }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:molweight ?molweight }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:mw_freebase ?mw_freebase }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:psa ?psa }"
                + "       OPTIONAL { <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                      c2b2r_chembl:rtb ?rtb }"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:affectedOrganism ?affectedOrganism }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:biotransformation ?biotransformation }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:description ?description }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:indication ?indication }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:proteinBinding ?proteinBinding }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:toxicity ?toxicity }"
                + "       OPTIONAL { <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                       drugbank:meltingPoint ?meltingPoint}"
                + "   }"
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://rdf.chemspider.com/187440");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398");

        final HardCodedFilterMapper hardCoded = new HardCodedFilterMapper(dummyIMSMapper);
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return hardCoded;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }    

    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testGraphPatternsInDifferentJoins() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "SELECT ?prefLabel ?affectedOrganism ?biotransformation "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:affectedOrganism ?affectedOrganism }"
                + "       OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:biotransformation ?biotransformation }"
                + "   }"
                + "}";     
        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "SELECT ?prefLabel ?affectedOrganism ?biotransformation "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:affectedOrganism ?affectedOrganism }"
                + "       OPTIONAL {<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:biotransformation ?biotransformation }"
                + "   }"
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://rdf.chemspider.com/187440");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398");

        final HardCodedFilterMapper hardCoded = new HardCodedFilterMapper(dummyIMSMapper);
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return hardCoded;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }    

    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testDoubleOptional() throws MalformedQueryException, QueryExpansionException {
        System.out.println("testDoubleOptional");
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
                + "SELECT ?prefLabel ?affectedOrganism ?biotransformation "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   OPTIONAL {"
                + "       GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "           OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:affectedOrganism ?affectedOrganism }"
                + "           OPTIONAL { <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                      drugbank:biotransformation ?biotransformation }"
                + "       }"
                + "   }"
                + "}";     
        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + "PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> \n"
                + "SELECT ?prefLabel ?affectedOrganism ?biotransformation \n"
                + "WHERE { \n"
                + "   GRAPH <http://larkc.eu#Fixedcontext> { \n"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel \n"
                + "   } \n"
                + "   OPTIONAL { \n"
                + "       GRAPH <http://linkedlifedata.com/resource/drugbank> { \n"
                + "           OPTIONAL { \n"
                + "               <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:affectedOrganism ?affectedOrganism \n"
                + "           } \n"
                + "           OPTIONAL {"
                + "                <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                      drugbank:biotransformation ?biotransformation \n"
                + "           } \n"
                + "       } \n"
                + "   } \n"
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://rdf.chemspider.com/187440");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398");

        final HardCodedFilterMapper hardCoded = new HardCodedFilterMapper(dummyIMSMapper);
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return hardCoded;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }    

    /**
     * Test query found in Section compoundLookup
     */
    @Test   
    public void testCompoundPharmacology() throws MalformedQueryException, QueryExpansionException {
        System.out.println("testCompoundPharmacology");
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973>"
                + "                                          chemspider:smiles ?smiles ;"
                + "                                          chemspider:inchi ?inchi ; "
                + "                                          chemspider:inchikey ?inchiKey ."
                + "       ?csid_uri chemspider:inchi ?inchi"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {"
                + "       OPTIONAL { "
                + "           ?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno "
                + "                             <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> ;"
                + "                         c2b2r_chembl:std_type ?std_type ; "
                + "                         c2b2r_chembl:relation ?relation ; "
                + "                         c2b2r_chembl:std_value ?std_value ;"
                + "                         c2b2r_chembl:std_unites ?std_unites ; "
                + "                         farmbio:onAssay ?assay_uri ."
                + "           ?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; "
                + "                             c2b2r_chembl:assay_organism ?assay_organism;"
                + "                             c2b2r_chembl:tid ?tid . "
                + "                             ?tid c2b2r_chembl:pref_name ?target_pref_name"
                + "       } OPTIONAL { "
                + "           <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                    c2b2r_chembl:molweight ?molweight "
                + "       } OPTIONAL { "
                + "           <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations "
                + "       }"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "       OPTIONAL {"
                + "           <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                   drugbank:drugType ?drugType_uri "
                + "       }"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {"
                + "       OPTIONAL {?drugType_uri rdfs:label ?drugType}"
                + "   }"
                + "}";     

        String expectedQuery1 = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType \n"
                + "WHERE {\n"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {\n"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl> {\n"
                + "       <http://rdf.chemspider.com/187440> chemspider:smiles ?smiles ;\n"
                + "                                          chemspider:inchi ?inchi ; \n"
                + "                                          chemspider:inchikey ?inchiKey .\n"
                + "       ?csid_uri chemspider:inchi ?inchi\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {\n"
                + "       OPTIONAL { \n"
                + "           ?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno "
                + "                             <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> ;\n"
                + "                         c2b2r_chembl:std_type ?std_type ; \n"
                + "                         c2b2r_chembl:relation ?relation ; \n"
                + "                         c2b2r_chembl:std_value ?std_value ;\n"
                + "                         c2b2r_chembl:std_unites ?std_unites ; \n"
                + "                         farmbio:onAssay ?assay_uri .\n"
                + "           ?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; \n"
                + "                             c2b2r_chembl:assay_organism ?assay_organism;\n"
                + "                             c2b2r_chembl:tid ?tid . \n"
                + "                             ?tid c2b2r_chembl:pref_name ?target_pref_name\n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:molweight ?molweight \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations \n"
                + "       }\n"
                + "   }\n"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {\n"
                + "       OPTIONAL {\n"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                   drugbank:drugType ?drugType_uri \n"
                + "       }\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {\n"
                + "       OPTIONAL {?drugType_uri rdfs:label ?drugType}\n"
                + "   }\n"
                + "}";     
        String expectedQuery2 = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType \n"
                + "WHERE {\n"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {\n"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/OPS-DS-TTL/ChEMBL_nonewlines.ttl> {\n"
                + "       <http://rdf.chemspider.com/187440> chemspider:smiles ?smiles ;\n"
                + "                                          chemspider:inchi ?inchi ; \n"
                + "                                          chemspider:inchikey ?inchiKey .\n"
                + "       ?csid_uri chemspider:inchi ?inchi\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {\n"
                + "       OPTIONAL { \n"
                + "           ?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno "
                + "                             <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> ;\n"
                + "                         c2b2r_chembl:std_type ?std_type ; \n"
                + "                         c2b2r_chembl:relation ?relation ; \n"
                + "                         c2b2r_chembl:std_value ?std_value ;\n"
                + "                         c2b2r_chembl:std_unites ?std_unites ; \n"
                + "                         farmbio:onAssay ?assay_uri .\n"
                + "           ?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; \n"
                + "                             c2b2r_chembl:assay_organism ?assay_organism;\n"
                + "                             c2b2r_chembl:tid ?tid . \n"
                + "                             ?tid c2b2r_chembl:pref_name ?target_pref_name\n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:molweight ?molweight \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations \n"
                + "       }\n"
                + "   }\n"
                + "   OPTIONAL {\n"
                + "       GRAPH <http://linkedlifedata.com/resource/drugbank> {\n"
                + "           <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398> "
                + "                   drugbank:drugType ?drugType_uri \n"
                + "       }\n"
                + "   }\n"
                + "   OPTIONAL {"
                + "       GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {\n"
                + "           ?drugType_uri rdfs:label ?drugType}\n"
                + "   }\n"
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://rdf.chemspider.com/187440");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398");

        final HardCodedFilterMapper hardCoded = new HardCodedFilterMapper(dummyIMSMapper);
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return hardCoded;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(query.toString(), expectedQuery1, expectedQuery2, true));
    }    
    
    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testInnerOptionaly() throws MalformedQueryException, QueryExpansionException {
        System.out.println("testInnerOptionaly");
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType "
                + "WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> {"
                + "       OPTIONAL { "
                + "           ?activity_uri farmbio:onAssay ?assay_uri ."
                + "           ?assay2target_uri c2b2r_chembl:tid ?tid . "
                + "       } OPTIONAL { "
                + "           <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                    c2b2r_chembl:molweight ?molweight "
                + "       } OPTIONAL { "
                + "           <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations "
                + "       }"
                + "   }"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {"
                + "       OPTIONAL {?drugType_uri rdfs:label ?drugType}"
                + "   }"
                + "}";     

        String expectedQuery1 = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType \n"
                + "WHERE { \n"
                + "   GRAPH <http://larkc.eu#Fixedcontext> { \n"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   } \n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> { \n"
                + "       OPTIONAL { \n"
                + "           ?activity_uri farmbio:onAssay ?assay_uri .\n"
                + "           ?assay2target_uri c2b2r_chembl:tid ?tid . \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:molweight ?molweight \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations \n"
                + "       }\n"
                + "   }\n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {\n"
                + "       OPTIONAL {?drugType_uri rdfs:label ?drugType}\n"
                + "   }\n"
                + "}";     
        String expectedQuery2 = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + "PREFIX chemspider: <http://rdf.chemspider.com/#> "
                + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + "PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations ?std_type ?relation "
                + "       ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType \n"
                + "WHERE { \n"
                + "   GRAPH <http://larkc.eu#Fixedcontext> { \n"
                + "       <http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973> "
                + "             <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel"
                + "   } \n"
                + "   GRAPH <file:///home/OPS/develop/openphacts/datasets/chem2bio2rdf/chembl.nt> { \n"
                + "       OPTIONAL { \n"
                + "           ?activity_uri farmbio:onAssay ?assay_uri .\n"
                + "           ?assay2target_uri c2b2r_chembl:tid ?tid . \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:molweight ?molweight \n"
                + "       } OPTIONAL { \n"
                + "           <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734> "
                + "                    c2b2r_chembl:num_ro5_violations ?num_ro5_violations \n"
                + "       }\n"
                + "   }\n"
                + "   OPTIONAL {"
                + "       GRAPH <file:///home/OPS/develop/openphacts/datasets/lld/drug_type_labels.ttl> {\n"
                + "           ?drugType_uri rdfs:label ?drugType}\n"
                + "   }\n"
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://rdf.chemspider.com/187440");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/276734");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/wiki/concept/37ac0ee8-9ff7-454f-ac04-2438e4fac973" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00398");

        final HardCodedFilterMapper hardCoded = new HardCodedFilterMapper(dummyIMSMapper);
        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return hardCoded;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(query.toString(), expectedQuery1, expectedQuery2, true));
    }    
}
