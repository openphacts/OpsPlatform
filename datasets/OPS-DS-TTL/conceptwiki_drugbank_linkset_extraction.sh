#
# Script to extract the ConceptWiki to drugbank linkset 
# 
c2b2r_cid_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/CIDs_in_ChEMBL.tmp
c2b2r_db_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/c2b2rchembl_drugbank.tmp
db_cid_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/DB_to_PC_URLs.ttl
cw_c2b2r_file=$OPS_PATH/openphacts/datasets/OPS-DS-TTL/conceptwiki_c2b2rChEMBL.map
outputfile=$OPS_PATH/openphacts/linksets/conceptwiki_drugbank.map
linkpredicate="skos:exactMatch"
today=$(date "+%Y-%m-%d")

#Create c2b2rchembl_drugbank.tmp
#while read line
#do
# c2b2r=`echo $line|sed 's/ [[:print:]]*$//'`
# cid=`echo $line|sed -e 's/ \.$//' -e 's/^[[:print:]]* //'`
 #echo $c2b2r
 #echo $cid
# db=`grep -m 1 "$cid" $db_cid_file | sed 's/ [[:print:]]*$//'`
 #echo $db
# if [ "$db" != "" ]; then
#  echo $c2b2r $db >> $c2b2r_db_file
# fi
#done < $c2b2r_cid_file

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
echo ":drugbank a void:Dataset  ." >> $outputfile
echo ":conceptwiki_drugbank a void:Linkset  ." >> $outputfile
echo ":conceptwiki_drugbank void:subjectsTarget :conceptwiki ." >> $outputfile
echo ":conceptwiki_drugbank void:objectsTarget :drugbank ." >> $outputfile
echo ":conceptwiki_drugbank void:subset :conceptwiki ." >> $outputfile
echo ":conceptwiki_drugbank void:linkPredicate $linkpredicate ." >> $outputfile
echo ":conceptwiki_drugbank dcterms:created \"$today\"^^xsd:date ." >> $outputfile
echo "" >> $outputfile

#Delete preamble
sed '1,18d' $cw_c2b2r_file > tmp

#Read cw_c2b2r_file line by line
while read line
do
 cw=`echo $line|sed 's/ [[:print:]]*$//'| tr -d '\r' | tr -d '\n'`
 c2b2r=`echo $line| sed 's/ \.$//' |sed 's/[[:print:]]* //' | tr -d '\r' | tr -d '\n'`
 #echo $cw
 #echo $c2b2r
 #Find the corresponding drugbank URL
 db=`grep -m 1 "$c2b2r" $c2b2r_db_file | sed 's/[[:print:]]* //' | tr -d '\r' | tr -d '\n'`
 #echo $db
 if [ "$db" != "" ]; then
  echo $cw skos:exactmatch $db . >> $outputfile
 fi
done < tmp
rm tmp
