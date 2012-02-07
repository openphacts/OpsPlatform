package eu.ops.plugin.imssparqlexpand;

import eu.ops.plugin.imssparqlexpand.IMSSPARQLExpand;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import eu.ops.plugin.imssparqlexpand.QueryUtils;
import static org.junit.Assert.*;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.ops.plugin.imssparqlexpand.ims.DummyIMSMapper;
import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test all the queries found in https://wiki.openphacts.org/index.php/Core_API
 * 
 * @author Christian
 * @version Jan 4 2012
 */
public class ByGraphTest {
        
    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);

    public ByGraphTest() {
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
     
    /**
     * Test part of the query found in Section compoundLookup
     */
    @Test
    public void testPartCompoundLookup() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ."
                + "    } "
                + "  } OPTIONAL {"
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ."
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery1 = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi \n"
                + "WHERE {"
                + "  OPTIONAL {\n"
                + "    GRAPH <http://PDSP_DB/Data> {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;\n"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced .\n"
                + "    } "
                + "  } "
                + "  OPTIONAL {\n"
                + "    GRAPH <http://rdf.chemspider.com/data>  {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;\n"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi .\n"
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";    
        String expectedQuery2 = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi \n"
                + "WHERE {"
                + "  GRAPH <http://PDSP_DB/Data> {\n"
                + "    OPTIONAL {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;\n"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced .\n"
                + "    } "
                + "  } "
                + "  GRAPH <http://rdf.chemspider.com/data>  {\n"
                + "    OPTIONAL {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;\n"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi .\n"
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(query.toString(), expectedQuery1, expectedQuery2, true));
    }

    /**
     * Test part of the query found in Section compoundLookup
     */
    @Test
    public void testPartNoOptionalCompoundLookup() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi "
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ."
                + "    } "
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ."
                + "    }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi \n"
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;\n"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced .\n"
                + "    } "
                + "    GRAPH <http://rdf.chemspider.com/data>  {\n"
                + "      <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;\n"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi .\n"
                + "    }"
                + "}"
                + "LIMIT 10";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test part of the query found in Section compoundLookup
     */
    @Test
    public void testPartThreeMapsp() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi "
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ."
                + "    } "
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ."
                + "    }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi \n"
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {\n"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;\n"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced .\n"
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> || "
                + "              ?replacedURI1 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043>) "
                + "    } "
                + "    GRAPH <http://rdf.chemspider.com/data>  {\n"
                + "      ?replacedURI2 "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;\n"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi .\n"
                + "      FILTER (?replacedURI2 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI2 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> || "
                + "              ?replacedURI2 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043>) "
                + "    }"
                + "}"
                + "LIMIT 10";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test part of the query found in Section compoundLookup
     */
    @Test
    public void testSameGraphUsedTwice() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi "
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ."
                + "    } "
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ."
                + "    }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_name ?ligand_displaced ?smiles ?inchi \n"
                + "WHERE {"
                + "    GRAPH <http://PDSP_DB/Data> {\n"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;\n"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced .\n"
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> || "
                + "              ?replacedURI1 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043>) "
                + "    } "
                + "    GRAPH <http://PDSP_DB/Data>  {\n"
                + "      ?replacedURI2 "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;\n"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi .\n"
                + "      FILTER (?replacedURI2 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI2 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> || "
                + "              ?replacedURI2 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043>) "
                + "    }"
                + "}"
                + "LIMIT 10";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
        String expectedQuery1 = "SELECT  DISTINCT ?csid_uri ?ligand_name ?ligand_displaced ?cas ?receptor_name "
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
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI1 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI1 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  } OPTIONAL {"
                + "    GRAPH <http://rdf.chemspider.com/data>  {"
                + "      ?replacedURI2 "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ;"
                + "           <http://rdf.chemspider.com/#inchikey>  ?inchi_key ."
                + "      ?csid_uri <http://rdf.chemspider.com/#inchikey> ?inchi_key ."
                + "      FILTER (?replacedURI2 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI2 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI2 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI2 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://chem2bio2rdf.org/data> {"
                + "       ?replacedURI3 "
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
                + "      FILTER (?replacedURI3 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI3 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI3 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI3 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    }"
                + "  } OPTIONAL {"
                + "    GRAPH <http://www4.wiwiss.fu-berlin.de/data> {"
                + "       ?replacedURI4 "
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
                + "      FILTER (?replacedURI4 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI4 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI4 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI4 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    }"
                + "  }"
                + "}"
                + "LIMIT 10";     
        String expectedQuery2 = "SELECT  DISTINCT ?csid_uri ?ligand_name ?ligand_displaced ?cas ?receptor_name "
                + "?pdsp_species ?pdsp_source ?smiles ?inchi ?inchi_key ?med_chem_friendly ?molweight ?hhd ?alogp "
                + "?mw_freebase ?psa ?molformula ?molregno ?num_ro5_violations ?ro3_pass ?hha ?rtb "
                + "?predictedWaterSolubility ?experimentalWaterSolubility ?molecularWeightAverage ?description "
                + "?halfLife ?state ?predictedLogs ?brandName ?predictedLogpHydrophobicity "
                + "?experimentalLogpHydrophobicity ?drugCategoryLabel ?targetLabel \n"
                + "WHERE {"
                + "  GRAPH <http://PDSP_DB/Data> \n{"
                + "    OPTIONAL {"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced>  ?ligand_displaced ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num>  ?cas ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_receptor_name>  ?receptor_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#species>  ?pdsp_species ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI1 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI1 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  } \n"
                + "  GRAPH <http://rdf.chemspider.com/data>  {"
                + "    OPTIONAL {"
                + "      ?replacedURI2 "
                + "           <http://rdf.chemspider.com/#smiles>  ?smiles;"
                + "           <http://rdf.chemspider.com/#inchi>  ?inchi ;"
                + "           <http://rdf.chemspider.com/#inchikey>  ?inchi_key ."
                + "      ?csid_uri <http://rdf.chemspider.com/#inchikey> ?inchi_key ."
                + "      FILTER (?replacedURI2 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI2 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI2 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI2 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    }"
                + "  } \n"
                + "  GRAPH <http://chem2bio2rdf.org/data> {"
                + "    OPTIONAL {"
                + "       ?replacedURI3 "
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
                + "      FILTER (?replacedURI3 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI3 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI3 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI3 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    }"
                + "  } \n"
                + "  GRAPH <http://www4.wiwiss.fu-berlin.de/data> {"
                + "    OPTIONAL {"
                + "       ?replacedURI4 "
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
                + "       ?target_uri <http://www.w3.org/2000/01/rdf-schema#label> ?targetLabel "
                + "      FILTER (?replacedURI4 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> " 
                + "           || ?replacedURI4 = <http://rdf.chemspider.com/3914> "
                + "           || ?replacedURI4 = <http://chem2bio2rdf.org/chembl/resource/chembl_compounds/52523> "
                + "           || ?replacedURI4 = <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB01043> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#23597> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36322> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36328> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36332> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36339> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36340> "
                + "           || ?replacedURI4 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
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
                return dummyIMSMapper;
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
    public void testCompoundLookupStatementsOptionalOutside() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/DataQ> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/DataQ> \n{"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
    public void testCompoundLookupStatementsOptionalInside() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  GRAPH <http://PDSP_DB/DataQ> {"
                + "    OPTIONAL {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/DataQ> \n{"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
    public void testCompoundLookupInandOutsideOfOPTIONAL() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_displaced ?cas ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  GRAPH <http://PDSP_DB/DataQ> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced> ?ligand_displaced ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas ;"
                + "    OPTIONAL {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_displaced ?cas ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  GRAPH <http://PDSP_DB/DataQ> {"
                + "    ?replacedURI1 "
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced> ?ligand_displaced ;"
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas ;"
                + "    OPTIONAL {"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "    FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "         || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "  }"
                + "}"
                + "LIMIT 10";
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
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
    public void testCompoundLookupOptinalwithPartGraphInside() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT ?ligand_displaced ?cas ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/DataQ> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced> ?ligand_displaced ;"
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas ;"
                + "    } "
                + "    <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?ligand_displaced ?cas ?ligand_name ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/DataQ> {"
                + "      ?replacedURI1 "
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#ligand_displaced> ?ligand_displaced ;"
                + "        <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas ;"
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "    ?replacedURI2 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name>  ?ligand_name ;"
                + "    FILTER (?replacedURI2 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "         || ?replacedURI2 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    ?replacedURI3 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    FILTER (?replacedURI3 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "         || ?replacedURI3 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "  }"
                + "}"
                + "LIMIT 10";
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        System.out.println("CYAB");
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section compoundLookup
     */
    @Test
    public void testSingleInOptional() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT  DISTINCT  ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/Data> {"
                + "      <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";
        String expectedQuery = "SELECT  DISTINCT ?pdsp_source "
                + "WHERE {"
                + "  OPTIONAL {"
                + "    GRAPH <http://PDSP_DB/Data> \n{"
                + "      ?replacedURI1 "
                + "          <http://wiki.openphacts.org/index.php/PDSP_DB#source>  ?pdsp_source ."
                + "      FILTER (?replacedURI1 = <http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c> "
                + "           || ?replacedURI1 = <http://wiki.openphacts.org/index.php/PDSP_DB#36341>)"
                + "    } "
                + "  }"
                + "}"
                + "LIMIT 10";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c");
        dummyIMSMapper.addMapping("http://www.conceptwiki.org/concept/d510239a-6b55-4ca9-8f64-cfc9b8e7c64c" ,
                                  "http://wiki.openphacts.org/index.php/PDSP_DB#36341");

        IMSSPARQLExpand expander = 
                new IMSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IMSSPARQLExpand1")) {
            @Override
            IMSMapper instantiateIMSMapper() {
                return dummyIMSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

}
