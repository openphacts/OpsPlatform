#
# Script to extract the ChemSpider to PDSP linkset from the 
# OPS-DS-TTL/PDSP_URIs.ttl datafile
#

inputfile=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/PDSP_URIs.ttl
outputfile=$OPS_PATH/openphacts/linksets/cs_pdsp.ttl
linkpredicate="cspr:exturl"
today=$(date "+%Y-%m-%d")

echo "# This file has been generated using the script " > $outputfile 
echo "# \$OPS_PATH/openphacts/datasets/OPS-DS-TTL/cs_pdsp_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "@prefix cspr: <http://rdf.chemspider.com/#> ." >> $outputfile
echo "" >> $outputfile

echo ":chemspider a void:Dataset  ." >> $outputfile
echo ":pdsp a void:Dataset  ." >> $outputfile
echo ":chemspider_pdsp a void:Linkset  ." >> $outputfile
echo ":chemspider_pdsp void:subjectsTarget :chemspider ." >> $outputfile
echo ":chemspider_pdsp void:objectsTarget :pdsp ." >> $outputfile
echo ":chemspider_pdsp void:subset :chemspider ." >> $outputfile
echo ":chemspider_pdsp void:linkPredicate $linkpredicate ." >> $outputfile
echo ":chemspider_pdsp dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

grep -e $linkpredicate $inputfile >> $outputfile
