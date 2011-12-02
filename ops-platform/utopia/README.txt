Using Utopia and and Larkc

Requires larkc (or any other sparql endpoint that returns xml)

For the most recent utopia please email Steve Pettifer
He will need your operating system, version, 32/64 byte and stuff like that

After installing and running Utopida once find the Utopia folder in your home directory.
%Utopia_path something like User/you/Utopia (no need to set environment variable)

copy the *.py files from ops-platform/utopia to %Utopia_path/plugins/python/

OpsUtils.py method def runQuery(query):
Has a line with a hard coded sparql endpoint.
Locathost has NOT been tested with success
Replace this with your sparql endpoint
sparql = SPARQLWrapper('http://localhost:8183/sparql')

note: The log file for the plugins is in %Utopia_path/logs/python.log
    (you may have to close Utopia to get the last lines held in buffer)
   
Pubmed tool has been tested with documents
20471260 and 17107798

text Larktest2.py lookup has been tested with
Stub test for Utopia.pdf 
created from "Stub test for Utopia.docx
I fear the current settup of pre annotating every word will be TOO SLOW for a full size docuemnt.


Current state is at best a proof of concept.
   