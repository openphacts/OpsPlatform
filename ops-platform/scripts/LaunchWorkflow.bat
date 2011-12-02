ECHO Under development curl --data "workflow=%WORKFLOW%" http://localhost:8182/rdf/workflows gives an error

# LARKC_PATH= # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# OPS_PATH= # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
#!/bin/bash
set WORKFLOW=%LARKC_PATH%\openphacts\ops-platform\larkc-workflow\simpleworkflow.ttl

set ENDPOINT_ID="urn:eu.larkc.endpoint.sparql.ep1"
set WID=`curl  --data "workflow=%WORKFLOW%" http://localhost:8182/rdf/workflows`
curl  --data "workflow=%WORKFLOW%" http://localhost:8182/rdf/workflows
rem curl "http://localhost:8182/rdf/workflows/$WID/endpoint?urn=%ENDPOINT_ID%"
echo

ECHO Under development curl --data "workflow=%WORKFLOW%" http://localhost:8182/rdf/workflows gives an error
