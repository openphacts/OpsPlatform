package eu.ops.plugin.irssparqlexpand;

import static org.junit.Assert.*;
import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
public class CoreAPIQueryTest {
        
    private static Logger logger = LoggerFactory.getLogger(IRSSPARQLExpand.class);

    public CoreAPIQueryTest() {
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
     * Test query found in Section compoundLookup
     */
    @Test
    public void testCompoundLookup() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX cspr: <http://rdf.chemspider.com/#> "  
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + " PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
                + " PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + " SELECT DISTINCT ?compound_uri ?compound_name "
                + " WHERE { "
                + "   {?compound_uri cspr:synonym ?compound_name} "
                + " UNION "
                + "   {?compound_uri cspr:exturl ?mapping ."
                + "   ?mapping skos:exactMatch ?chebi ."
                + "   ?c2b2r_ChEMBL chembl:chebi ?chebi ; chembl:cid ?cid ."
                + "   ?drugbank_uri db:pubchemCompoundURL ?cid ; db:brandName ?compound_name} "
                + " FILTER regex(?compound_name, \"substring_value\", \"i\") } ";
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section proteinLookup 
     */
    @Test
    public void testProteinLookup () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " SELECT DISTINCT ?target_uri ?target_name WHERE {"
                + "   { ?protein_uri brenda:recommended_name ?protein_name }"
                + "   UNION { ?protein_uri pdsp:has_receptor_name ?protein_name }"
                + "   FILTER regex(?target_name, \"substring_value\", \"i\") }";
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    static String CompoundInfoQuery = " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + " PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?csid_uri ?inchi ?inchi_key ?smiles ?synonym ?med_chem_friendly "
                + "                 ?molweight ?chembl_synonyms ?hhd ?alogp ?mw_freebase ?psa "
                + "                 ?molformula ?molregno ?ro5_violations ?ro3_pass ?hha ?rtb "
                + "                 ?pred_water_sol ?exp_water_sol ?target ?weight_average ?category ?description "
                + "                 ?generic_name ?half_life ?state ?pred_logs ?brand_name ?pred_hydrophob "
                + "                 ?exp_hydrophob ?pdsp_name ?pdsp_displaced ?cas ?pdsp_target ?pdsp_species "
                + "                 ?pdsp_source ?r1 ?r2 ?r3 ?r4 ?r5 ?r6 ?r7 ?r8 ?r9 ?r10 ?r11 "
                + "                 ?r12 ?r13 ?r14 ?r15 ?r16 ?r17 ?r18 ?r19 ?r20 ?r21 ?r22 ?r23 ?r24 ?r25 ?r26 "
                + " WHERE {<http://www.example.com/werf> cspr:smiles ?smiles; "
                + "   cspr:synonym ?synonym ; cspr:inchi ?inchi ; "
                + "   cspr:inchikey ?inchi_key ; cspr:exturl ?mapping . "
                + "   ?csid_uri cspr:exturl ?mapping . "
                + "     { ?mapping skos:exactMatch ?chebi . "
                + "       ?c2b2r_ChEMBL chembl:chebi ?chebi ; chembl:med_chem_friendly ?med_chem_friendly; chembl:cid ?cid ; "
                + "       chembl:molweight ?molweight;  chembl:synonyms ?chembl_synonyms ; chembl:hhd ?hhd ; "
                + "       chembl:alogp ?alogp ; chembl:mw_freebase ?mw_freebase ;  chembl:psa ?psa ; "
                + "       chembl:molformula ?molformula ;  chembl:molregno ?molregno ; chembl:num_ro5_violations ?ro5_violations ; "
                + "       chembl:ro3_pass ?ro3_pass ; chembl:hha ?hha ; chembl:rtb ?rtb . "
                + "         OPTIONAL { ?drugbank_uri db:pubchemCompoundURL ?cid . "
                + "           ?drugbank_uri db:predictedWaterSolubility ?pred_water_sol ; " 
                + "           db:experimentalWaterSolubility ?exp_water_sol ;  db:molecularWeightAverage ?weight_average ; "
                + "           db:drugCategory ?category_uri ; db:description ?description ; db:genericName ?generic_name ; "
                + "           db:halfLife ?half_life ;  db:state ?state ; db:predictedLogs ?pred_logs ; "
                + "           db:brandName ?brand_name ; db:target ?target_uri . ?target_uri rdfs:label ?target . "
                + "           ?category_uri rdfs:label ?category} . "
                + "             OPTIONAL {?drugbank_uri db:predictedLogpHydrophobicity ?pred_hydrophob } . "
                + "             OPTIONAL {?drugbank_uri db:experimentalLogpHydrophobicity ?exp_hydrophob } "
                + "         } "
                + "     UNION { ?mapping pdsp:has_test_ligand_id ?pdsp_name ; pdsp:ligand_displaced ?pdsp_displaced; pdsp:has_cas_num ?cas ; "
                + "       pdsp:has_receptor_name ?pdsp_target ; pdsp:species ?pdsp_species ; pdsp:source ?pdsp_source ; "
                + "       ?r1 ?r2 ; ?r3 ?r4 ; ?r5 ?r6 ; ?r7 ?r8; ?r9 ?r10; ?r11 ?r12; ?r13 ?r14; ?r15 ?r16; ?r17 ?r18; "
                + "       ?r19 ?r20; ?r21 ?r22; ?r23 ?r24; ?r25 ?r26;} "
                + "}";
    /**
     * Test query found in Section compoundInfo  
     */
    @Test
    public void testCompoundInfoUnMapped  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQuery;
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    static String CompoundInfoQueryMapped = " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + " PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?csid_uri ?inchi ?inchi_key ?smiles ?synonym ?med_chem_friendly "
                + "                 ?molweight ?chembl_synonyms ?hhd ?alogp ?mw_freebase ?psa "
                + "                 ?molformula ?molregno ?ro5_violations ?ro3_pass ?hha ?rtb "
                + "                 ?pred_water_sol ?exp_water_sol ?target ?weight_average ?category ?description "
                + "                 ?generic_name ?half_life ?state ?pred_logs ?brand_name ?pred_hydrophob "
                + "                 ?exp_hydrophob ?pdsp_name ?pdsp_displaced ?cas ?pdsp_target ?pdsp_species "
                + "                 ?pdsp_source ?r1 ?r2 ?r3 ?r4 ?r5 ?r6 ?r7 ?r8 ?r9 ?r10 ?r11 "
                + "                 ?r12 ?r13 ?r14 ?r15 ?r16 ?r17 ?r18 ?r19 ?r20 ?r21 ?r22 ?r23 ?r24 ?r25 ?r26 "
                + " WHERE {"
//              + "<http://www.example.com/werf> cspr:smiles ?smiles; "
                + "    ?subjectUri1 cspr:smiles ?smiles . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
//                + "   cspr:synonym ?synonym ; "
                + "    ?subjectUri2 cspr:synonym ?synonym . "
                + "        FILTER (?subjectUri2 = <http://www.example.com/werf> || "
                + "                ?subjectUri2 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri2 = <http://foo.com/45273>) "
//                + "cspr:inchi ?inchi ; "
                + "    ?subjectUri3 cspr:inchi ?inchi . "
                + "        FILTER (?subjectUri3 = <http://www.example.com/werf> || "
                + "                ?subjectUri3 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri3 = <http://foo.com/45273>) "
//               + "   cspr:inchikey ?inchi_key ; "
                + "    ?subjectUri4 cspr:inchikey ?inchi_key . "
                + "        FILTER (?subjectUri4 = <http://www.example.com/werf> || "
                + "                ?subjectUri4 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri4 = <http://foo.com/45273>) "
//                + "   cspr:exturl ?mapping . "
                + "    ?subjectUri5 cspr:exturl ?mapping . "
                + "        FILTER (?subjectUri5 = <http://www.example.com/werf> || "
                + "                ?subjectUri5 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri5 = <http://foo.com/45273>) "
                + "   ?csid_uri cspr:exturl ?mapping . "
                + "     { ?mapping skos:exactMatch ?chebi . "
                + "       ?c2b2r_ChEMBL chembl:chebi ?chebi ; chembl:med_chem_friendly ?med_chem_friendly; chembl:cid ?cid ; "
                + "       chembl:molweight ?molweight;  chembl:synonyms ?chembl_synonyms ; chembl:hhd ?hhd ; "
                + "       chembl:alogp ?alogp ; chembl:mw_freebase ?mw_freebase ;  chembl:psa ?psa ; "
                + "       chembl:molformula ?molformula ;  chembl:molregno ?molregno ; chembl:num_ro5_violations ?ro5_violations ; "
                + "       chembl:ro3_pass ?ro3_pass ; chembl:hha ?hha ; chembl:rtb ?rtb . "
                + "         OPTIONAL { ?drugbank_uri db:pubchemCompoundURL ?cid . "
                + "           ?drugbank_uri db:predictedWaterSolubility ?pred_water_sol ; " 
                + "           db:experimentalWaterSolubility ?exp_water_sol ;  db:molecularWeightAverage ?weight_average ; "
                + "           db:drugCategory ?category_uri ; db:description ?description ; db:genericName ?generic_name ; "
                + "           db:halfLife ?half_life ;  db:state ?state ; db:predictedLogs ?pred_logs ; "
                + "           db:brandName ?brand_name ; db:target ?target_uri . ?target_uri rdfs:label ?target . "
                + "           ?category_uri rdfs:label ?category} . "
                + "             OPTIONAL {?drugbank_uri db:predictedLogpHydrophobicity ?pred_hydrophob } . "
                + "             OPTIONAL {?drugbank_uri db:experimentalLogpHydrophobicity ?exp_hydrophob } "
                + "         } "
                + "     UNION { ?mapping pdsp:has_test_ligand_id ?pdsp_name ; pdsp:ligand_displaced ?pdsp_displaced; pdsp:has_cas_num ?cas ; "
                + "       pdsp:has_receptor_name ?pdsp_target ; pdsp:species ?pdsp_species ; pdsp:source ?pdsp_source ; "
                + "       ?r1 ?r2 ; ?r3 ?r4 ; ?r5 ?r6 ; ?r7 ?r8; ?r9 ?r10; ?r11 ?r12; ?r13 ?r14; ?r15 ?r16; ?r17 ?r18; "
                + "       ?r19 ?r20; ?r21 ?r22; ?r23 ?r24; ?r25 ?r26;} "
                + "}";
    
    /**
     * Test query found in Section compoundInfo  
     */
    @Test
    public void testCompoundInfoMapped  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQuery;
        String expectedQuery = CompoundInfoQueryMapped;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section proteinInfo
     */
    @Test
    public void testProteinInfoPDSP () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = " PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?unigene_id ?nsc ?pubmed_id ?species ?systematic_name ?uniprot_id WHERE { "
                + "    {<http://www.example.com/werf> brenda:recommended_name ?protein_name . "
                + "    ?target_url brenda:recommended_name ?protein_name ; "
                + "    brenda:systematic_name ?systematic_name ; "
                + "    brenda:species ?species_uri ; "
                + "    brenda:has_ec_number ?uniprot_id . "
                + "    ?species_uri rdfs:label ?species } "
                + "    UNION {<http://www.example.com/werf> pdsp:has_receptor_name ?protein_name  . "
                + "    ?target_url pdsp:has_receptor_name ?protein_name "
                + "        OPTIONAL {?target_url pdsp:has_unigene_id ?unigene_id ; pdsp:has_nsc_number ?nsc} "
                + "        OPTIONAL {?target_url pdsp:pubmed_id ?pubmed_id } "
                + "        OPTIONAL {?target_url pdsp:species ?species} "
                + "    } "
                + " }";

        String expectedQuery = " PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?unigene_id ?nsc ?pubmed_id ?species ?systematic_name ?uniprot_id WHERE { "
                + "    {"
                //+ "    <http://www.example.com/werf> brenda:recommended_name ?protein_name . "
                + "    ?subjectUri1 brenda:recommended_name ?protein_name . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "    ?target_url brenda:recommended_name ?protein_name ; "
                + "    brenda:systematic_name ?systematic_name ; "
                + "    brenda:species ?species_uri ; "
                + "    brenda:has_ec_number ?uniprot_id . "
                + "    ?species_uri rdfs:label ?species } "
                + "  UNION {"
                //+ "    <http://www.example.com/werf> pdsp:has_receptor_name ?protein_name  . "
                + "    ?subjectUri7 pdsp:has_receptor_name ?protein_name . "
                + "        FILTER (?subjectUri7 = <http://www.example.com/werf> || "
                + "                ?subjectUri7 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri7 = <http://foo.com/45273>) "
                + "    ?target_url pdsp:has_receptor_name ?protein_name "
                + "        OPTIONAL {?target_url pdsp:has_unigene_id ?unigene_id ; pdsp:has_nsc_number ?nsc} "
                + "        OPTIONAL {?target_url pdsp:pubmed_id ?pubmed_id } "
                + "        OPTIONAL {?target_url pdsp:species ?species} "
                + "    } "
                + " }";
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section compoundPharmacology
     */
    @Test
    public void testCompoundPharmacology () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " SELECT ?compound_name ?species ?ic50 ?target_name ?pubmed_id ?ki_value ?ki_unit "
                + " WHERE { "
                + " 	{<http://www.example.com/werf> brenda:has_inhibitor ?compound_name ; "
                + " 	brenda:has_ic50_value_of ?ic50 ; "
                + " 	brenda:species ?species_uri . "
                + " 	?species_uri rdfs:label ?species } "
                + "UNION {"
                + "     <http://www.example.com/werf> pdsp:has_test_ligand_name ?compound_name ; "
                + " 	pdsp:has_receptor_name ?target_name; "
                + " 	pdsp:species ?species ; "
                + " 	pdsp:pubmed_id ?pubmed_id ; " 
                + " 	pdsp:has_ki_value ?ki_entry . " 
                + " 	?ki_entry rdf:value ?ki_value ; " 
                + " 	pdsp:unit ?ki_unit}"
                + "}"; 
        String expectedQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " SELECT ?compound_name ?species ?ic50 ?target_name ?pubmed_id ?ki_value ?ki_unit "
                + "WHERE { "
                //+ " 	{<http://www.example.com/werf> brenda:has_inhibitor ?compound_name ; "
                + "    {"
                + "    ?subjectUri1 brenda:has_inhibitor ?compound_name . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
               //+ " 	brenda:has_ic50_value_of ?ic50 ; "
                + "    ?subjectUri2 brenda:has_ic50_value_of ?ic50 . "
                + "        FILTER (?subjectUri2 = <http://www.example.com/werf> || "
                + "                ?subjectUri2 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri2 = <http://foo.com/45273>) "
                //+ " 	brenda:species ?species_uri . "
                + "    ?subjectUri3 brenda:species ?species_uri . "
                + "        FILTER (?subjectUri3 = <http://www.example.com/werf> || "
                + "                ?subjectUri3 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri3 = <http://foo.com/45273>) "
                + " 	?species_uri rdfs:label ?species } "
                + " 	UNION {"
                //+ "     <http://www.example.com/werf> pdsp:has_test_ligand_name ?compound_name ; "
                + "    ?subjectUri5 pdsp:has_test_ligand_name ?compound_name . "
                + "        FILTER (?subjectUri5 = <http://www.example.com/werf> || "
                + "                ?subjectUri5 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri5 = <http://foo.com/45273>) "
                //+ " 	pdsp:has_receptor_name ?target_name; "
                + "    ?subjectUri6 pdsp:has_receptor_name ?target_name . "
                + "        FILTER (?subjectUri6 = <http://www.example.com/werf> || "
                + "                ?subjectUri6 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri6 = <http://foo.com/45273>) "
                //+ " 	pdsp:species ?species ; "
                + "    ?subjectUri7 pdsp:species ?species . "
                + "        FILTER (?subjectUri7 = <http://www.example.com/werf> || "
                + "                ?subjectUri7 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri7 = <http://foo.com/45273>) "
                //+ " 	pdsp:pubmed_id ?pubmed_id ; " 
                + "    ?subjectUri8 pdsp:pubmed_id ?pubmed_id . "
                + "        FILTER (?subjectUri8 = <http://www.example.com/werf> || "
                + "                ?subjectUri8 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri8 = <http://foo.com/45273>) "
                //+ " 	pdsp:has_ki_value ?ki_entry . " 
                + "    ?subjectUri9 pdsp:has_ki_value ?ki_entry . "
                + "        FILTER (?subjectUri9 = <http://www.example.com/werf> || "
                + "                ?subjectUri9 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri9 = <http://foo.com/45273>) "
                + " 	?ki_entry rdf:value ?ki_value ; " 
                + " 	pdsp:unit ?ki_unit}"
                + "}"; 
        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    static String PharmacologyQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?uniprot_entry ?species ?ic50 ?inhibitor ?pubmed_id ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	{<http://www.example.com/werf> brenda:recommended_name ?protein_name ;"
                + " 	brenda:has_ec_number ?uniprot_entry ;"
                + " 	brenda:species ?species_code ."
                + " 	?species_code rdfs:label ?species "
                + " 		OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 } "
                + " 	} "
                + " 	UNION {<http://www.example.com/werf> pdsp:has_receptor_name ?protein_name ;"
                + " 	pdsp:species ?species ;"
                + " 	pdsp:has_test_ligand_name ?inhibitor ;"
                + " 		OPTIONAL {?target_uri pdsp:pubmed_id ?pubmed_id ;"
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry rdf:value ?ki_value ;"
                + " 		pdsp:unit ?ki_unit}"
                + " 	}"
                + " }";
    /**
     * Test query found in Section proteinPharmacology
     */
    @Test
    public void testProteinPharmacology () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = PharmacologyQuery;
        String expectedQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?uniprot_entry ?species ?ic50 ?inhibitor ?pubmed_id ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	{"
                //+ "     <http://www.example.com/werf> brenda:recommended_name ?protein_name ;"
                + "    ?subjectUri1 brenda:recommended_name ?protein_name . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                //+ " 	    brenda:has_ec_number ?uniprot_entry ;"
                + "    ?subjectUri2 brenda:has_ec_number ?uniprot_entry . "
                + "        FILTER (?subjectUri2 = <http://www.example.com/werf> || "
                + "                ?subjectUri2 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri2 = <http://foo.com/45273>) "
                //+ " 	    brenda:species ?species_code ."
                + "    ?subjectUri3 brenda:species ?species_code . "
                + "        FILTER (?subjectUri3 = <http://www.example.com/werf> || "
                + "                ?subjectUri3 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri3 = <http://foo.com/45273>) "
                + " 	?species_code rdfs:label ?species "
                + " 		OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 } "
                + " 	} "
                + "   UNION {"
                //+ "     <http://www.example.com/werf> pdsp:has_receptor_name ?protein_name ;"
                + "    ?subjectUri7 pdsp:has_receptor_name ?protein_name . "
                + "        FILTER (?subjectUri7 = <http://www.example.com/werf> || "
                + "                ?subjectUri7 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri7 = <http://foo.com/45273>) "
                //+ " 	    pdsp:species ?species ;"
                + "    ?subjectUri8 pdsp:species ?species . "
                + "        FILTER (?subjectUri8 = <http://www.example.com/werf> || "
                + "                ?subjectUri8 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri8 = <http://foo.com/45273>) "
                //+ " 	    pdsp:has_test_ligand_name ?inhibitor ;"
                + "    ?subjectUri9 pdsp:has_test_ligand_name ?inhibitor . "
                + "        FILTER (?subjectUri9 = <http://www.example.com/werf> || "
                + "                ?subjectUri9 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri9 = <http://foo.com/45273>) "
                + " 		OPTIONAL {?target_uri pdsp:pubmed_id ?pubmed_id ;"
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry rdf:value ?ki_value ;"
                + " 		pdsp:unit ?ki_unit}"
                + " 	}"
                + " }";

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
     
    /**
     * Test query found in Section proteinPharmacology
     * Witrh Single replace
     */
    @Test
    public void testProteinPharmacologySingleReplace () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = PharmacologyQuery;
        String expectedQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?uniprot_entry ?species ?ic50 ?inhibitor ?pubmed_id ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	{"
                + "     <http://bar.ac.uk/19278> brenda:recommended_name ?protein_name ;"
                + " 	    brenda:has_ec_number ?uniprot_entry ;"
                + " 	    brenda:species ?species_code ."
                + " 	?species_code rdfs:label ?species "
                + " 		OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 } "
                + " 	} "
                + "   UNION {"
                + "     <http://bar.ac.uk/19278> pdsp:has_receptor_name ?protein_name ;"
                + " 	    pdsp:species ?species ;"
                + " 	    pdsp:has_test_ligand_name ?inhibitor ;"
                + " 		OPTIONAL {?target_uri pdsp:pubmed_id ?pubmed_id ;"
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry rdf:value ?ki_value ;"
                + " 		pdsp:unit ?ki_unit}"
                + " 	}"
                + " }";

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }
     
    /**
     * Test query found in Section chemicalSimilaritySearch 
     */
    @Test
    public void testChemicalSimilaritySearch  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> "
                + " SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name "
                + " ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	?csid_uri ext:has_similar \"smiles_value\" "
                + " 		OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ; "
                + " 		cspr:inchikey ?compound_inchi_key; "
                + " 		cspr:synonym ?compound_name ; "
                + " 		cspr:smiles ?compound_smiles }"
                + " 		OPTIONAL{?csid_uri cspr:exturl ?exturl . "
                + " 		?exturl pdsp:has_receptor_name ?receptor_name ; "
                + " 		pdsp:has_test_ligand_id ?test_ligand_id ; "
                + " 		pdsp:has_test_ligand_name ?test_ligand_name;"
                + " 		pdsp:has_cas_num ?cas_no ; "
                + " 		pdsp:has_unigene_id ?unigene_id ; "
                + " 		pdsp:ligand_displaced ?ligand_displaced ; "
                + " 		pdsp:species ?species ; "
                + " 		pdsp:source ?source ; "
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry pdsp:unit ?ki_unit ;" 
                + " 		rdf:value ?ki_value}"
                + " }";

        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section chemicalSubstructureSearch
     */
    @Test
    public void testChemicalSubstructureSearch  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>"
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + " PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>"
                + " SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name "
                + " ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	?csid_uri ext:has_substructure_match \"smiles_value\""
                + " 		OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ; "
                + " 		cspr:inchikey ?compound_inchi_key; "
                + " 		cspr:synonym ?compound_name ; "
                + " 		cspr:smiles ?compound_smiles }"
                + " 		OPTIONAL{?csid_uri cspr:exturl ?exturl . "
                + " 		?exturl pdsp:has_receptor_name ?receptor_name ; "
                + " 		pdsp:has_test_ligand_id ?test_ligand_id ; "
                + " 		pdsp:has_test_ligand_name ?test_ligand_name;"
                + " 		pdsp:has_cas_num ?cas_no ; "
                + " 		pdsp:has_unigene_id ?unigene_id ; "
                + " 		pdsp:ligand_displaced ?ligand_displaced ; "
                + " 		pdsp:species ?species ; "
                + " 		pdsp:source ?source ; "
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry pdsp:unit ?ki_unit ; "
                + " 		rdf:value ?ki_value}"
                + " }";
//
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

    /**
     * Test query found in Section chemicalExactStructureSearch
     */
    @Test
    public void testChemicalExactStructureSearch () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>"
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + " PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>"
                + " SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name "
                + " ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	?csid_uri ext:has_exact_structure_match \"smiles_value\" "
                + " 		OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ; "
                + " 		cspr:inchikey ?compound_inchi_key; "
                + " 		cspr:synonym ?compound_name ; "
                + " 		cspr:smiles ?compound_smiles }"
                + " 		OPTIONAL{?csid_uri cspr:exturl ?exturl ." 
                + " 		?exturl pdsp:has_receptor_name ?receptor_name ;" 
                + " 		pdsp:has_test_ligand_id ?test_ligand_id ; "
                + " 		pdsp:has_test_ligand_name ?test_ligand_name;"
                + " 		pdsp:has_cas_num ?cas_no ; "
                + " 		pdsp:has_unigene_id ?unigene_id ;" 
                + " 		pdsp:ligand_displaced ?ligand_displaced ;" 
                + " 		pdsp:species ?species ; "
                + " 		pdsp:source ?source ; "
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry pdsp:unit ?ki_unit ; "
                + " 		rdf:value ?ki_value}"
                + "}";
//
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section subclasses
     */
    @Test
    public void testSubclasses () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?s WHERE { ?s rdfs:subClassOf <http://www.example.com/werf> }";

        String expectedQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?s WHERE { "
                + "    ?s rdfs:subClassOf ?objectUri1 . "
                + "        FILTER (?objectUri1 = <http://www.example.com/werf> || "
                + "                ?objectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?objectUri1 = <http://foo.com/45273>) "
               + "}";    

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section superclasses
     */
    @Test
    public void testSuperclasses () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
             +" SELECT DISTINCT ?o WHERE { <http://www.example.com/werf> rdfs:subClassOf ?o }";
        String expectedQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                +" SELECT DISTINCT ?o WHERE { "
                + "    ?subjectUri1 rdfs:subClassOf ?o  . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section triplesWithSubject 
     */
    @Test
    public void testTriplesWithSubject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p ?o WHERE {<http://www.example.com/werf> ?p ?o}";
        String expectedQuery = "SELECT DISTINCT ?p ?o WHERE {"
                + "    ?subjectUri1  ?p ?o . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section triplesWithPredicate
     */
    @Test
    public void testTriplesWithPredicate () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s ?o WHERE {?s <http://www.example.com/werf> ?o}";

        String expectedQuery = "SELECT DISTINCT ?s ?o WHERE {?s <http://www.example.com/werf> ?o}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section predicatesForSubject 
     */
    @Test
    public void testPredicatesForSubject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {<http://www.example.com/werf> ?p ?o}";
        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?subjectUri1  ?p ?o . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "}";     

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section subjectsWithPredicate 
     */
    @Test
    public void testSubjectsWithPredicate  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";     

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section predicatesWithObject 
     */
    @Test
    public void testPredicatesWithObject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {?s  ?p <http://www.example.com/werf>}";
        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?s  ?p ?objectUri1 . "
                + "        FILTER (?objectUri1 = <http://www.example.com/werf> || "
                + "                ?objectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?objectUri1 = <http://foo.com/45273>) "
                + "}";    

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section objectsOfPredicate
     */
    @Test
    public void testObjectsOfPredicate () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";    

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section subjects 
     */
    @Test
    public void testSubjects  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/bark> <http://www.example.com/werf>}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {"
                + "    ?s <http://www.example.com/bark> ?objectUri1 . "
                + "        FILTER (?objectUri1 = <http://www.example.com/werf> || "
                + "                ?objectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?objectUri1 = <http://foo.com/45273>) "
                + "}";;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section predicates 
     */
    @Test
    public void testPredicates  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {<http://www.example.com/werf>?p <http://www.example.com/bark>}";

        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?subjectUri1 ?p ?objectUri1 . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "        FILTER (?objectUri1 = <http://www.example.com/bark> || "
                + "                ?objectUri1 = <http://bar.ac.uk/453245355435>) "
                + "}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");
        dummyIRSMapper.addMapping("http://www.example.com/bark","http://www.example.com/bark");
        dummyIRSMapper.addMapping("http://www.example.com/bark","http://bar.ac.uk/453245355435");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section objects 
     */
    @Test
    public void testObjects () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?o WHERE {<http://www.example.com/werf> <http://www.example.com/bark> ?o}";

        String expectedQuery = "SELECT DISTINCT ?o WHERE {"
                + "    ?subjectUri1  <http://www.example.com/bark> ?o . "
                + "        FILTER (?subjectUri1 = <http://www.example.com/werf> || "
                + "                ?subjectUri1 = <http://bar.ac.uk/19278> || "
                + "                ?subjectUri1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIRSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

     /**
     * Test query found in Section proteinInfo
     * /
    @Test
    public void test () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "";
//                + "
        String expectedQuery = inputQuery;      

        final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();

        IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
        };
        expander.initialiseInternal(null);
        SetOfStatements eQuery = expander.invokeInternalWithExceptions(
                new SPARQLQueryImpl(inputQuery).toRDF());
        SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(eQuery);
        assertTrue(QueryUtils.sameTupleExpr(expectedQuery, query.toString()));
    }

 /**/
    
}
