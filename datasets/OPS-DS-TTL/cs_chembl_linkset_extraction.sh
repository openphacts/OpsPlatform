#
# Script to extract the ChemSpider to ChEMBL linkset from the 
# OPS-DS-TTL/ChEMBL.ttl datafile
#

inputfile=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/ChEMBL.ttl
outputfile=$OPS_PATH/openphacts/linksets/cs_chembl.ttl
linkpredicate="cspr:exturl"
today=$(date "+%Y-%m-%d")

echo "# This file has been generated using the script " > $outputfile 
echo "# \$OPS_PATH/openphacts/datasets/OPS-DS-TTL/cs_chembl_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "@prefix cspr: <http://rdf.chemspider.com/#> ." >> $outputfile
echo "" >> $outputfile

echo ":chemspider a void:Dataset  ." >> $outputfile
echo ":chembl a void:Dataset  ." >> $outputfile
echo ":chemspider_chembl a void:Linkset  ." >> $outputfile
echo ":chemspider_chembl void:subjectsTarget :chemspider ." >> $outputfile
echo ":chemspider_chembl void:objectsTarget :chembl ." >> $outputfile
echo ":chemspider_chembl void:subset :chemspider ." >> $outputfile
echo ":chemspider_chembl void:linkPredicate $linkpredicate ." >> $outputfile
echo ":chemspider_chembl dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

grep -e $linkpredicate $inputfile >> $outputfile
