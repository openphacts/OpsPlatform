# LARKC_PATH= # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# OPS_PATH= # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
#!/bin/bash
WORKFLOW=`cat $LARKC_PATH/openphacts/ops-platform/larkc-workflow/datasetsworkflow.ttl`
ENDPOINT_ID="urn:eu.larkc.endpoint.sparql.ep1"
WID=`curl  --data "workflow=$WORKFLOW" http://localhost:8182/rdf/workflows`
curl "http://localhost:8182/rdf/workflows/$WID/endpoint?urn=$ENDPOINT_ID"
echo
