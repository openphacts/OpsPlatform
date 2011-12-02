#
# Script to extract the brenda to uniprot linkset from the brenda.ttl datafile
#

inputfile=$OPS_PATH/openphacts/datasets/brenda/brenda.ttl
outputfile=$OPS_PATH/openphacts/linksets/brenda_uniprot.ttl
linkpredicate="http://brenda-enzymes.info/has_ec_number"
today=$(date "+%Y-%m-%d")

echo "# This file has been generated using the script " > $outputfile 
echo "# \$OPS_PATH/openphacts/datasets/brenda/brenda_uniprot_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "" >> $outputfile

echo ":brenda a void:Dataset  ." >> $outputfile
echo ":uniprot a void:Dataset  ." >> $outputfile
echo ":brenda_uniprot a void:Linkset  ." >> $outputfile
echo ":brenda_uniprot void:subjectsTarget :brenda ." >> $outputfile
echo ":brenda_uniprot void:objectsTarget :uniprot ." >> $outputfile
echo ":brenda_uniprot void:subset :brenda ." >> $outputfile
echo ":brenda_uniprot void:linkPredicate $linkpredicate ." >> $outputfile
echo ":brenda_uniprot dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

grep -e $linkpredicate $inputfile >> $outputfile
