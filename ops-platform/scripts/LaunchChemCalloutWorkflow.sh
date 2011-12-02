# OPS_PATH= # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
#!/bin/bash
echo $OPS_PATH
WORKFLOW=`cat $OPS_PATH/openphacts/ops-platform/larkc-workflow/simplechemcalloutworkflow.ttl`
ENDPOINT_ID="urn:eu.larkc.endpoint.sparql.ep1"
echo ENDPOINT_ID: $ENDPOINT_ID
WID=`curl  --data "workflow=$WORKFLOW" http://localhost:8182/rdf/workflows`
curl "http://localhost:8182/rdf/workflows/$WID/endpoint?urn=$ENDPOINT_ID"
echo
