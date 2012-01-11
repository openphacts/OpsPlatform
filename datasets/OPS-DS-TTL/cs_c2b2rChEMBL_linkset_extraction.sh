#
# Script to extract the ChemSpider to chem2bio2rdf ChEMBL linkset 
#
c2b2r_ChEMBLfile=$OPS_PATH/openphacts/datasets/chem2bio2rdf/chembl.nt
chebi_mappingfile=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/chembl2c2b2rchembl.ttl
cs2chemblfile=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/ChEMBL_URIs.ttl
outputfile=$OPS_PATH/openphacts/linksets/cs_c2b2rChEMBL.map
linkpredicate="skos:exactMatch"
today=$(date "+%Y-%m-%d")

#Create file with all unique CHEBI URIs in chem2bio2rdf/chembl.nt 
#Format: <c2b2rChEMBL URI> <CHEBI URI>
grep CHEBI $c2b2r_ChEMBLfile | sed 's,<http://chem2bio2rdf.org/chembl/resource/chebi> ,,' > CHEBIs_in_ChEMBL.uris

#Write headers
echo "# This file has been generated using the script " > $outputfile
echo "# \$OPS_PATH/openphacts/datasets/OPS-DS-TTL/cs_chembl_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "@prefix skos: <http://www.w3.org/2004/02/skos/core#> ." >> $outputfile
echo "@prefix dcterms: <http://purl.org/dc/terms/> ." >> $outputfile
echo "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ." >> $outputfile
echo "" >> $outputfile

echo ":chemspider a void:Dataset  ." >> $outputfile
echo ":c2b2r_chembl a void:Dataset  ." >> $outputfile
echo ":chemspider_c2b2rchembl a void:Linkset  ." >> $outputfile
echo ":chemspider_c2b2rchembl void:subjectsTarget :chemspider ." >> $outputfile
echo ":chemspider_c2b2rchembl void:objectsTarget :c2b2r_chembl ." >> $outputfile
echo ":chemspider_c2b2rchembl void:subset :chemspider ." >> $outputfile
echo ":chemspider_c2b2rchembl void:linkPredicate $linkpredicate ." >> $outputfile
echo ":chemspider_c2b2rchembl dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile


#Read cs2chemblfile line by line
while read line
do
 cs=`echo $line|sed 's/ [[:print:]]*//'| tr -d '\r' | tr -d '\n'`
 chembl=`echo $line|sed 's/ \.//'|sed 's/[[:print:]]* //' | tr -d '\r' | tr -d '\n'`
 #echo $cs
 #echo $chembl 
 #Generate the CHEBI that corresponds to the chembl URL
 chebi=`echo $chembl | sed 's,<https://www.ebi.ac.uk/chembldb/index.php/compound/inspect/,<http://chem2bio2rdf.org/chebi/resource/chebi/CHEBI:,'| tr -d '\r' | tr -d '\n'`
 #echo $chebi
 #Find the corresponding chem2bio2rdf URL
 c2b2r_chembl=`grep -m 1 "$chebi" CHEBIs_in_ChEMBL.uris | sed 's/ [[:print:]]*$//' | tr -d '\r' | tr -d '\n'`
 #echo $c2b2r_chembl
 if [ "$c2b2r_chembl" != "" ]; then
  echo $cs skos:exactMatch $c2b2r_chembl . >> $outputfile
 fi
done < $cs2chemblfile

rm CHEBIs_in_ChEMBL.uris
