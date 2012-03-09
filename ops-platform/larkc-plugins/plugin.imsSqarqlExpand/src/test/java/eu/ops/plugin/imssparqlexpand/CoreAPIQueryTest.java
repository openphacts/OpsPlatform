package eu.ops.plugin.imssparqlexpand;

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
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test all the queries found in https://wiki.openphacts.org/index.php/Core_API
 * Need to add https://wiki.openphacts.org/index.php/Research/Drug_Discovery_Questions
 * D:\OpenPhacts\OPSPlatform_git\ops-platform\larkc-endpoints\endpoint.opsapi\src\main\java\eu\larkc\endpoint\opsapi\OPSAPIEndpointResource.java
 * 
 * @author Christian
 * @version Jan 4 2012
 */
public class CoreAPIQueryTest {
        
    private static Logger logger = LoggerFactory.getLogger(IMSSPARQLExpand.class);

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
     * Older Test query found in Section compoundLookup
     */
    @Test
    public void testCompoundLookupV1() throws MalformedQueryException, QueryExpansionException {
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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
    public void testCompoundLookup() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>"
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "PREFIX cw: <http://www.conceptwiki.org/wiki/concept/>"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT DISTINCT ?concept_label ?concept_url ?concept_uuid ?concept_alt_label ?tag_uuid"
                + "WHERE { "
                + "  GRAPH <http://larkc.eu#Fixedcontext> {"
                + "    ?concept_url ext:conceptwiki_search_by_tag \"substring_value\";"
                + "    ext:semantic_type <http://www.conceptwiki.org/concept/87c08f16-3710-47fc-8ed7-33a1e426891d> ;"
                + "    skos:prefLabel ?concept_label ; dc:identifier ?concept_uuid ; "
                + "    skos:altLabel ?concept_alt_label ; cw:tag ?tag_uuid"
                + "  }"
                + "}";
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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
     * Older Test query found in Section proteinLookup 
     */
    @Test
    public void testProteinLookupV1 () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX cspr: <http://rdf.chemspider.com/#> "
                + " SELECT DISTINCT ?target_uri ?target_name WHERE {"
                + "   { ?protein_uri brenda:recommended_name ?protein_name }"
                + "   UNION { ?protein_uri pdsp:has_receptor_name ?protein_name }"
                + "   FILTER regex(?target_name, \"substring_value\", \"i\") }";
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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
     * Test query found in Section proteinLookup 
     */
    @Test
    public void testProteinLookup () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>"
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                + "PREFIX cw: <http://www.conceptwiki.org/wiki/concept/>"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                + "SELECT DISTINCT ?concept_label ?concept_url ?concept_uuid ?concept_alt_label ?tag_uuid"
                + "WHERE { "
                + "  GRAPH <http://larkc.eu#Fixedcontext> {"
                + "    ?concept_url ext:conceptwiki_search_by_tag \"substring_value\";"
                + "    ext:semantic_type <http://www.conceptwiki.org/concept/eeaec894-d856-4106-9fa1-662b1dc6c6f1> ;"
                + "    skos:prefLabel ?concept_label ; dc:identifier ?concept_uuid ; "
                + "    skos:altLabel ?concept_alt_label ; cw:tag ?tag_uuid"
                + "  }"
                + "}";
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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

    static String CompoundInfoQueryV1 = " PREFIX cspr: <http://rdf.chemspider.com/#> "
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
    public void testCompoundInfoUnMappedV1  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQueryV1;
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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

    static String CompoundInfoQueryMappedV1 = " PREFIX cspr: <http://rdf.chemspider.com/#> "
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
                + "    ?replacedURI1 cspr:smiles ?smiles . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
//                + "   cspr:synonym ?synonym ; "
                + "    ?replacedURI2 cspr:synonym ?synonym . "
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
//                + "cspr:inchi ?inchi ; "
                + "    ?replacedURI3 cspr:inchi ?inchi . "
                + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
//               + "   cspr:inchikey ?inchi_key ; "
                + "    ?replacedURI4 cspr:inchikey ?inchi_key . "
                + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || "
                + "                ?replacedURI4 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI4 = <http://foo.com/45273>) "
//                + "   cspr:exturl ?mapping . "
                + "    ?replacedURI5 cspr:exturl ?mapping . "
                + "        FILTER (?replacedURI5 = <http://www.example.com/werf> || "
                + "                ?replacedURI5 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI5 = <http://foo.com/45273>) "
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
    public void testCompoundInfoMappedV1  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQueryV1;
        String expectedQuery = CompoundInfoQueryMappedV1;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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

    static String CompoundInfoQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
            + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
            + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
            + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
            + " SELECT ?prefLabel ?csid_uri ?smiles ?inchi ?inchiKey"
            + " ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase ?num_ro5_violations ?psa ?rtb"
            + " ?affectedOrganism ?biotransformation ?description ?indication ?meltingPoint ?proteinBinding ?toxicity"
            + " WHERE {"
            + "    GRAPH <http://larkc.eu#Fixedcontext> {"
            + "        <http://www.example.com/werf> skos:prefLabel ?prefLabel "
            + "    }"
            + "    GRAPH <http://www.chemspider.com> {"
            + "        <http://www.example.com/werf> chemspider:smiles ?smiles ;"
            + "        chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey ."
            + "        ?csid_uri chemspider:inchi ?inchi"
            + "    }"
            + "    GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:alogp ?alogp }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:hha ?hha }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:hhd ?hhd }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:molformula ?molformula }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:molweight ?molweight }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:mw_freebase ?mw_freebase }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:psa ?psa }"
            + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:rtb ?rtb }"
            + "  }"
            + "    GRAPH <http://linkedlifedata.com/resource/drugbank> {"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:affectedOrganism ?affectedOrganism }"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:biotransformation ?biotransformation }  "
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:description ?description }"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:indication ?indication }"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:proteinBinding ?proteinBinding }"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:toxicity ?toxicity }"
            + "        OPTIONAL {<http://www.example.com/werf> drugbank:meltingPoint ?meltingPoint}"
            + "    }"
            + " }";
    
    /**
     * Test query found in Section compoundInfo  
     */
    @Test
    public void testCompoundInfoUnMapped() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQuery;
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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

    static String CompoundInfoQueryMapped = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> \n"
            + " PREFIX chemspider: <http://rdf.chemspider.com/#>\n"
            + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>\n"
            + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
            + " SELECT ?prefLabel ?csid_uri ?smiles ?inchi ?inchiKey\n"
            + " ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase ?num_ro5_violations ?psa ?rtb\n"
            + " ?affectedOrganism ?biotransformation ?description ?indication ?meltingPoint ?proteinBinding ?toxicity\n"
            + " WHERE {\n"
            + "    GRAPH <http://larkc.eu#Fixedcontext> {\n"
            + "        ?replacedURI1 skos:prefLabel ?prefLabel \n"
            + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || \n"
            + "                ?replacedURI1 = <http://bar.ac.uk/19278> || \n"
            + "                ?replacedURI1 = <http://foo.com/45273>) \n"
            + "    }\n"
            + "    GRAPH <http://www.chemspider.com> {\n"
            + "        ?replacedURI2 chemspider:smiles ?smiles ;\n"
            + "        chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey .\n"
            + "        ?csid_uri chemspider:inchi ?inchi\n"
            + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || \n"
            + "                ?replacedURI2 = <http://bar.ac.uk/19278> || \n"
            + "                ?replacedURI2 = <http://foo.com/45273>) \n"
            + "    }\n"
            + "    GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:alogp ?alogp }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:hha ?hha }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:hhd ?hhd }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:molformula ?molformula }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:molweight ?molweight }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:mw_freebase ?mw_freebase }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:num_ro5_violations ?num_ro5_violations }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:psa ?psa }\n"
            + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:rtb ?rtb }\n"
            + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || \n"
            + "                ?replacedURI3 = <http://bar.ac.uk/19278> || \n"
            + "                ?replacedURI3 = <http://foo.com/45273>) \n"
            + "  }\n"
            + "    GRAPH <http://linkedlifedata.com/resource/drugbank> {\n"
            + "        OPTIONAL {?replacedURI4 drugbank:affectedOrganism ?affectedOrganism }\n"
            + "        OPTIONAL {?replacedURI4 drugbank:biotransformation ?biotransformation } \n "
            + "        OPTIONAL {?replacedURI4 drugbank:description ?description }\n"
            + "        OPTIONAL {?replacedURI4 drugbank:indication ?indication }\n"
            + "        OPTIONAL {?replacedURI4 drugbank:proteinBinding ?proteinBinding }\n"
            + "        OPTIONAL {?replacedURI4 drugbank:toxicity ?toxicity }\n"
            + "        OPTIONAL {?replacedURI4 drugbank:meltingPoint ?meltingPoint}\n"
            + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || \n"
            + "                ?replacedURI4 = <http://bar.ac.uk/19278> || \n"
            + "                ?replacedURI4 = <http://foo.com/45273>) \n"
            + "    }\n"
            + " }";
    
    /**
     * Test query found in Section compoundInfo  
     */
    @Test
    public void testCompoundInfoMapped() throws MalformedQueryException, QueryExpansionException {
        String inputQuery = CompoundInfoQuery;
        String expectedQuery = CompoundInfoQueryMapped;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
                + "    ?replacedURI1 brenda:recommended_name ?protein_name . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "    ?target_url brenda:recommended_name ?protein_name ; "
                + "    brenda:systematic_name ?systematic_name ; "
                + "    brenda:species ?species_uri ; "
                + "    brenda:has_ec_number ?uniprot_id . "
                + "    ?species_uri rdfs:label ?species } "
                + "  UNION {"
                //+ "    <http://www.example.com/werf> pdsp:has_receptor_name ?protein_name  . "
                + "    ?replacedURI2 pdsp:has_receptor_name ?protein_name . "
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
                + "    ?target_url pdsp:has_receptor_name ?protein_name "
                + "        OPTIONAL {?target_url pdsp:has_unigene_id ?unigene_id ; pdsp:has_nsc_number ?nsc} "
                + "        OPTIONAL {?target_url pdsp:pubmed_id ?pubmed_id } "
                + "        OPTIONAL {?target_url pdsp:species ?species} "
                + "    } "
                + " }";
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section proteinInfo
     */
    @Test
    public void testProteinInfo () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
                + " SELECT DISTINCT ?prefLabel ?smiles ?inchi ?inchiKey "
                + "   ?molweight ?num_ro5_violations ?std_type ?relation ?std_value ?std_unites ?assay_organism "
                + "   ?compound_name ?drugType "
                + " WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "     <http://www.example.com/werf> skos:prefLabel ?prefLabel "
                + "   }"
                + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "     ?assay2target_uri c2b2r_chembl:tid <http://www.example.com/werf> ;"
                + "     c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism ."
                + "     ?activity_uri farmbio:onAssay ?assay_uri ;  c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ;"
                + "     c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ;"
                + "     c2b2r_chembl:std_unites ?std_unites"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi}"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey}"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "     OPTIONAL {?drug_uri drugbank:target <http://www.example.com/werf> ; drugbank:genericName ?compound_name . ?drug_uri drugbank:drugType ?drugType_uri . "
                + "       ?drugType_uri rdfs:label ?drugType}"
                + "   }"
                + " }   ";

        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
                + " SELECT DISTINCT ?prefLabel ?smiles ?inchi ?inchiKey "
                + "   ?molweight ?num_ro5_violations ?std_type ?relation ?std_value ?std_unites ?assay_organism "
                + "   ?compound_name ?drugType "
                + " WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "     ?replacedURI1 skos:prefLabel ?prefLabel "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "   }"
                + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "     ?assay2target_uri c2b2r_chembl:tid ?replacedURI2 ;"
                + "     c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism ."
                + "     ?activity_uri farmbio:onAssay ?assay_uri ;  c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ;"
                + "     c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ;"
                + "     c2b2r_chembl:std_unites ?std_unites"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles }"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi}"
                + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey}"
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
                + "   }"
                //Moved the optional outside of the graph as that is what the query expander does.
                + "   OPTIONAL {"
                + "     GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "         ?drug_uri drugbank:target ?replacedURI3 ; drugbank:genericName ?compound_name . ?drug_uri drugbank:drugType ?drugType_uri . "
                + "         ?drugType_uri rdfs:label ?drugType"
                + "         FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
                + "     }"
                + "   }"
                + " }   ";

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Old Test query found in Section compoundPharmacology
     */
    @Test
    public void testCompoundPharmacologyV1 () throws MalformedQueryException, QueryExpansionException {
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
                + "    ?replacedURI1 brenda:has_inhibitor ?compound_name . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
               //+ " 	brenda:has_ic50_value_of ?ic50 ; "
                + "    ?replacedURI2 brenda:has_ic50_value_of ?ic50 . "
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
                //+ " 	brenda:species ?species_uri . "
                + "    ?replacedURI3 brenda:species ?species_uri . "
                + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
                + " 	?species_uri rdfs:label ?species } "
                + " 	UNION {"
                //+ "     <http://www.example.com/werf> pdsp:has_test_ligand_name ?compound_name ; "
                + "    ?replacedURI4 pdsp:has_test_ligand_name ?compound_name . "
                + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || "
                + "                ?replacedURI4 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI4 = <http://foo.com/45273>) "
                //+ " 	pdsp:has_receptor_name ?target_name; "
                + "    ?replacedURI5 pdsp:has_receptor_name ?target_name . "
                + "        FILTER (?replacedURI5 = <http://www.example.com/werf> || "
                + "                ?replacedURI5 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI5 = <http://foo.com/45273>) "
                //+ " 	pdsp:species ?species ; "
                + "    ?replacedURI6 pdsp:species ?species . "
                + "        FILTER (?replacedURI6 = <http://www.example.com/werf> || "
                + "                ?replacedURI6 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI6 = <http://foo.com/45273>) "
                //+ " 	pdsp:pubmed_id ?pubmed_id ; " 
                + "    ?replacedURI7 pdsp:pubmed_id ?pubmed_id . "
                + "        FILTER (?replacedURI7 = <http://www.example.com/werf> || "
                + "                ?replacedURI7 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI7 = <http://foo.com/45273>) "
                //+ " 	pdsp:has_ki_value ?ki_entry . " 
                + "    ?replacedURI8 pdsp:has_ki_value ?ki_entry . "
                + "        FILTER (?replacedURI8 = <http://www.example.com/werf> || "
                + "                ?replacedURI8 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI8 = <http://foo.com/45273>) "
                + " 	?ki_entry rdf:value ?ki_value ; " 
                + " 	pdsp:unit ?ki_unit}"
                + "}"; 
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section compoundPharmacology
     */
    @Test
    public void testCompoundPharmacology () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
                + " SELECT DISTINCT ?prefLabel ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations "
                + "      ?std_type ?relation ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType "
                + " WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "     <http://www.example.com/werf> skos:prefLabel ?prefLabel "
                + "   }"
                + "   GRAPH <http://www.chemspider.com> {"
                + "     <http://www.example.com/werf> chemspider:smiles ?smiles ;"
                + "         chemspider:inchi ?inchi ; "
                + "         chemspider:inchikey ?inchiKey ."
                + "     ?csid_uri chemspider:inchi ?inchi"
                + "   }"
                + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "     OPTIONAL { "
                + "         ?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno  <http://www.example.com/werf> ;"
                + "             c2b2r_chembl:std_type ?std_type ; "
                + "             c2b2r_chembl:relation ?relation ; "
                + "             c2b2r_chembl:std_value ?std_value ;"
                + "             c2b2r_chembl:std_unites ?std_unites ; "
                + "             farmbio:onAssay ?assay_uri ."
                + "         ?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; "
                + "             c2b2r_chembl:assay_organism ?assay_organism;"
                + "             c2b2r_chembl:tid ?tid . "
                + "         ?tid c2b2r_chembl:pref_name ?target_pref_name "
                + "     }"
                + "     OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:molweight ?molweight }"
                + "     OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "   }"
                + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "     OPTIONAL { "
                + "         <http://www.example.com/werf> drugbank:drugType ?drugType_uri . "
                + "         ?drugType_uri rdfs:label ?drugType "
                + "     }"
                + "   }"
                + " }"; 
        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
                + " SELECT DISTINCT ?prefLabel ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations "
                + "      ?std_type ?relation ?std_value ?std_unites ?assay_organism ?target_pref_name ?drugType "
                + " WHERE {"
                + "   GRAPH <http://larkc.eu#Fixedcontext> {"
                + "     ?replacedURI1 skos:prefLabel ?prefLabel "
                + "     FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "             ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "             ?replacedURI1 = <http://foo.com/45273>) "
                + "   }"
                + "   GRAPH <http://www.chemspider.com> {"
                + "     ?replacedURI2 chemspider:smiles ?smiles ;"
                + "         chemspider:inchi ?inchi ; "
                + "         chemspider:inchikey ?inchiKey ."
                + "     ?csid_uri chemspider:inchi ?inchi"
                + "     FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "             ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "             ?replacedURI2 = <http://foo.com/45273>) "
                + "   }"
                + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "     OPTIONAL { "
                + "         ?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno  ?replacedURI3 ;"
                + "             c2b2r_chembl:std_type ?std_type ; "
                + "             c2b2r_chembl:relation ?relation ; "
                + "             c2b2r_chembl:std_value ?std_value ;"
                + "             c2b2r_chembl:std_unites ?std_unites ; "
                + "             farmbio:onAssay ?assay_uri ."
                + "         ?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; "
                + "             c2b2r_chembl:assay_organism ?assay_organism;"
                + "             c2b2r_chembl:tid ?tid . "
                + "         ?tid c2b2r_chembl:pref_name ?target_pref_name "
                + "     }"
                + "     OPTIONAL { ?replacedURI3 c2b2r_chembl:molweight ?molweight }"
                + "     OPTIONAL { ?replacedURI3 c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
                + "   }"
                //Moved the optional outside of the graph as that is what the query expander does.
                + "   OPTIONAL { "
                + "      GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "         ?replacedURI4 drugbank:drugType ?drugType_uri . "
                + "         ?drugType_uri rdfs:label ?drugType "
                + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || "
                + "                ?replacedURI4 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI4 = <http://foo.com/45273>) "
                + "     }"
                + "   }"
                + " }"; ; 
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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

    static String PharmacologyQuery = " PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
            + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
            + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
            + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
            + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
            + " SELECT DISTINCT ?target_name ?compound_name ?csid_uri ?drugType "
            + "   ?std_type ?relation ?std_value ?std_unites ?assay_organism"
            + "   ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations"
            + " WHERE {"
            + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
            + "     ?assay2target_uri c2b2r_chembl:tid <http://www.example.com/werf> ;"
            + "         c2b2r_chembl:assay_id ?assay_uri ; "
            + "         c2b2r_chembl:assay_organism ?assay_organism ."
            + "     ?activity_uri farmbio:onAssay ?assay_uri ;  "
            + "     c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ;"
            + "         c2b2r_chembl:std_type ?std_type ; "
            + "         c2b2r_chembl:relation ?relation ; "
            + "         c2b2r_chembl:std_value ?std_value ;"
            + "         c2b2r_chembl:std_unites ?std_unites"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi}"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey}"
            + "     OPTIONAL {?csid_uri skos:exactMatch ?compound_uri}"
            + "   }"
            + "   GRAPH <http://linkedlifedata.com/resource/drugbank> {"
            + "     OPTIONAL {"
            + "         ?drug_uri drugbank:target   <http://www.example.com/werf> ; "
            + "             drugbank:drugType ?drugType_uri . "
            + "         ?drugType_uri rdfs:label ?drugType}"
            + "   }"
            + "   GRAPH <http://larkc.eu#Fixedcontext> {"
            + "     <http://www.example.com/werf> skos:prefLabel ?target_name . "
            + "     ?compound_cw skos:exactMatch ?csid_uri ; "
            + "         skos:prefLabel ?compound_name"
            + "   }"
            + " }";

    /**
     * Test query found in Section proteinPharmacology
     */
    @Test
    public void testProteinPharmacology () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = PharmacologyQuery;
        String expectedQuery = "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
            + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
            + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
            + " PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#>"
            + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + " PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
            + " SELECT DISTINCT ?target_name ?compound_name ?csid_uri ?drugType "
            + "   ?std_type ?relation ?std_value ?std_unites ?assay_organism"
            + "   ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations"
            + " WHERE {"
            + "   GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
            + "     ?assay2target_uri c2b2r_chembl:tid   ?replacedURI1 ;"
            + "         c2b2r_chembl:assay_id ?assay_uri ; "
            + "         c2b2r_chembl:assay_organism ?assay_organism ."
            + "     ?activity_uri farmbio:onAssay ?assay_uri ;  "
            + "         c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ;"
            + "         c2b2r_chembl:std_type ?std_type ; "
            + "         c2b2r_chembl:relation ?relation ; "
            + "         c2b2r_chembl:std_value ?std_value ;"
            + "         c2b2r_chembl:std_unites ?std_unites"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles }"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi}"
            + "     OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey}"
            + "     OPTIONAL { ?csid_uri skos:exactMatch ?compound_uri}"
            + "     FILTER (?replacedURI1 = <http://www.example.com/werf> || "
            + "             ?replacedURI1 = <http://bar.ac.uk/19278> || "
            + "             ?replacedURI1 = <http://foo.com/45273>) "
            + "   }"
            //Moved the optional outside of the graph as that is what the query expander does.
            + "   OPTIONAL {"
            + "     GRAPH <http://linkedlifedata.com/resource/drugbank> {"
            + "         ?drug_uri drugbank:target   ?replacedURI2 ; "
            + "             drugbank:drugType ?drugType_uri . "
            + "         ?drugType_uri rdfs:label ?drugType "
            + "         FILTER (?replacedURI2 = <http://www.example.com/werf> || "
            + "                 ?replacedURI2 = <http://bar.ac.uk/19278> || "
            + "                 ?replacedURI2 = <http://foo.com/45273>) "
            + "     }"
            + "   }"
            + "   GRAPH <http://larkc.eu#Fixedcontext> {"
            + "     ?replacedURI3 skos:prefLabel ?target_name ."
            + "     ?compound_cw skos:exactMatch ?csid_uri ; "
            + "         skos:prefLabel ?compound_name"
            + "     FILTER (?replacedURI3 = <http://www.example.com/werf> || "
            + "             ?replacedURI3 = <http://bar.ac.uk/19278> || "
            + "             ?replacedURI3 = <http://foo.com/45273>) "
            + "   }"
            + " }";

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     
    static String PharmacologyQueryV1 = "PREFIX brenda: <http://brenda-enzymes.info/> "
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
    public void testProteinPharmacologyV1 () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = PharmacologyQueryV1;
        String expectedQuery = "PREFIX brenda: <http://brenda-enzymes.info/> "
                + " PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> "
                + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT ?protein_name ?uniprot_entry ?species ?ic50 ?inhibitor ?pubmed_id ?ki_value ?ki_unit "
                + " WHERE {"
                + " 	{"
                //+ "     <http://www.example.com/werf> brenda:recommended_name ?protein_name ;"
                + "    ?replacedURI1 brenda:recommended_name ?protein_name . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                //+ " 	    brenda:has_ec_number ?uniprot_entry ;"
                + "    ?replacedURI2 brenda:has_ec_number ?uniprot_entry . "
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
                //+ " 	    brenda:species ?species_code ."
                + "    ?replacedURI3 brenda:species ?species_code . "
                + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
                + " 	?species_code rdfs:label ?species "
                + " 		OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 } "
                + " 	} "
                + "   UNION {"
                //+ "     <http://www.example.com/werf> pdsp:has_receptor_name ?protein_name ;"
                + "    ?replacedURI4 pdsp:has_receptor_name ?protein_name . "
                + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || "
                + "                ?replacedURI4 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI4 = <http://foo.com/45273>) "
                //+ " 	    pdsp:species ?species ;"
                + "    ?replacedURI5 pdsp:species ?species . "
                + "        FILTER (?replacedURI5 = <http://www.example.com/werf> || "
                + "                ?replacedURI5 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI5 = <http://foo.com/45273>) "
                //+ " 	    pdsp:has_test_ligand_name ?inhibitor ;"
                + "    ?replacedURI6 pdsp:has_test_ligand_name ?inhibitor . "
                + "        FILTER (?replacedURI6 = <http://www.example.com/werf> || "
                + "                ?replacedURI6 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI6 = <http://foo.com/45273>) "
                + " 		OPTIONAL {?target_uri pdsp:pubmed_id ?pubmed_id ;"
                + " 		pdsp:has_ki_value ?ki_entry ."
                + " 		?ki_entry rdf:value ?ki_value ;"
                + " 		pdsp:unit ?ki_unit}"
                + " 	}"
                + " }";

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section proteinPharmacology
     * Witrh Single replace
     */
    @Test
    public void testProteinPharmacologySingleReplaceV1 () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = PharmacologyQueryV1;
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");

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
     * Test query found in Section chemicalSimilaritySearch 
     */
    @Test
    public void testChemicalSimilaritySearchV1  () throws MalformedQueryException, QueryExpansionException {
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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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
     * Test query found in Section chemicalSimilaritySearch 
     */
    @Test
    public void testChemicalSpiderInfo  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = " PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase "
                + "        ?num_ro5_violations ?psa ?rtb ?affectedOrganism ?biotransformation ?description ?indication "
                + "        ?meltingPoint ?proteinBinding ?toxicity "
                + " WHERE {"
                + "    GRAPH <http://larkc.eu#Fixedcontext> {"
                + "        <http://www.example.com/werf> <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel "
                + "    }"
                + "    GRAPH <http://www.chemspider.com> {"
                + "        <http://www.example.com/werf> chemspider:smiles ?smiles ;"
                + "        chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey ."
                + "        ?csid_uri chemspider:inchi ?inchi"
                + "    }"
                + "    GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:alogp ?alogp }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:hha ?hha }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:hhd ?hhd }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:molformula ?molformula }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:molweight ?molweight }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:mw_freebase ?mw_freebase }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:psa ?psa }"
                + "        OPTIONAL { <http://www.example.com/werf> c2b2r_chembl:rtb ?rtb }"
                + " }"
                + "    GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:affectedOrganism ?affectedOrganism }"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:biotransformation ?biotransformation }  "
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:description ?description }"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:indication ?indication }"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:proteinBinding ?proteinBinding }"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:toxicity ?toxicity }"
                + "        OPTIONAL {<http://www.example.com/werf> drugbank:meltingPoint ?meltingPoint}"
                + "    }"
                + " }";

        String expectedQuery = " PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>"
                + " PREFIX chemspider: <http://rdf.chemspider.com/#>"
                + " PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>"
                + " SELECT ?csid_uri ?smiles ?inchi ?inchiKey ?alogp ?hha ?hhd ?molformula ?molweight ?mw_freebase "
                + "        ?num_ro5_violations ?psa ?rtb ?affectedOrganism ?biotransformation ?description ?indication "
                + "        ?meltingPoint ?proteinBinding ?toxicity "
                + " WHERE {"
                + "    GRAPH <http://larkc.eu#Fixedcontext> {"
                + "        ?replacedURI1 <http://www.w3.org/2004/02/skos/core#prefLabel> ?prefLabel "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "    }"
                + "    GRAPH <http://www.chemspider.com> {"
                + "        ?replacedURI2 chemspider:smiles ?smiles ;"
                + "        chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey ."
                + "        ?csid_uri chemspider:inchi ?inchi"
                + "        FILTER (?replacedURI2 = <http://www.example.com/werf> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI2 = <http://foo.com/45273>) "
                + "    }"
                + "    GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:alogp ?alogp }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:hha ?hha }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:hhd ?hhd }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:molformula ?molformula }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:molweight ?molweight }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:mw_freebase ?mw_freebase }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:num_ro5_violations ?num_ro5_violations }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:psa ?psa }"
                + "        OPTIONAL { ?replacedURI3 c2b2r_chembl:rtb ?rtb }"
                + "        FILTER (?replacedURI3 = <http://www.example.com/werf> || "
                + "                ?replacedURI3 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI3 = <http://foo.com/45273>) "
                + " }"
                + "    GRAPH <http://linkedlifedata.com/resource/drugbank> {"
                + "        OPTIONAL {?replacedURI4 drugbank:affectedOrganism ?affectedOrganism }"
                + "        OPTIONAL {?replacedURI4 drugbank:biotransformation ?biotransformation }  "
                + "        OPTIONAL {?replacedURI4 drugbank:description ?description }"
                + "        OPTIONAL {?replacedURI4 drugbank:indication ?indication }"
                + "        OPTIONAL {?replacedURI4 drugbank:proteinBinding ?proteinBinding }"
                + "        OPTIONAL {?replacedURI4 drugbank:toxicity ?toxicity }"
                + "        OPTIONAL {?replacedURI4 drugbank:meltingPoint ?meltingPoint}"
                + "        FILTER (?replacedURI4 = <http://www.example.com/werf> || "
                + "                ?replacedURI4 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI4 = <http://foo.com/45273>) "
                + "    }"
                + " }";
      
        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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
     * Test query found in Section subclasses
     */
    @Test
    public void testSubclasses () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?s WHERE { ?s rdfs:subClassOf <http://www.example.com/werf> }";

        String expectedQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + " SELECT DISTINCT ?s WHERE { "
                + "    ?s rdfs:subClassOf ?replacedURI1 . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
               + "}";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section superclasses
     */
    @Test
    public void testSuperclasses () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
             +" SELECT DISTINCT ?o WHERE { <http://www.example.com/werf> rdfs:subClassOf ?o }";
        String expectedQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                +" SELECT DISTINCT ?o WHERE { "
                + "    ?replacedURI1 rdfs:subClassOf ?o  . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section triplesWithSubject 
     */
    @Test
    public void testTriplesWithSubject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p ?o WHERE {<http://www.example.com/werf> ?p ?o}";
        String expectedQuery = "SELECT DISTINCT ?p ?o WHERE {"
                + "    ?replacedURI1  ?p ?o . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section triplesWithPredicate
     */
    @Test
    public void testTriplesWithPredicate () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s ?o WHERE {?s <http://www.example.com/werf> ?o}";

        String expectedQuery = "SELECT DISTINCT ?s ?o WHERE {?s <http://www.example.com/werf> ?o}";      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section predicatesForSubject 
     */
    @Test
    public void testPredicatesForSubject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {<http://www.example.com/werf> ?p ?o}";
        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?replacedURI1  ?p ?o . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section subjectsWithPredicate 
     */
    @Test
    public void testSubjectsWithPredicate  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";     

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section predicatesWithObject 
     */
    @Test
    public void testPredicatesWithObject  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {?s  ?p <http://www.example.com/werf>}";
        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?s  ?p ?replacedURI1 . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section objectsOfPredicate
     */
    @Test
    public void testObjectsOfPredicate () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/werf> ?o}";    

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section subjects 
     */
    @Test
    public void testSubjects  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?s WHERE {?s <http://www.example.com/bark> <http://www.example.com/werf>}";
        String expectedQuery = "SELECT DISTINCT ?s WHERE {"
                + "    ?s <http://www.example.com/bark> ?replacedURI1 . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section predicates 
     */
    @Test
    public void testPredicates  () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?p WHERE {<http://www.example.com/werf>?p <http://www.example.com/bark>}";

        String expectedQuery = "SELECT DISTINCT ?p WHERE {"
                + "    ?replacedURI1 ?p ?replacedURI2 . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "        FILTER (?replacedURI2 = <http://www.example.com/bark> || "
                + "                ?replacedURI2 = <http://bar.ac.uk/453245355435>) "
                + "}";      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");
        dummyIMSMapper.addMapping("http://www.example.com/bark","http://www.example.com/bark");
        dummyIMSMapper.addMapping("http://www.example.com/bark","http://bar.ac.uk/453245355435");

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
     * Test query found in Section objects 
     */
    @Test
    public void testObjects () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "SELECT DISTINCT ?o WHERE {<http://www.example.com/werf> <http://www.example.com/bark> ?o}";

        String expectedQuery = "SELECT DISTINCT ?o WHERE {"
                + "    ?replacedURI1  <http://www.example.com/bark> ?o . "
                + "        FILTER (?replacedURI1 = <http://www.example.com/werf> || "
                + "                ?replacedURI1 = <http://bar.ac.uk/19278> || "
                + "                ?replacedURI1 = <http://foo.com/45273>) "
                + "}";      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://www.example.com/werf");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://bar.ac.uk/19278");
        dummyIMSMapper.addMapping("http://www.example.com/werf","http://foo.com/45273");

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
     * Test query found in Section proteinInfo
     * /
    @Test
    public void test () throws MalformedQueryException, QueryExpansionException {
        String inputQuery = "";
//                + "
        String expectedQuery = inputQuery;      

        final DummyIMSMapper dummyIMSMapper = new DummyIMSMapper();

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

 /**/
    
}
