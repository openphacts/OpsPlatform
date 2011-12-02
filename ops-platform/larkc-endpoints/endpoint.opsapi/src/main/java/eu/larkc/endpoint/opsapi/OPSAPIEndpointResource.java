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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

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
import eu.larkc.endpoint.sparql.exceptions.SparqlException;
import eu.larkc.endpoint.sparql.exceptions.SparqlQueryRefusedException;
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
		} else if (method.equals("chemicalExactStructureSearch")){
			qr=chemicalExactStructureSearch(parts);
		} else if (method.equals("chemicalSubstructureSearch")){
			qr=chemicalSubstructureSearch(parts);
		} else if (method.equals("chemicalSimilaritySearch")){
			qr=chemicalSimilaritySearch(parts);
		} else {
			throw new APIException("Unknown method name: "+method+". Use one of: " );
		}
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
				sparql= "PREFIX cspr: <http://rdf.chemspider.com/#> " +
					"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#> " +
					" SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit" +
					" WHERE {" +
						"?csid_uri ext:has_similar \""+value+"\"" +
							" OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ;" +
							"cspr:inchikey ?compound_inchi_key;" +
							"cspr:synonym ?compound_name ;" +
							"cspr:smiles ?compound_smiles }" +
							" OPTIONAL{?csid_uri cspr:exturl ?exturl ." +
							"?exturl pdsp:has_receptor_name ?receptor_name ;" +
							"pdsp:has_test_ligand_id ?test_ligand_id ;" +
							"pdsp:has_test_ligand_name ?test_ligand_name;" +
							"pdsp:has_cas_num ?cas_no ;" +
							"pdsp:has_unigene_id ?unigene_id ;" +
							"pdsp:ligand_displaced ?ligand_displaced ;" +
							"pdsp:species ?species ;" +
							"pdsp:source ?source ;" +
							"pdsp:has_ki_value ?ki_entry ." +
							"?ki_entry pdsp:unit ?ki_unit ;" +
							"rdf:value ?ki_value}" +
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
				sparql= "PREFIX cspr: <http://rdf.chemspider.com/#>" +
					"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>" +
					" SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit" +
					" WHERE {" +
						"?csid_uri ext:has_substructure_match \""+value+"\"" +
							" OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ;" +
							"cspr:inchikey ?compound_inchi_key;" +
							"cspr:synonym ?compound_name ;" +
							"cspr:smiles ?compound_smiles }" +
							" OPTIONAL{?csid_uri cspr:exturl ?exturl ." +
							"?exturl pdsp:has_receptor_name ?receptor_name ;" +
							"pdsp:has_test_ligand_id ?test_ligand_id ;" +
							"pdsp:has_test_ligand_name ?test_ligand_name;" +
							"pdsp:has_cas_num ?cas_no ;" +
							"pdsp:has_unigene_id ?unigene_id ;" +
							"pdsp:ligand_displaced ?ligand_displaced ;" +
							"pdsp:species ?species ;" +
							"pdsp:source ?source ;" +
							"pdsp:has_ki_value ?ki_entry ." +
							"?ki_entry pdsp:unit ?ki_unit ;" +
							"rdf:value ?ki_value}" +
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
				sparql= "PREFIX cspr: <http://rdf.chemspider.com/#>" +
					"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"PREFIX ext: <http://wiki.openphacts.org/index.php/ext_function#>" +
					" SELECT ?csid_uri ?compound_inchi ?compound_inchi_key ?compound_name ?compound_smiles ?receptor_name ?test_ligand_name ?cas_no ?unigene_id ?ligand_displaced ?species ?source ?ki_value ?ki_unit" +
					" WHERE {" +
						"?csid_uri ext:has_exact_structure_match \""+value+"\"" +
							" OPTIONAL { ?csid_uri cspr:inchi ?compound_inchi ;" +
							"cspr:inchikey ?compound_inchi_key;" +
							"cspr:synonym ?compound_name ;" +
							"cspr:smiles ?compound_smiles }" +
							" OPTIONAL{?csid_uri cspr:exturl ?exturl ." +
							"?exturl pdsp:has_receptor_name ?receptor_name ;" +
							"pdsp:has_test_ligand_id ?test_ligand_id ;" +
							"pdsp:has_test_ligand_name ?test_ligand_name;" +
							"pdsp:has_cas_num ?cas_no ;" +
							"pdsp:has_unigene_id ?unigene_id ;" +
							"pdsp:ligand_displaced ?ligand_displaced ;" +
							"pdsp:species ?species ;" +
							"pdsp:source ?source ;" +
							"pdsp:has_ki_value ?ki_entry ." +
							"?ki_entry pdsp:unit ?ki_unit ;" +
							"rdf:value ?ki_value}" +
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
				if (value.contains("brenda")){
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						" SELECT ?protein_name ?uniprot_entry ?species ?ic50 ?inhibitor ?pubmed_id ?ki_value ?ki_unit " +
						" WHERE {" +
							"{"+value+" brenda:recommended_name ?protein_name ;" +
							"brenda:has_ec_number ?uniprot_entry ;" +
							"brenda:species ?species_code ." +
							"?species_code rdfs:label ?species" +
							" OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 }" +
							"} UNION {"+value+" pdsp:has_receptor_name ?protein_name ;" +
							"pdsp:species ?species ;" +
							"pdsp:has_test_ligand_name ?inhibitor ." +
							" OPTIONAL {?protein_uri pdsp:pubmed_id ?pubmed_id ;" +
								"pdsp:has_ki_value ?ki_entry ." +
								"?ki_entry rdf:value ?ki_value ;" +
								"pdsp:unit ?ki_unit}" +
							"}" +
						"}";
				}else {
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						" SELECT ?protein_name ?species ?inhibitor ?pubmed_id ?ki_value ?ki_unit ?ic50 ?uniprot_entry " +
						" WHERE {" +
							"{"+value+" brenda:recommended_name ?protein_name ;" +
							"brenda:has_ec_number ?uniprot_entry ;" +
							"brenda:species ?species_code ." +
							"?species_code rdfs:label ?species" +
							" OPTIONAL {?ic50experiment brenda:has_inhibitor ?inhibitor ; brenda:has_ic50_value_of ?ic50 }" +
							"} UNION {"+value+" pdsp:has_receptor_name ?protein_name  ;" +
							"pdsp:species ?species ;" +
							"pdsp:has_test_ligand_name ?inhibitor ." +
								" OPTIONAL {?protein_uri pdsp:pubmed_id ?pubmed_id ;" +
								"pdsp:has_ki_value ?ki_entry ." +
								"?ki_entry rdf:value ?ki_value ;" +
								"pdsp:unit ?ki_unit}" +
							"}" +
						"}";
					}
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
				if (value.contains("brenda")){
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						" SELECT ?compound_name ?species ?ic50 ?target_name ?ki_value ?ki_unit ?pubmed_id" +
						" WHERE {" +
							"{"+value+" brenda:has_inhibitor ?compound_name ;" +
							"brenda:has_ic50_value_of ?ic50 ;" +
							"brenda:species ?species_uri ." +
							"?species_uri rdfs:label ?species }" +
						" UNION {"+value+" pdsp:has_test_ligand_name ?compound_name ;" +
							"pdsp:has_receptor_name ?target_name;" +
							"pdsp:species ?species ;" +
							"pdsp:has_ki_value ?ki_entry ." +
							"?ki_entry rdf:value ?ki_value ;" +
							"pdsp:unit ?ki_unit " +
							" OPTIONAL {"+value+" pdsp:pubmed_id ?pubmed_id }}" +
						"}";
				}else {
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						" SELECT ?compound_name ?species ?target_name ?ki_value ?ki_unit ?pubmed_id ?ic50" +
						" WHERE {" +
							"{"+value+" brenda:has_inhibitor ?compound_name ;" +
							"brenda:has_ic50_value_of ?ic50 ;" +
							"brenda:species ?species_uri ." +
							"?species_uri rdfs:label ?species }" +
						" UNION {"+value+" pdsp:has_test_ligand_name ?compound_name ;" +
							"pdsp:has_receptor_name ?target_name;" +
							"pdsp:species ?species ;" +
							"pdsp:has_ki_value ?ki_entry ." +
							"?ki_entry rdf:value ?ki_value ;" +
							"pdsp:unit ?ki_unit " +
							" OPTIONAL {"+value+" pdsp:pubmed_id ?pubmed_id }}" +
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
				if (value.contains("brenda")){
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX cspr: <http://rdf.chemspider.com/#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						" SELECT ?protein_name ?species ?systematic_name ?uniprot_id ?unigene_id ?nsc ?pubmed_id " +
							" WHERE {" +
								"{"+value+" brenda:recommended_name ?protein_name ." +
								"?protein_url brenda:recommended_name ?protein_name ;" +
								"brenda:systematic_name ?systematic_name ;" +
								"brenda:species ?species_uri ;" +
								"brenda:has_ec_number ?uniprot_id ." +
								"?species_uri rdfs:label ?species }" +
							" UNION {"+value+" pdsp:has_receptor_name ?protein_name  ." +
								"?protein_url pdsp:has_receptor_name ?protein_name" +
								" OPTIONAL {?protein_url pdsp:has_unigene_id ?unigene_id ; pdsp:has_nsc_number ?nsc}" +
								" OPTIONAL {?protein_url pdsp:pubmed_id ?pubmed_id }" +
								" OPTIONAL {?protein_url pdsp:species ?species}" +
							"}" +
						"}";
				}else {
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX cspr: <http://rdf.chemspider.com/#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						" SELECT ?protein_name ?unigene_id ?nsc ?pubmed_id ?species ?systematic_name ?uniprot_id " +
							" WHERE {" +
								"{"+value+" brenda:recommended_name ?protein_name ." +
								"?protein_url brenda:recommended_name ?protein_name ;" +
								"brenda:systematic_name ?systematic_name ;" +
								"brenda:species ?species_uri ;" +
								"brenda:has_ec_number ?uniprot_id ." +
								"?species_uri rdfs:label ?species }" +
							" UNION {"+value+" pdsp:has_receptor_name ?protein_name  ." +
								"?protein_url pdsp:has_receptor_name ?protein_name" +
								" OPTIONAL {?protein_url pdsp:has_unigene_id ?unigene_id ; pdsp:has_nsc_number ?nsc}" +
								" OPTIONAL {?protein_url pdsp:pubmed_id ?pubmed_id }" +
								" OPTIONAL {?protein_url pdsp:species ?species}" +
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
				if (value.contains("brenda")){
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/> " +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
						"PREFIX cspr: <http://rdf.chemspider.com/#> " +
						"PREFIX rdfs: <http://w3.org/2000/01/rdf-schema#> " +
						" SELECT ?compound_name ?ic50 ?species ?csid_uri ?inchi ?inchi_key ?smiles ?synonym WHERE {" +
							"{"+value+" brenda:has_inhibitor ?compound_name " +
								" OPTIONAL {"+value+" brenda:has_ic50_value_of ?ic50}" +
								" OPTIONAL {"+value+" brenda:species ?species_uri . ?species_uri rdfs:label ?species } }" +
							" UNION {"+value+" pdsp:has_test_ligand_name ?compound_name  " +
								" OPTIONAL {?csid_uri cspr:exturl "+value+" } " +
								" OPTIONAL {?csid_uri cspr:smiles ?smiles}" +
								" OPTIONAL {?csid_uri cspr:synonym ?synonym} " +
								" OPTIONAL {?csid_uri cspr:inchi ?inchi ; cspr:inchikey ?inchi_key}}" +
						"}";
				}else {
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/> " +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#> " +
						"PREFIX cspr: <http://rdf.chemspider.com/#> " +
						"PREFIX rdfs: <http://w3.org/2000/01/rdf-schema#> " +
						" SELECT ?compound_name ?csid_uri ?inchi ?inchi_key ?smiles ?synonym ?ic50 ?species WHERE {" +
							"{"+value+" brenda:has_inhibitor ?compound_name " +
								" OPTIONAL {"+value+" brenda:has_ic50_value_of ?ic50}" +
								" OPTIONAL {"+value+" brenda:species ?species_uri . ?species_uri rdfs:label ?species } }" +
							" UNION {"+value+" pdsp:has_test_ligand_name ?compound_name  " +
								" OPTIONAL {?csid_uri cspr:exturl "+value+" } " +
								" OPTIONAL {?csid_uri cspr:smiles ?smiles}" +
								" OPTIONAL {?csid_uri cspr:synonym ?synonym} " +
								" OPTIONAL {?csid_uri cspr:inchi ?inchi ; cspr:inchikey ?inchi_key}}" +
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
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						" SELECT DISTINCT ?protein_uri ?protein_name WHERE {" +
							"{ ?protein_uri brenda:recommended_name ?protein_name } " +
							" UNION { ?protein_uri pdsp:has_receptor_name ?protein_name } " +
			 				"FILTER regex(?protein_name, \""+value+"\", \"i\") }";
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
					sparql= "PREFIX brenda: <http://brenda-enzymes.info/>" +
						"PREFIX pdsp: <http://wiki.openphacts.org/index.php/PDSP_DB#>" +
						"PREFIX cspr: <http://rdf.chemspider.com/#> " +
						" SELECT DISTINCT ?compound_uri ?compound_name WHERE {" +
							"{ ?compound_uri brenda:has_inhibitor ?compound_name } " +
							" UNION { ?compund_uri pdsp:has_test_ligand_name ?compound_name } " +
							" UNION { ?csid cspr:exturl ?compound_uri . ?csid cspr:synonym ?compound_name}" +
			 				"FILTER regex(?compound_name, \""+value+"\", \"i\") }";
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
				sparql+="LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+="OFFSET "+value;
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
				sparql+="LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+="OFFSET "+value;
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
				sparql+="LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+="OFFSET "+value;
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
				sparql+="LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+="OFFSET "+value;
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
				sparql+="LIMIT "+value;
			} else if (name.equals("offset")) {
				sparql+="OFFSET "+value;
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