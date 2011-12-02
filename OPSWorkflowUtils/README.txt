This jar contains a utility to pass a workflow file name to a LarKC server and return the SPARQL 
endpoint to that workflow. 

usage: OPSClient [OPTIONS] WORKFLOW
 -larkc <arg>   base URL for LarKC server
 -test          run a SPARQL test query

If no larkc argument is specified a default of "http://localhost:8182" is used. 
The reference to OPSClient above is only for when the class is used directly, ignore it when
using an executable jar. Simply run it as: 

java -jar OPSWorkflowUtils.jar WORKFLOW_FILENAME

