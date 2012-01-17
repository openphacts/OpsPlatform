/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Christian
 */
public class openrdfToy {
     public static void expanderTest(String inputQuery, String expectedQuery) 
             throws MalformedQueryException, QueryExpansionException {
         SetOfStatements larkc = new SPARQLQueryImpl(inputQuery).toRDF();
         SPARQLQuery larkcQuery = DataFactory.INSTANCE.createSPARQLQuery(larkc);
         String larkcString = larkcQuery.toString();
         if (QueryUtils.sameTupleExpr(inputQuery,  larkcString)) {
             System.out.println("larkc OK");
         }
         if (larkcQuery instanceof SPARQLQueryImpl){
            SPARQLQueryImpl impl = (SPARQLQueryImpl)larkcQuery;
            ParsedQuery larkcParsedQuery = impl.getParsedQuery();
            Dataset larkcDataset = larkcParsedQuery.getDataset();
            System.out.println(larkcDataset);
         }
         final DummyIRSMapper dummyIRSMapper = new DummyIRSMapper();
        
         IRSSPARQLExpand expander = 
                new IRSSPARQLExpand(new URIImpl("http://larkc.eu/plugin#IRSSPARQLExpand1")) {
            @Override
            IRSMapper instantiateIRSMapper() {
                return dummyIRSMapper;
            }
         };
         expander.initialiseInternal(null);
         SetOfStatements newLarkc = expander.invokeInternalWithExceptions(larkc);
         SPARQLQuery expandedQuery = DataFactory.INSTANCE.createSPARQLQuery(newLarkc);
         larkcString = expandedQuery.toString();
         System.out.println("expanded query");
         System.out.println(larkcString);
         if (QueryUtils.sameTupleExpr(expectedQuery,  larkcString)) {
             System.out.println("expanded OK");
         }         
     }

     public static void main(String[] args) throws MalformedQueryException, Exception {
         SPARQLParser parser = new SPARQLParser();
         String queryStr0 = " SELECT  DISTINCT ?protein ?protein2"
                + " WHERE {"
                + "?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> "
                + "<http://brenda-enzymes.info/1.1.1.1> . "
                + "?protein2 <http://www.biopax.org/release.biopax-level2.owl#NAME> ?name . "
                + "<http://rdf.chemspider.com/37> ?p ?o ."
                + "}";
         String queryStr1 = "SELECT ?protein"
                + " WHERE {"
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1>)} "
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine2 . "
                + "FILTER (?objectUriLine = <http://bar.com/8hd83> || "
                + "?objectUriLine2 = <http://foo.info/2.2.2.2>)} "
                + "}";
         String queryStr2 = "SELECT ?protein ?name "
                + "WHERE { "
                + "<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . "
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
         String queryStr3 = "SELECT ?protein ?name "
                + "WHERE { "
                + "OPTIONAL {<http://foo.info/1.1.1.1> <http://foo.com/somePredicate> ?protein . }"
                + "OPTIONAL {<http://bar.com/ijdu> <http://foo.com/anotherPredicate> ?name . }"
                + "}";
         String queryStr4 = "SELECT ?protein "
                + " WHERE {"
                + "{?protein <http://www.foo.org/somePredicate> "
                + "?objectUriLine1 . "
                + "FILTER (?objectUriLine1 = <http://bar.com/8hd83> || "
                + "?objectUriLine1 = <http://foo.info/1.1.1.1> || "
                + "(?objectUriLine1 = <http://bar.com/8hd83> && "
                + "?objectUriLine1 = <http://foo.info/2.2.2.2>))} "
                + "}";
         String queryStr5 = "SELECT  ?protein"
                + "{"
                + "FILTER (( ?protein = <http://www.another.org> ||  ?protein = <http://something.org>))"
                + " ?protein <http://www.biopax.org/release/biopax-level2.owl#EC-NUMBER> ?objectUri1 ."
                + " FILTER (?objectUri1 = <http://example.com/983juy> || ?objectUri1 = <http://brenda-enzymes.info/1.1.1.1>)"
                +" }";
         String queryStr6 = "SELECT ?stuff ?protein "
            + "WHERE {"
            + "?stuff <http://www.foo.com/predicate> ?protein . "
            + "FILTER (?stuff = <http://brenda-enzymes.info/1.1.1.1> || <http://Fishlink/123> = ?stuff)"
            + "}";
         String queryStr7 = "SELECT ?stuff ?protein WHERE {FILTER ((?stuff = <http://example.com/983juy> || ?stuff = <http://manchester.com/983juy> ||?stuff = <http://brenda-enzymes.info/1.1.1.1>) ||?stuff = (<http://Fishlink/456> || ?stuff = <http://Fishlink/123>))?stuff <http://www.foo.com/predicate> ?protein . }";
         String queryStr8 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "PREFIX org:    <http://example.com/ns#>"
            + "CONSTRUCT { ?x foaf:name ?name } "
            + "WHERE  { ?x org:employeeName ?name }";
         
         String queryStr9 = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT  ?title "
            + "WHERE   { ?x dc:title ?title "
            + "          FILTER regex(?title, \"web\", \"i\" ) " 
            + "        }";
         String queryStr10 = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/>"
            + "PREFIX  ns:  <http://example.org/ns#>"
            + "SELECT  ?title ?price "
            + "WHERE   { ?x dc:title ?title ."
            + "          OPTIONAL { ?x ns:price ?price . FILTER (?price < 30) }"
            + "        }";
         String queryStr11 = "PREFIX dc10:  <http://purl.org/dc/elements/1.0/>"
            + "PREFIX dc11:  <http://purl.org/dc/elements/1.1/>"
            + "SELECT ?title "
            + "WHERE  { { ?book dc10:title  ?title } UNION { ?book dc11:title  ?title } }";
         String queryStrTooHard12 = "PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?person "
            + "WHERE "
            + "{"
            + "    ?person rdf:type  foaf:Person ."
            + "    FILTER EXISTS {?person foaf:name ?name }"
            + "}     ";
         String queryStrTooHard13 = "PREFIX :       <http://example/>" 
            + "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
            + "SELECT DISTINCT ?s "
            + "WHERE { "
            + "   ?s ?p ?o ."
            + "   MINUS {"
            + "      ?s foaf:givenName \"Bob\" ."
            + "   } "
            + "}";
         String queryStr14 = "SELECT * "
            + "WHERE { "
            + "   ?s ?p ?o ."
            + "}";
         String queryStrTooHard15 = "PREFIX :  <http://books.example/> "
            + "SELECT (SUM(?lprice) AS ?totalPrice) "
            + "WHERE { "
            + "  ?org :affiliates ?auth . "
            + "  ?auth :writesBook ?book . "
            + "  ?book :price ?lprice . "
            + "} "
            + "GROUP BY ?org "
            + "HAVING (SUM(?lprice) > 10) ";
         String queryStr16 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT  ?name "
            + "FROM    <http://example.org/foaf/aliceFoaf> "
            + "WHERE   { ?x foaf:name ?name }";
         String queryStr17 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
            + "SELECT ?who ?g ?mbox "
            + "FROM <http://example.org/dft.ttl> "
            + "FROM NAMED <http://example.org/alice> "
            + "FROM NAMED <http://example.org/bob> "
            + "WHERE { "
            + "    ?g dc:publisher ?who ."
            + "    GRAPH ?g { ?x foaf:mbox ?mbox. }"
            + "}";
         String queryStr18 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "SELECT ?src ?bobNick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "    GRAPH ?src "
            + "    { ?x foaf:mbox <mailto:bob@work.example> ."
            + "      ?x foaf:nick ?bobNick "
            + "    }"
            + "  }";
        String queryStr19 ="PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
            + "PREFIX data: <http://example.org/foaf/> "
            + "SELECT ?nick "
            + "FROM NAMED <http://example.org/foaf/aliceFoaf> "
            + "FROM NAMED <http://example.org/foaf/bobFoaf> "
            + "WHERE "
            + "  {"
            + "     GRAPH data:bobFoaf {"
            + "         ?x foaf:mbox \"mailto:bob@work.example\" ."
            + "         ?x foaf:nick ?nick }"
            + "  }";
        String queryStr20 = 
                " PREFIX cspr: <http://rdf.chemspider.com/#> "
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
         String queryStr21 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name "
                + "WHERE { ?x foaf:name ?name }"
                + "ORDER BY ?name";
                 
         String queryStr22 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                 + "SELECT REDUCED ?name WHERE { ?x foaf:name ?name }";
         String queryStr23 = "CONSTRUCT { ?s ?p ?o } WHERE { ?o ?p ?s }";
         String queryStr24 = "SELECT REDUCED ?s ?p ?o  WHERE { ?o ?p ?s }";
         String queryStr25 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            + "PREFIX org:    <http://example.com/ns#>"
            + "CONSTRUCT { ?x foaf:name ?name }"
            + "WHERE  { ?x org:employeeName ?name }";
         String queryStr26 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                 + "SELECT ?name "
                 + "WHERE { ?x foaf:name ?name }"
                 + "LIMIT 20";         
         String queryStr27 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                 + "SELECT  ?name "
                 + "WHERE   { ?x foaf:name ?name }"
                 + "ORDER BY ?name "
                 + "LIMIT   5 "
                 + "OFFSET  10 ";
        String queryStr28 ="PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
                + "PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#>"
                + "CONSTRUCT { ?x  vcard:N _:v ."
                + "            _:v vcard:givenName ?gname ."
                + "            _:v vcard:familyName ?fname }"
                + "WHERE"
                + " {"
                + "    { ?x foaf:firstname ?gname } UNION  { ?x foaf:givenname   ?gname } ."
                + "    { ?x foaf:surname   ?fname } UNION  { ?x foaf:family_name ?fname } ."
                + " }";
        String queryStr29 = "PREFIX  dc: <http://purl.org/dc/elements/1.1/>"
                + "PREFIX app: <http://example.org/ns#>"
                + "PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>"
                + "CONSTRUCT { ?s ?p ?o } WHERE"
                + " {"
                + "   GRAPH ?g { ?s ?p ?o } ."
                + "   { ?g dc:publisher <http://www.w3.org/> } ."
                + "   { ?g dc:date ?date } ."
                + "   FILTER ( app:customDate(?date) > \"2005-02-28T00:00:00Z\"^^xsd:dateTime ) ."
                //+ "   FILTER ( ?date > \"2005-02-28T00:00:00Z\" )."
                + " }";
        String queryStr30 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX site: <http://example.org/stats#> "
                + "CONSTRUCT { [] foaf:name ?name } "
                + "WHERE "
                + "{ [] foaf:name ?name ;"
                + "     site:hits ?hits ."
                + "}"
                + "ORDER BY desc(?hits) "
                + "LIMIT 2";
       String queryStr31 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name"
                + "WHERE  { "
                + "[] foaf:name ?name ."
                + "[] foaf:name2 ?name ."
                + "[] foaf:name3 ?name ."
                + "[] foaf:name4 ?name ."
                + "[] foaf:name5 ?name ."
                + "[] foaf:name6 ?name ."
                + "[] foaf:name7 ?name ."
                + "[] foaf:name8 ?name ."
                + "[] foaf:name9 ?name ."
                + "[] foaf:name10 ?name ."
                + "[] foaf:name11 ?name ."
                + "[] foaf:name12 ?name ."
                + "[] foaf:name13 ?name ."
                + "[] foaf:name14 ?name ."
                + "[] foaf:name15 ?name ."
                + "[] foaf:name16 ?name ."
                + "[] foaf:name17 ?name ."
                + "[] foaf:name18 ?name ."
                + "[] foaf:name19 ?name ."
                + "[] foaf:name20 ?name ."
                + "[] foaf:name21 ?name ."
                + "[] foaf:name22 ?name ."
                + "[] foaf:name23 ?name ."
                + "[] foaf:name24 ?name ."
                + "[] foaf:name25 ?name ."
                + "[] foaf:name26 ?name ."
                + "[] foaf:name27 ?name ."
                + "[] foaf:name28 ?name ."
                + "[] foaf:name29 ?name ."
                + "[] foaf:name30 ?name ."
                + "[] foaf:name31 ?name ."
                + "[] foaf:name32 ?name ."
                + "[] foaf:name33 ?name ."
                + "[] foaf:name34 ?name ."
                + "[] foaf:name35 ?name ."
                + "[] foaf:name36 ?name ."
                + "[] foaf:name37 ?name ."
                + "[] foaf:name38 ?name ."
                + "[] foaf:name39 ?name ."
                + "[] foaf:name40 ?name ;"
                + "   foaf:name41 ?name ."
                + "[] foaf:name42 ?name ;"
                + "   foaf:name43 ?name ."
                + "[] foaf:name44 ?name ;"
                + "   foaf:name45 ?name ;"
                + "   foaf:name46 ?name ;"
                + "   foaf:name47 ?name ."
                + "[] foaf:name46 ?name ;"
                + "   foaf:name47 ?name ."
                + "[] foaf:name48 ?name ;"
                + "   foaf:name49 ?name ."
                + "[] foaf:name50 ?name ."
                + "[] foaf:name51 ?name ."
                + "[] foaf:name52 ?name ."
                + "[] foaf:name53 ?name ."
                + "[] foaf:name54 ?name ."
                + "[] foaf:name55 ?name ."
                + "[] foaf:name56 ?name ."
                + "[] foaf:name57 ?name ."
                + "[] foaf:name58 ?name ."
                + "[] foaf:name59 ?name ."
                + "}";
       String queryStr32 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
               + "ASK  { ?x foaf:name  \"Alice\" }";
       String queryStr33= "DESCRIBE <http://example.org/1> <http://example.org/2> <http://example.org/1> ";
       String queryStr34 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
               + "DESCRIBE ?x ?y "
               + "WHERE    { "
               + "   FILTER ( ?x > 25) }";
       String queryStr35 = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
               + "DESCRIBE ?x "
               + "WHERE    { ?x foaf:mbox <mailto:alice@org> }";
       String queryStr36 = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>"
               + "ASK  { ?x foaf:name  \"Alice\" }";
       String queryStr37 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "PREFIX dc:   <http://purl.org/dc/elements/1.1/>"
                + "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?name"
                + " WHERE { ?x foaf:givenName  ?givenName ."
                + "         OPTIONAL { ?x dc:date ?date } ."
                + "         FILTER ( bound(?date) ) }";
       String queryStr38 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { "
                + "       FILTER isIRI(?mbox) "
                + "       ?x foaf:name  ?name ;"
                + "          foaf:mbox  ?mbox ."
                + "}";
       String queryStr39 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT ?name ?mbox "
                + "WHERE { "
                + "         {"
                + "            ?x foaf:name  <http://www.example.com/1> "
                + "         }"
                + "         UNION"
                + "         {"
                + "             ?x foaf:name  <http://www.example.com/2> ."
                + "         }"
                + "       ?x foaf:mbox  ?mbox ."
                + "}";
      String queryStr40 =  "SELECT  ?given ?family "
              + "WHERE {"
              + "FILTER  isBlank( ?c)"
              + " ?annot <http://www.w3.org/2000/10/annotation-ns#annotates> <http://www.w3.org/TR/rdf-sparql-query/>. "
              + " ?annot <http://purl.org/dc/elements/1.1/creator>  ?c. "
              + "OPTIONAL {"
              + " ?c <http://xmlns.com/foaf/0.1/given>  ?given. "
              + " ?c <http://xmlns.com/foaf/0.1/family>  ?family. }}";
      String queryStr = "PREFIX  :      <http://example.org/WMterms#>"
                + "PREFIX  t:     <http://example.org/types#>"
                + "SELECT ?aLabel1 ?bLabel "
                + "WHERE { ?a  :label        ?aLabel ."
                + "        ?a  :weight       ?aWeight ."
                + "        ?a  :displacement ?aDisp ."
                + "        ?b  :label        ?bLabel ."
                + "        ?b  :weight       ?bWeight ."
                + "        ?b  :displacement ?bDisp ."
                + "        FILTER ( sameTerm(?aWeight, ?bWeight) && !sameTerm(?aDisp, ?bDisp) ) "
                + "}";
                 //ParsedQuery parsedQuery23 = parser.parseQuery(queryStr23, null); 
         //TupleExpr tupleExpr23 = parsedQuery23.getTupleExpr();
         //System.out.println("23:"+ tupleExpr23);
        
         ParsedQuery parsedQuery = parser.parseQuery(queryStr, null); 
         TupleExpr tupleExpr = parsedQuery.getTupleExpr();
         Dataset dataset = parsedQuery.getDataset();
         System.out.println(tupleExpr);
         
         QueryWriterModelVisitor myVisitor = new QueryWriterModelVisitor(dataset);
         tupleExpr.visit(myVisitor);
         String newQuery = myVisitor.getQuery();
         System.out.println(newQuery);
         ParsedQuery newParsedQuery = parser.parseQuery(newQuery, null); 
         if (QueryUtils.sameTupleExpr(queryStr,  newQuery)) {
             System.out.println("ok");
         }
         //expanderTest(queryStr, queryStr);
     }
     
     
}