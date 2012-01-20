#
# Script to extract the ConceptWiki to drugbank linkset 
# 
cw_cs_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/chemspider.map
pdsp_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/PDSP_URIs.ttl
unique_cs_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/CSs_in_PDSP.tmp
sed_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/sed_PDSP.sh
cw_c2b2r_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/conceptwiki_c2b2rChEMBL.map
outputfile=$OPS_PATH/openphacts/linksets/conceptwiki_PDSP.map
linkpredicate="skos:exactMatch"
today=$(date "+%Y-%m-%d")

#Create unique_cs_file
#grep exturl PDSP_URIs.ttl | sed 's/ [[:print:]]*//' | sort |uniq > $unique_cs_file

#Read unique_cs_file line by line
#Record corresponding conceptwiki in sed_file
#echo "sed -n '" > $sed_file
#while read cs 
#do
 #cs=`echo $cs| tr -d '\r' | tr -d '\n'`
 #echo $cs
 #cw=`grep -m 1 "$cs" chemspider.map | sed 's/ [[:print:]]*$//'`
 #if [ "$cw" != "" ]; then
 #  echo s,$cs,$cw,p >> $sed_file
 #fi
#done < $unique_cs_file
#grep exturl PDSP_URIs.ttl > tmp

#Write headers
echo "# This file has been generated using the script " > $outputfile
echo "# \$OPS_PATH/openphacts/datasets/OPS-DS-TTL/conceptwiki_drugbank_linkset_extraction.sh" >> $outputfile
echo "" >> $outputfile

echo "@prefix : <#> ." >> $outputfile
echo "@prefix void: <http://rdfs.org/ns/void#> ." >> $outputfile
echo "@prefix skos: <http://www.w3.org/2004/02/skos/core#> ." >> $outputfile
echo "@prefix dcterms: <http://purl.org/dc/terms/> ." >> $outputfile
echo "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ." >> $outputfile
echo "" >> $outputfile

echo ":conceptwiki a void:Dataset  ." >> $outputfile
echo ":pdsp a void:Dataset  ." >> $outputfile
echo ":conceptwiki_pdsp a void:Linkset  ." >> $outputfile
echo ":conceptwiki_pdsp void:subjectsTarget :conceptwiki ." >> $outputfile
echo ":conceptwiki_pdsp void:objectsTarget :pdsp ." >> $outputfile
echo ":conceptwiki_pdsp void:subset :conceptwiki ." >> $outputfile
echo ":conceptwiki_pdsp void:linkPredicate $linkpredicate ." >> $outputfile
echo ":conceptwiki_pdsp dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

sed -n -f sed_PDSP.sh tmp | sed 's/cspr:exturl/skos:exactMatch/' >> $outputfile
rm tmp
