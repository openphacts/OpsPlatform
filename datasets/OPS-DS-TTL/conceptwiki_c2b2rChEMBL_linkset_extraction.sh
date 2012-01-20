#
# Script to extract the ConceptWiki to chem2bio2rdf ChEMBL linkset 
# 
cw_cs_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/chemspider.map
cs_c2b2r_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/cs_c2b2rChEMBL.map
outputfile=$OPS_PATH/openphacts/linksets/conceptwiki_c2b2rChEMBL.map
linkpredicate="skos:exactMatch"
today=$(date "+%Y-%m-%d")

#Write headers
echo "# This file has been generated using the script " > $outputfile
echo "# \$OPS_PATH/openphacts/datasets/OPS-DS-TTL/conceptwiki_c2b2rCHeMBL_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "@prefix skos: <http://www.w3.org/2004/02/skos/core#> ." >> $outputfile
echo "@prefix dcterms: <http://purl.org/dc/terms/> ." >> $outputfile
echo "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ." >> $outputfile
echo "" >> $outputfile

echo ":conceptwiki a void:Dataset  ." >> $outputfile
echo ":c2b2r_chembl a void:Dataset  ." >> $outputfile
echo ":conceptwiki_c2b2rchembl a void:Linkset  ." >> $outputfile
echo ":conceptwiki_c2b2rchembl void:subjectsTarget :conceptwiki ." >> $outputfile
echo ":conceptwiki_c2b2rchembl void:objectsTarget :c2b2r_chembl ." >> $outputfile
echo ":conceptwiki_c2b2rchembl void:subset :conceptwiki ." >> $outputfile
echo ":conceptwiki_c2b2rchembl void:linkPredicate $linkpredicate ." >> $outputfile
echo ":conceptwiki_c2b2rchembl dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

#Delete preamble
sed '1,19d' $cw_cs_file > tmp

#Read cw_cs_file line by line
while read line
do
 cw=`echo $line|sed 's/ [[:print:]]*//'| tr -d '\r' | tr -d '\n'`
 cs=`echo $line|sed 's/ \.//'|sed 's/[[:print:]]* //' | tr -d '\r' | tr -d '\n' | sed 's/\.$//'`
 #echo $cw
 #echo $cs 
 #Find the corresponding chem2bio2rdf URL
 c2b2r_chembl=`grep -m 1 "$cs" $cs_c2b2r_file | sed 's/[[:print:]]*match //' | tr -d '\r' | tr -d '\n'`
 #echo $c2b2r_chembl
 if [ "$c2b2r_chembl" != "" ]; then
  echo $cw skos:exactMatch $c2b2r_chembl >> $outputfile
 fi
done < tmp
rm tmp
