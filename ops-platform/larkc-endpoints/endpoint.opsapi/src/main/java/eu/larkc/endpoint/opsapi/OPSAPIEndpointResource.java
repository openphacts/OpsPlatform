/*
   This file is part of the LarKC platform
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.endpoint.opsapi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.parser.QueryParserUtil;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import eu.larkc.core.endpoint.Endpoint;
import eu.larkc.endpoint.opsapi.APIRequestQueryHandler;
import eu.larkc.endpoint.opsapi.exceptions.APIException;
import eu.larkc.endpoint.sparql.SparqlQueryRequest;
import eu.larkc.endpoint.sparql.SparqlQueryResult;
import eu.larkc.endpoint.sparql.exceptions.MalformedSparqlQueryException;
import eu.larkc.core.executor.Executor;


public class OPSAPIEndpointResource extends ServerResource {

	private static Logger logger = LoggerFactory.getLogger(OPSAPIEndpointResource.class);

	protected SparqlQueryRequest parseGetUrl(java.net.URI url, String query)
			throws APIException {
		logger.debug("Query String: "+query);
        int methodIndexStart = query.indexOf("method=");
		if (methodIndexStart==-1) {
			throw new APIException("The variable \"method\" is required.");
		}
        int methodIndexEnd =  query.indexOf("&",methodIndexStart);
        if (methodIndexEnd==-1){
                methodIndexEnd = query.length();
        }
		String[] parts = query.split("&");
		if (parts == null) {
			throw new APIException(
					"The query part of the URL of the GET request is empty.");
		}
		SparqlQueryRequest qr = null;
		String method="";
        method=query.substring(methodIndexStart+7,methodIndexEnd);
        logger.debug("Method:"+method);
		if (method.equals("sparql")){
			qr=sparql(parts);
		} else if (method.equals("triplesWithSubject")){
			qr=triplesWithSubject(parts);		
		} else if (method.equals("triplesWithPredicate")){
			qr=triplesWithPredicate(parts);	
		} else if (method.equals("triplesWithObject")){
			qr=triplesWithObject(parts);	
		} else if (method.equals("predicatesForSubject")){
			qr=predicatesForSubject(parts);				
		} else if (method.equals("subjectsWithPredicate")){
			qr=subjectsWithPredicate(parts);				
		} else if (method.equals("predicatesWithObject")){
			qr=predicatesWithObject(parts);				
		} else if (method.equals("objectsOfPredicate")){
			qr=objectsOfPredicate(parts);
		} else if (method.equals("subjects")){
			qr=subjects(parts);				
		} else if (method.equals("predicates")){
			qr=predicates(parts);
		} else if (method.equals("objects")){
			qr=objects(parts);				
		} else if (method.equals("superclasses")){
			qr=superclasses(parts);	
		} else if (method.equals("subclasses")){
			qr=subclasses(parts);
		} else if (method.equals("compoundLookup")){
			qr=compoundLookup(parts);
		} else if (method.equals("proteinLookup")){
			qr=proteinLookup(parts);
		} else if (method.equals("compoundInfo")){
			qr=compoundInfo(parts);				
		} else if (method.equals("proteinInfo")){
			qr=proteinInfo(parts);
		} else if (method.equals("compoundPharmacology")){
			qr=compoundPharmacology(parts);
		} else if (method.equals("proteinPharmacology")){
			qr=proteinPharmacology(parts);		
		} else if (method.equals("enzymeClassPharmacology")){
			qr=enzymeClassPharmacology(parts);	
		/*} else if (method.equals("chemicalExactStructureSearch")){
			qr=chemicalExactStructureSearch(parts);
		} else if (method.equals("chemicalSubstructureSearch")){
			qr=chemicalSubstructureSearch(parts);
		} else if (method.equals("chemicalSimilaritySearch")){
			qr=chemicalSimilaritySearch(parts);
		} else if (method.equals("chemspiderInfo")){
			qr=chemspiderInfo(parts);*/
		} else {
			throw new APIException("Unknown method name: "+method+". Use one of: " );
		}
		try {
			QueryParserUtil.parseQuery(QueryLanguage.SPARQL,qr.getQuery(), null);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			throw new APIException("Malformed query." );
		} catch (UnsupportedQueryLanguageException e) {
			e.printStackTrace();
			throw new APIException("Should never happen" );
		}
		return qr;
	}
	
	private SparqlQueryRequest chemspiderInfo(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql ="PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/>" +
				"PREFIX chemspider: <http://rdf.chemspider.com/#>" +
				"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
				"SELECT DISTINCT ?compound_name ?csid_uri ?molformula ?molweight ?inchi ?inchiKey ?smiles" +
				"?alogp ?hha ?hhd ?mw_freebase ?num_ro5_violations ?psa ?rtb" +
				"?meltingPoint ?affectedOrganism ?biotransformation ?description ?indication ?proteinBinding ?toxicity" +
				"WHERE { {";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("csids")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				int count=0;
				while (st.hasMoreTokens()){
					if (count > 5) 
						throw new APIException("Maximum 5 CSIDs");
					String token = st.nextToken();
					String uri="<http://rdf.chemspider.com/"+token.trim()+">";
					if (!sparql.endsWith("WHERE { {"))
						sparql+= " UNION {";
					sparql +=" GRAPH <http://larkc.eu#Fixedcontext> { " +
								uri+" skos:prefLabel ?compound_name " +
							"}" +
							"GRAPH <http://www.chemspider.com> { " +
								uri+" chemspider:smiles ?smiles ;" +
								"chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey . " +
								"?csid_uri chemspider:inchi ?inchi " +
							"} " +
							"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
								uri+" c2b2r_chembl:molformula ?molformula " +
								"OPTIONAL { "+uri+" c2b2r_chembl:alogp ?alogp } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:hha ?hha } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:hhd ?hhd } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:molweight ?molweight } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:mw_freebase ?mw_freebase } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:psa ?psa } " +
								"OPTIONAL { "+uri+" c2b2r_chembl:rtb ?rtb } " +
							"} " +
							"OPTIONAL {" +
								"GRAPH <http://linkedlifedata.com/resource/drugbank> { " +
									uri+" drugbank:affectedOrganism ?affectedOrganism " +
									"OPTIONAL {"+uri+" drugbank:biotransformation ?biotransformation } " +
									"OPTIONAL {"+uri+" drugbank:description ?description } " +
									"OPTIONAL {"+uri+" drugbank:indication ?indication } " +
									"OPTIONAL {"+uri+" drugbank:proteinBinding ?proteinBinding } " +
									"OPTIONAL {"+uri+" drugbank:toxicity ?toxicity } " +
									"OPTIONAL {"+uri+" drugbank:meltingPoint ?meltingPoint} " +
								"} " +
							"} " +
						"}";
					count++;
				}
				sparql += "}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method compoundInfo; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}

	private SparqlQueryRequest chemicalSimilaritySearch(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("smiles")) {
				sparql="PREFIX cspr: <http://rdf.chemspider.com/#> " +
						"PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>  " +
						"PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " + 
						"SELECT DISTINCT ?csid_uri ?inchi ?inchi_key ?smiles ?synonym " +
						"?med_chem_friendly ?molweight ?chembl_synonyms ?hhd ?alogp ?mw_freebase ?psa " + 
						"?molformula ?molregno ?ro5_violations ?ro3_pass ?hha ?rtb ?pred_water_sol ?exp_water_sol ?target " + 
						"?weight_average ?category ?description ?generic_name ?half_life ?state ?pred_logs ?brand_name ?pred_hydrophob ?exp_hydrophob " +
						"?pdsp_name ?pdsp_displaced ?cas ?pdsp_target ?pdsp_species ?pdsp_source " +
						"?r1 ?r2 ?r3 ?r4 ?r5 ?r6 ?r7 ?r8 ?r9 ?r10 ?r11 ?r12 ?r13 ?r14 ?r15 ?r16 ?r17 ?r18 ?r19 ?r20 ?r21 ?r22 ?r23 ?r24 ?r25 ?r26 " +
							"WHERE {?csid_uri ext:has_similar \""+value+"\" ; cspr:smiles ?smiles; " +
							"cspr:synonym ?synonym ; cspr:inchi ?inchi ; cspr:inchikey ?inchi_key ; " +
							"cspr:exturl ?mapping . ?csid_uri cspr:exturl ?mapping . " +
								"{ ?mapping skos:exactMatch ?chebi . ?c2b2r_ChEMBL chembl:chebi ?chebi ; " +
								"chembl:med_chem_friendly ?med_chem_friendly; " + 
								"chembl:cid ?cid ; chembl:molweight ?molweight;  " +
								"chembl:synonyms ?chembl_synonyms ; chembl:hhd ?hhd ; " +
								"chembl:alogp ?alogp ; chembl:mw_freebase ?mw_freebase ;  " +
								"chembl:psa ?psa ; chembl:molformula ?molformula ;  " +
								"chembl:molregno ?molregno ; chembl:num_ro5_violations ?ro5_violations ; " + 
								"chembl:ro3_pass ?ro3_pass ; chembl:hha ?hha ; chembl:rtb ?rtb ." + 
									"OPTIONAL {?drugbank_uri db:pubchemCompoundURL ?cid . " +
									"?drugbank_uri db:predictedWaterSolubility ?pred_water_sol ; " + 
									"db:experimentalWaterSolubility ?exp_water_sol ;  " +
									"db:molecularWeightAverage ?weight_average ;  " +
									"db:drugCategory ?category_uri ; db:description ?description ; " + 
									"db:genericName ?generic_name ; db:halfLife ?half_life ;  " +
									"db:state ?state ; db:predictedLogs ?pred_logs ; db:brandName ?brand_name ; " +
									"db:target ?target_uri . ?target_uri rdfs:label ?target . ?category_uri rdfs:label ?category} . " + 
									"OPTIONAL {?drugbank_uri db:predictedLogpHydrophobicity ?pred_hydrophob } .  " +
									"OPTIONAL {?drugbank_uri db:experimentalLogpHydrophobicity ?exp_hydrophob } " +
									"} " +
								"UNION { ?mapping pdsp:has_test_ligand_id ?pdsp_name ; " +
								"pdsp:ligand_displaced ?pdsp_displaced; " +
								"pdsp:has_cas_num ?cas ; pdsp:has_receptor_name ?pdsp_target ; " +
								"pdsp:species ?pdsp_species ; pdsp:source ?pdsp_source ; " +
								"?r1 ?r2 ; ?r3 ?r4 ; ?r5 ?r6 ; ?r7 ?r8; ?r9 ?r10; ?r11 ?r12; ?r13 ?r14; ?r15 ?r16; ?r17 ?r18; " + 
								"?r19 ?r20; ?r21 ?r22; ?r23 ?r24; ?r25 ?r26;} " +
							"}";
			}
			else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method chemicalSimilaritySearch; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest chemicalSubstructureSearch(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("smiles")) {
				sparql="PREFIX cspr: <http://rdf.chemspider.com/#> " +
						"PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>  " +
						"PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
						"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " +
						"SELECT DISTINCT ?csid_uri ?inchi ?inchi_key ?smiles ?synonym " +
						"?med_chem_friendly ?molweight ?chembl_synonyms ?hhd ?alogp ?mw_freebase ?psa " + 
						"?molformula ?molregno ?ro5_violations ?ro3_pass ?hha ?rtb ?pred_water_sol ?exp_water_sol ?target " + 
						"?weight_average ?category ?description ?generic_name ?half_life ?state ?pred_logs ?brand_name ?pred_hydrophob ?exp_hydrophob " +
						"?pdsp_name ?pdsp_displaced ?cas ?pdsp_target ?pdsp_species ?pdsp_source " +
						"?r1 ?r2 ?r3 ?r4 ?r5 ?r6 ?r7 ?r8 ?r9 ?r10 ?r11 ?r12 ?r13 ?r14 ?r15 ?r16 ?r17 ?r18 ?r19 ?r20 ?r21 ?r22 ?r23 ?r24 ?r25 ?r26 " +
							"WHERE {?csid_uri ext:has_substructure_match \""+value+"\" ; cspr:smiles ?smiles; " +
							"cspr:synonym ?synonym ; cspr:inchi ?inchi ; cspr:inchikey ?inchi_key ; " +
							"cspr:exturl ?mapping . ?csid_uri cspr:exturl ?mapping . " +
								"{ ?mapping skos:exactMatch ?chebi . ?c2b2r_ChEMBL chembl:chebi ?chebi ; " +
								"chembl:med_chem_friendly ?med_chem_friendly; " + 
								"chembl:cid ?cid ; chembl:molweight ?molweight;  " +
								"chembl:synonyms ?chembl_synonyms ; chembl:hhd ?hhd ; " +
								"chembl:alogp ?alogp ; chembl:mw_freebase ?mw_freebase ;  " +
								"chembl:psa ?psa ; chembl:molformula ?molformula ;  " +
								"chembl:molregno ?molregno ; chembl:num_ro5_violations ?ro5_violations ; " + 
								"chembl:ro3_pass ?ro3_pass ; chembl:hha ?hha ; chembl:rtb ?rtb ." + 
									"OPTIONAL {?drugbank_uri db:pubchemCompoundURL ?cid . " +
									"?drugbank_uri db:predictedWaterSolubility ?pred_water_sol ; " + 
									"db:experimentalWaterSolubility ?exp_water_sol ;  " +
									"db:molecularWeightAverage ?weight_average ;  " +
									"db:drugCategory ?category_uri ; db:description ?description ; " + 
									"db:genericName ?generic_name ; db:halfLife ?half_life ;  " +
									"db:state ?state ; db:predictedLogs ?pred_logs ; db:brandName ?brand_name ; " +
									"db:target ?target_uri . ?target_uri rdfs:label ?target . ?category_uri rdfs:label ?category} . " + 
									"OPTIONAL {?drugbank_uri db:predictedLogpHydrophobicity ?pred_hydrophob } .  " +
									"OPTIONAL {?drugbank_uri db:experimentalLogpHydrophobicity ?exp_hydrophob } " +
									"} " +
								"UNION { ?mapping pdsp:has_test_ligand_id ?pdsp_name ; " +
								"pdsp:ligand_displaced ?pdsp_displaced; " +
								"pdsp:has_cas_num ?cas ; pdsp:has_receptor_name ?pdsp_target ; " +
								"pdsp:species ?pdsp_species ; pdsp:source ?pdsp_source ; " +
								"?r1 ?r2 ; ?r3 ?r4 ; ?r5 ?r6 ; ?r7 ?r8; ?r9 ?r10; ?r11 ?r12; ?r13 ?r14; ?r15 ?r16; ?r17 ?r18; " + 
								"?r19 ?r20; ?r21 ?r22; ?r23 ?r24; ?r25 ?r26;} " +
							"}";
			}
			else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method chemicalSubstructureSearch; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest chemicalExactStructureSearch(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("smiles")) {
				sparql="PREFIX cspr: <http://rdf.chemspider.com/#> " +
						"PREFIX chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>  " +
						"PREFIX db: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
						"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " +
						"SELECT DISTINCT ?csid_uri ?inchi ?inchi_key ?smiles ?synonym " +
						"?med_chem_friendly ?molweight ?chembl_synonyms ?hhd ?alogp ?mw_freebase ?psa " + 
						"?molformula ?molregno ?ro5_violations ?ro3_pass ?hha ?rtb ?pred_water_sol ?exp_water_sol ?target " + 
						"?weight_average ?category ?description ?generic_name ?half_life ?state ?pred_logs ?brand_name ?pred_hydrophob ?exp_hydrophob " +
						"?pdsp_name ?pdsp_displaced ?cas ?pdsp_target ?pdsp_species ?pdsp_source " +
						"?r1 ?r2 ?r3 ?r4 ?r5 ?r6 ?r7 ?r8 ?r9 ?r10 ?r11 ?r12 ?r13 ?r14 ?r15 ?r16 ?r17 ?r18 ?r19 ?r20 ?r21 ?r22 ?r23 ?r24 ?r25 ?r26 " +
							"WHERE {?csid_uri ext:has_exact_structure_match \""+value+"\" ; cspr:smiles ?smiles; " +
							"cspr:synonym ?synonym ; cspr:inchi ?inchi ; cspr:inchikey ?inchi_key ; " +
							"cspr:exturl ?mapping . ?csid_uri cspr:exturl ?mapping . " +
								"{ ?mapping skos:exactMatch ?chebi . ?c2b2r_ChEMBL chembl:chebi ?chebi ; " +
								"chembl:med_chem_friendly ?med_chem_friendly; " + 
								"chembl:cid ?cid ; chembl:molweight ?molweight;  " +
								"chembl:synonyms ?chembl_synonyms ; chembl:hhd ?hhd ; " +
								"chembl:alogp ?alogp ; chembl:mw_freebase ?mw_freebase ;  " +
								"chembl:psa ?psa ; chembl:molformula ?molformula ;  " +
								"chembl:molregno ?molregno ; chembl:num_ro5_violations ?ro5_violations ; " + 
								"chembl:ro3_pass ?ro3_pass ; chembl:hha ?hha ; chembl:rtb ?rtb ." + 
									"OPTIONAL {?drugbank_uri db:pubchemCompoundURL ?cid . " +
									"?drugbank_uri db:predictedWaterSolubility ?pred_water_sol ; " + 
									"db:experimentalWaterSolubility ?exp_water_sol ;  " +
									"db:molecularWeightAverage ?weight_average ;  " +
									"db:drugCategory ?category_uri ; db:description ?description ; " + 
									"db:genericName ?generic_name ; db:halfLife ?half_life ;  " +
									"db:state ?state ; db:predictedLogs ?pred_logs ; db:brandName ?brand_name ; " +
									"db:target ?target_uri . ?target_uri rdfs:label ?target . ?category_uri rdfs:label ?category} . " + 
									"OPTIONAL {?drugbank_uri db:predictedLogpHydrophobicity ?pred_hydrophob } .  " +
									"OPTIONAL {?drugbank_uri db:experimentalLogpHydrophobicity ?exp_hydrophob } " +
									"} " +
								"UNION { ?mapping pdsp:has_test_ligand_id ?pdsp_name ; " +
								"pdsp:ligand_displaced ?pdsp_displaced; " +
								"pdsp:has_cas_num ?cas ; pdsp:has_receptor_name ?pdsp_target ; " +
								"pdsp:species ?pdsp_species ; pdsp:source ?pdsp_source ; " +
								"?r1 ?r2 ; ?r3 ?r4 ; ?r5 ?r6 ; ?r7 ?r8; ?r9 ?r10; ?r11 ?r12; ?r13 ?r14; ?r15 ?r16; ?r17 ?r18; " + 
								"?r19 ?r20; ?r21 ?r22; ?r23 ?r24; ?r25 ?r26;} " +
							"}";
			}
			else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method chemicalExactStructureSearch; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}

	private SparqlQueryRequest enzymeClassPharmacology(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("class")) {
				sparql= "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX chemspider: <http://rdf.chemspider.com/#> " +
						"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
						"SELECT DISTINCT ?target_name ?target_class ?compound_name ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations " +
							"?std_type ?relation ?std_value ?std_unites ?assay_organism ?drug_name ?drug_type " +
						"WHERE { " +
							"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
								"?class rdfs:subClassOf <http://chem2bio2rdf.org/uniprot/resource/enzyme/"+value+">  ; skos:prefLabel ?target_class . " +
								"?tid c2b2r_chembl:ec_number ?class . ?tid c2b2r_chembl:pref_name ?target_name . " +
								"?assay2target_uri c2b2r_chembl:tid ?tid ; " +
								"c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism . " +
								"?activity_uri farmbio:onAssay ?assay_uri ;  c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ; " +
								"c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ; " +
								"c2b2r_chembl:std_unites ?std_unites . ?csid_uri skos:exactMatch ?compound_uri " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight } " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles } " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi} " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey} " +
							"} " +
							"GRAPH <http://larkc.eu#Fixedcontext> { " +
								"?compound_cw skos:exactMatch ?csid_uri ; skos:prefLabel ?compound_name " +
							"} " +
						"}" ;
			}
			else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
				}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}

	private SparqlQueryRequest proteinPharmacology(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql= "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX chemspider: <http://rdf.chemspider.com/#> " +
						"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
						"SELECT DISTINCT ?target_name ?compound_name ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations " +
							"?std_type ?relation ?std_value ?std_unites ?assay_organism " +/*?drug_name ?drug_type " +*/
						"WHERE { " +
							"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
								"?assay2target_uri c2b2r_chembl:tid   "+value+" ; " +
								"c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism . " +
								"?activity_uri farmbio:onAssay ?assay_uri ;  c2b2r_chembl:c2b2r_chembl_02_activities_molregno ?compound_uri ; " +
								"c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ; " +
								"c2b2r_chembl:std_unites ?std_unites . ?csid_uri skos:exactMatch ?compound_uri " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:molweight ?molweight }" +
								"OPTIONAL { ?compound_uri c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:canonical_smiles ?smiles } " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:inchi ?inchi} " +
								"OPTIONAL { ?compound_uri c2b2r_chembl:inchi_key ?inchiKey} " +
							"} " +
							"GRAPH <http://larkc.eu#Fixedcontext> {" +
								""+value+" skos:prefLabel ?target_name . " +
								"?compound_cw skos:exactMatch ?csid_uri ; skos:prefLabel ?compound_name " +
							"} " +
							/*"OPTIONAL {" +
								"GRAPH <http://linkedlifedata.com/resource/drugbank> {" +
									"?csid_uri skos:exactMatch ?drug_uri " +
									"OPTIONAL {?drug_uri drugbank:target   "+value+" ; drugbank:genericName ?drug_name ; drugbank:drugType ?drugType_uri . " +
										"?drugType_uri rdfs:label ?drug_type " +
									"} " +
								"}" +
							"} " +*/
						"}" ;
			}
			else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
				}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest compoundPharmacology(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql ="PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX chemspider: <http://rdf.chemspider.com/#> " +
						"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX farmbio: <http://rdf.farmbio.uu.se/chembl/onto/#> " +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
						"SELECT DISTINCT ?compound_name ?target_name ?csid_uri ?smiles ?inchi ?inchiKey ?molweight ?num_ro5_violations " +
							"?std_type ?relation ?std_value ?std_unites ?assay_organism " +
							/*"?drug_name ?drug_type " +*/
						"WHERE { " +
							"GRAPH <http://larkc.eu#Fixedcontext> { " +
								value+" skos:prefLabel ?compound_name " +
							"} " +
							"GRAPH <http://www.chemspider.com> { " +
								value+" chemspider:smiles ?smiles ; " +
								"chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey . " +
								"?csid_uri chemspider:inchi ?inchi " +
							"} " +
							"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
								"?activity_uri c2b2r_chembl:c2b2r_chembl_02_activities_molregno  "+value+" ; " +
								"c2b2r_chembl:std_type ?std_type ; c2b2r_chembl:relation ?relation ; c2b2r_chembl:std_value ?std_value ; " +
								"c2b2r_chembl:std_unites ?std_unites ; farmbio:onAssay ?assay_uri . " +
								"?assay2target_uri c2b2r_chembl:assay_id ?assay_uri ; c2b2r_chembl:assay_organism ?assay_organism; " +
								"c2b2r_chembl:tid ?tid . ?tid c2b2r_chembl:pref_name ?target_name " +
								"OPTIONAL { "+value+" c2b2r_chembl:molweight ?molweight } " +
								"OPTIONAL { "+value+" c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
							"} " +
							/*"OPTIONAL { " +
								"GRAPH <http://linkedlifedata.com/resource/drugbank> { " +
									value+" drugbank:drugType ?drugType_uri ; drugbank:genericName ?drug_name . " +
									"?drugType_uri rdfs:label ?drug_type " +
								"} " +
							"} " +*/
						"}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} else { throw new APIException( "Unknown parameter name: \""
						+ name + "\" for method compoundPharmacology; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest proteinInfo(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql= "PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
						"SELECT DISTINCT ?target_name ?target_type ?description ?organism ?keywords ?synonyms " +
							"?cellularLocation ?molecularWeight ?numberOfResidues ?pdbIdPage ?specificFunction ?theoreticalPi " +
						"WHERE { " +
							"GRAPH <http://larkc.eu#Fixedcontext> { " +
								value+" skos:prefLabel ?target_name " +
							"} " +
							"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> { " +
								value+" c2b2r_chembl:keywords ?keywords; c2b2r_chembl:description ?description ; " +
								"c2b2r_chembl:target_type ?target_type ; c2b2r_chembl:organism ?organism ; " +
								"c2b2r_chembl:synonyms ?synonyms " +
							"} " +
							"OPTIONAL {" +
								"GRAPH <http://linkedlifedata.com/resource/drugbank> {" +
									"OPTIONAL { "+value+" drugbank:cellularLocation ?cellularLocation } " +
									"OPTIONAL { "+value+" drugbank:molecularWeight ?molecularWeight } " +
									"OPTIONAL { "+value+" drugbank:numberOfResidues ?numberOfResidues } " +
									"OPTIONAL { "+value+" drugbank:pdbIdPage ?pdbIdPage } " +
									"OPTIONAL { "+value+" drugbank:specificFunction ?specificFunction } " +
									"OPTIONAL { "+value+" drugbank:theoreticalPi ?theoreticalPi } " +
								"} " +
							"} " +
						"}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinInfo; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest compoundInfo(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql ="PREFIX c2b2r_chembl: <http://chem2bio2rdf.org/chembl/resource/> " +
						"PREFIX chemspider: <http://rdf.chemspider.com/#> " +
						"PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> " +
						"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
						"SELECT DISTINCT ?compound_name ?csid_uri ?molformula ?molweight ?inchi ?inchiKey ?smiles " +
							"?alogp ?hha ?hhd ?mw_freebase ?num_ro5_violations ?psa ?rtb " +
							"?meltingPoint ?affectedOrganism ?biotransformation ?description ?indication ?proteinBinding ?toxicity " +
							"WHERE { " +
								"GRAPH <http://larkc.eu#Fixedcontext> {" +
									value+" skos:prefLabel ?compound_name " +
								"} " +
								"GRAPH <http://www.chemspider.com> { " +
									value+" chemspider:smiles ?smiles ; " +
									"chemspider:inchi ?inchi ; chemspider:inchikey ?inchiKey . " +
									"?csid_uri chemspider:inchi ?inchi " +
								"} " +
								"GRAPH <http://www.chem2bio2rdf.org/ChEMBL> {" +
									value+" c2b2r_chembl:molformula ?molformula " +
									"OPTIONAL { "+value+" c2b2r_chembl:alogp ?alogp } " +
									"OPTIONAL { "+value+" c2b2r_chembl:hha ?hha } " +
									"OPTIONAL { "+value+" c2b2r_chembl:hhd ?hhd } " +
									"OPTIONAL { "+value+" c2b2r_chembl:molweight ?molweight } " +
									"OPTIONAL { "+value+" c2b2r_chembl:mw_freebase ?mw_freebase } " +
									"OPTIONAL { "+value+" c2b2r_chembl:num_ro5_violations ?num_ro5_violations } " +
									"OPTIONAL { "+value+" c2b2r_chembl:psa ?psa } " +
									"OPTIONAL { "+value+" c2b2r_chembl:rtb ?rtb } " +
								"} " +
								"OPTIONAL { " +
									"GRAPH <http://linkedlifedata.com/resource/drugbank> {" +
										value+" drugbank:affectedOrganism ?affectedOrganism " +
										"OPTIONAL {"+value+" drugbank:biotransformation ?biotransformation } " +
										"OPTIONAL {"+value+" drugbank:description ?description } " +
										"OPTIONAL {"+value+" drugbank:indication ?indication } " +
										"OPTIONAL {"+value+" drugbank:proteinBinding ?proteinBinding } " +
										"OPTIONAL {"+value+" drugbank:toxicity ?toxicity } " +
										"OPTIONAL {"+value+" drugbank:meltingPoint ?meltingPoint} " +
									"} " +
								"} " +
							"}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method compoundInfo; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest proteinLookup(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("substring")) {
				if (value.length()<4){
					throw new APIException("Substrings should be at least 4 characters long.");
				}
				else {
					sparql= "PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " +
							"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
							"PREFIX cw: <http://www.conceptwiki.org/wiki/concept/> " +
							"PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
							"SELECT DISTINCT ?concept_label ?concept_url ?concept_uuid ?concept_alt_label ?tag_uuid " +
							"WHERE {" +
								"GRAPH <http://larkc.eu#Fixedcontext> { " +
									"?concept_url ext:conceptwiki_search_by_tag "+value+" ; " +
									"ext:semantic_type <http://www.conceptwiki.org/concept/eeaec894-d856-4106-9fa1-662b1dc6c6f1> ; " +
									"skos:prefLabel ?concept_label ; dc:identifier ?concept_uuid ; " +
									"skos:altLabel ?concept_alt_label ; cw:tag ?tag_uuid " +
								"}" +
							"}";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method proteinLookup; "+
						" should be \"substring\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"Substrings should be at least 4 characters long.");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest compoundLookup(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("substring")) {
				if (value.length()<4){
					throw new APIException("Substrings should be at least 4 characters long.");
				}
				else {
					sparql= "PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " +
							"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
							"PREFIX cw: <http://www.conceptwiki.org/wiki/concept/> " +
							"PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
							"SELECT DISTINCT ?concept_label ?concept_url ?concept_uuid ?concept_alt_label ?tag_uuid" +
							"WHERE { " +
								"GRAPH <http://larkc.eu#Fixedcontext> {" +
									"?concept_url ext:conceptwiki_search_by_tag " + value + " ; " +
									"ext:semantic_type <http://www.conceptwiki.org/concept/07a84994-e464-4bbf-812a-a4b96fa3d197> ; " +
									"skos:prefLabel ?concept_label ; dc:identifier ?concept_uuid ; " +
									"skos:altLabel ?concept_alt_label ; cw:tag ?tag_uuid " +
								"} " +
							"}";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method compoundLookup; "+
						" should be \"substring\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"Substrings should be at least 4 characters long.");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest subclasses(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql= "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					" SELECT DISTINCT ?s WHERE { ?s rdfs:subClassOf "+value+" }";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method subclasses; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest superclasses(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql= "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					" SELECT DISTINCT ?o WHERE { "+value+" rdfs:subClassOf ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method superclasses; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}	
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest objects(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("subject")) {
				sparql=" SELECT DISTINCT ?o WHERE { "+value+sparql;
			} else if (name.equals("predicate")) {
				sparql+=" "+value+" ?o }";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method objects; "+
						" should be \"uri\", \"predicate\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}	
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest predicates(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("subject")) {
				sparql=" SELECT DISTINCT ?p WHERE { "+value+" ?p"+sparql;
			} else if (name.equals("uri")) {
				sparql+=" "+value+" }";
			} else if (name.equals("literal")) {
				try {
					Float.parseFloat(value);
					sparql+=" "+value+" }";
				}
				catch (NumberFormatException e){
					sparql+=" \""+value+"\" }";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method predicates; "+
						" should be \"subject\", [\"uri\" OR \"literal\"], \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}	
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest subjects(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("predicate")) {
				sparql=" SELECT DISTINCT ?s WHERE {?s "+value+sparql;
			} else if (name.equals("uri")) {
				sparql+=" "+value+" }";
			} else if (name.equals("literal")) {
				try {
					Float.parseFloat(value);
					sparql+=" "+value+" }";
				}
				catch (NumberFormatException e){
					sparql+=" \""+value+"\" }";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method subjects; "+
						" should be \"predicate\", [\"uri\" OR \"literal\"], \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}		
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest objectsOfPredicate(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?o WHERE {?s "+value+" ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name	+ "\" for method objectsOfPredicate; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}		
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest predicatesWithObject(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?p WHERE {?s ?p "+value+"}";
			} else if (name.equals("literal")) {
				try {
					Float.parseFloat(value);
					sparql=" SELECT DISTINCT ?p WHERE {?s ?p "+value+" }";
				}catch (NumberFormatException e){
					sparql=" SELECT DISTINCT ?p WHERE {?s ?p \""+value+"\"}";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""+ name
						+ "\" for method predicatesWithObject; "+
						" should be [\"uri\" OR \"literal\"], \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}					
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest subjectsWithPredicate(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?s WHERE {?s "+value+" ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method subjectsWithPredicate; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}					
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest predicatesForSubject(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?p WHERE {"+value+" ?p ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method predicatesForSubject; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}					
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest triplesWithObject(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			}else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?s ?p WHERE {?s ?p "+value+"}";
			} else if (name.equals("literal")) {
				try {
					Float.parseFloat(value);
					sparql=" SELECT DISTINCT ?s ?p WHERE {?s ?p "+value+" }";
				}catch (NumberFormatException e){
					sparql=" SELECT DISTINCT ?s ?p WHERE {?s ?p \""+value+"\"}";
				}
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method triplesWithObject; "+
						" should be [\"uri\" OR \"literal\"], \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}					
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest triplesWithPredicate(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?s ?o WHERE {?s "+value+" ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name + "\" for method triplesWithPredicate; "+
						" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}
								
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest triplesWithSubject(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("uri")) {
				sparql=" SELECT DISTINCT ?p ?o WHERE {"+value+" ?p ?o}";
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} 				
			else {
				throw new APIException("Unknown parameter name: \""
						+ name+ "\" for method triplesWithSubject" 
						+" should be \"uri\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\"." +
						"URIs should be contained in <>");
			}			
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}


	private SparqlQueryRequest sparql(String[] parts) throws APIException {
		SparqlQueryRequest qr=new SparqlQueryRequest();
		boolean hasMethod = false;
		String sparql="";
		for (String part : parts) {
			int eq = part.indexOf('=');
			if (eq < 0) {
				logger.warn("Warning: no \'=\' sign in \"" + part
						+ "\" in the URL.");
				continue;
			}
			String rawName = part.substring(0, eq);
			String rawValue = part.substring(eq + 1);
			String name, value;
			try {
				name = URLDecoder.decode(rawName, "UTF-8");
				value = URLDecoder.decode(rawValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new APIException(e.toString());
			}
			logger.debug("Name: "+name+" Value: " + value);
			if (name.equals("method")) {
				if (hasMethod) {
					throw new APIException("More than one value of the \""
						+ name
						+ "\" parameter is being provided (first \""
						+ qr.getQuery() + "\", then \"" + value
						+ "\").");
				}
				hasMethod = true;
			} else if (name.equals("query")) {
				sparql=value;
			} else if (name.equals("default-graph-uri")) {
				qr.addDefaultGraphUri(value);
			} else if (name.equals("named-graph-uri")) {
				qr.addNamedGraphUri(value);
			} else if (name.equals("limit")) {
				if (!sparql.toUpperCase().contains("LIMIT"))
					sparql+=" LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+=" OFFSET "+value;
			} else {
				throw new APIException("Unknown parameter name: \""
					+ name + "\" for method sparql; "+
					" should be \"query\", \"limit\", \"offset\", \"default-graph-uri\" or \"named-graph-uri\".");
			}
		}
		if(!sparql.toUpperCase().contains("LIMIT")){
			sparql+=" LIMIT 100";
		}
		logger.debug("Setting query: "+sparql);
		qr.setQuery(sparql);
		return qr;
	}
	
	/**
	 * This method executes a query.
	 * 
	 * @param entity
	 *            the entity
	 * @return web page
	 * @throws Exception
	 *             if the query is empty
	 */
	@Post
	public Representation executeQueryPost(Representation entity)
			throws Exception {
		logger.debug("HTTP POST called ...");

		// check content type
		logger.debug("ContentType " + entity.getMediaType());
		String contentType = entity.getMediaType().toString();
		if (!contentType.contains("application/x-www-form-urlencoded")) {
			throw new MalformedSparqlQueryException(
					"Unsupported Content-Type in the HTTP request: \""
							+ contentType
							+ "\".  Only application/x-www-form-urlencoded is supported.");
		}

		String query="";
		// Read the body of the request.
		InputStream is = entity.getStream();
		if (is != null){
			StringBuilder reqBody = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			final int capacity = 8192;
			char[] buf = new char[capacity];
			int count;
			while ((count = isr.read(buf, 0, capacity)) > 0) {
				reqBody.append(buf, 0, count);
			}
			isr.close();
			query = reqBody.toString();
		}
		else
		{
			// get the query out of the HTTP POST command.
			Form form = new Form(entity);
			final String q = form.getFirstValue("method");
			if (q == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				throw new Exception("No method variable provided");
			}
			query=q;
		}

		
	
		java.net.URI uri = getRequest().getOriginalRef().toUri();
	
		SparqlQueryRequest queryRequest = parseGetUrl(uri, query);
		logger.debug("Will parse the following query: "+query);
		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof OPSAPIEndpointApp) {
			ep = ((OPSAPIEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());
		logger.debug("Query being processed by endpoint: " + ep.getURI());
		// handle the query
		APIRequestQueryHandler handler = new APIRequestQueryHandler(ex, ep);
		SparqlQueryResult queryResult = handler.handleQuery(queryRequest);

		String xmlResult = APIRequestQueryHandler.xmlToString(
				queryRequest.getQuery(), queryResult.getDocument());

		// set response
		setStatus(Status.SUCCESS_OK);
		Representation rep = new StringRepresentation(xmlResult,
				MediaType.register(queryResult.getContentType(),
						"SPARQL endpoint response"));
		return rep;
	}

	/**
	 * Returns the results of the workflow.
	 * 
	 * @param entity
	 * 
	 * @return the results
	 * @throws Exception
	 */
	@Get
	public Representation executeQueryGet()
			throws Exception {
		logger.debug("HTTP GET called ...");

		Request r = getRequest();
		
		
		
		// get the query out of the HTTP GET command.
		Form form = getQuery(); 
		final String q = form.getFirstValue("method");
		if (q == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			throw new Exception("No method variable provided.");
		}

		java.net.URI uri = new URI(r.getRootRef().toString());
		SparqlQueryRequest queryRequest = parseGetUrl(uri, form.getQueryString());

		// Get the corresponding executor from the LarKC platform.
		Application application = this.getApplication();
		Executor ex = null;
		Endpoint ep = null;
		if (application instanceof OPSAPIEndpointApp) {
			ep = ((OPSAPIEndpointApp) application).getEndpoint();
			ex = ep.getExecutor();
		}
		assert (ep != null);
		assert (ex != null);
		logger.debug("Found executor " + ex.toString());

		// handle the query
		APIRequestQueryHandler handler = new APIRequestQueryHandler(ex, ep);
		SparqlQueryResult queryResult = handler.handleQuery(queryRequest);

		String xmlResult = APIRequestQueryHandler.xmlToString(
				queryRequest.getQuery(), queryResult.getDocument());

		// set response
		setStatus(Status.SUCCESS_OK);
		Representation rep = new StringRepresentation(xmlResult,
				MediaType.register(queryResult.getContentType(),
						"SPARQL endpoint response"));
		return rep;
	}
	
}