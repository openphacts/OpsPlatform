#!/bin/bash
export MAVEN_OPTS=-Xmx512m
# LARKC_PATH=/tmp/cleanrun # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc
# OPS_PATH=/tmp/cleanrun # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

currentdir=`pwd`

cd $LARKC_PATH/larkc/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true
mvn install -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/NewFileIdentifier
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../SparqlQueryEvaluationReasoner
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../RDFReader
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../../platform/endpoints/endpointsSourceCode/endpoint.sparql/
mvn install

cd $OPS_PATH
cd openphacts/ops-platform/larkc-plugins/

# Removed December 8th 2011 By Christian
# Integeration tests where broken.
# Not used by current workflow according to Antonis
#cd plugin.querymapper/
#mvn assembly:assembly
#mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.edffilter/
mvn install
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.sparqlexpand/
mvn install
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.edfquerytransformer/
mvn install
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.edfsearch/
mvn install
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.chemcallout/ChemSpiderServices
mvn install -Dmaven.test.skip=true
cd ..
mvn assembly:assembly -Dmaven.test.skip=true
mv ./target/*SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/platform/plugins

cd ../../larkc-endpoints/endpoint.opsapi
mvn assembly:assembly
mv ./target/*SNAPSHOT-LarkcEndpointAssembly.jar $LARKC_PATH/larkc/platform/endpoints

cd $currentdir
