In this directory you'll find the sparql query for Question 15 and the corresponding results. The ruby files (.rb) are kept for reference purposes as they helped to develop these queries. 

There are two variants of the queries:

1) Find all oxidoreductase inhibitors with NAD(+) or NADP(+) as acceptors 
active < 100nM in human and mice.

This query can be found in question15-sparql.txt
Results can be found in question15-results.txt

This query can be run without inference enabled.

2) Find all oxidoreductase inhibitors active < 100nM in human and mice.

This query can be found in question15-sparql-full.txt
Results can be found in question15-results-full.txt

This query needs to be run with inference turned on in larkc (rdfs to be specific). That is running the script RunLarKCInf.sh