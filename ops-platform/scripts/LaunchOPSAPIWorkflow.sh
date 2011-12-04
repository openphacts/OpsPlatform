# LARKC_PATH= # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# OPS_PATH= # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
#!/bin/bash
WORKFLOW=`cat $OPS_PATH/openphacts/ops-platform/larkc-workflow/OPS_API_workflow.ttl`
HTML=`curl  --data "workflow=$WORKFLOW" http://localhost:8182/rdf/workflows`
WID=`echo $HTML | head -n1 |sed 's/[[:print:]]*<b>\([[:print:]]*\)<\/b> succ[[:print:]]*/\1/'`
echo "WID: "$WID
echo $HTML | sed "s/[[:print:]]*$WID<\/td><td>ep (\([.:\/a-z0-9]*\)[[:print:]]*/Endpoint URL: \1/"
