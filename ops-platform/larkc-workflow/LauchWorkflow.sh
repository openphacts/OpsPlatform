#!/bin/bash
WORKFLOW=`cat simpleworkflow.ttl`
ENDPOINT_ID="urn:eu.larkc.endpoint.sparql.ep1"
WID=`curl  --data "workflow=$WORKFLOW" http://localhost:8182/rdf/workflows`
curl "http://localhost:8182/rdf/workflows/$WID/endpoint?urn=$ENDPOINT_ID"
echo
