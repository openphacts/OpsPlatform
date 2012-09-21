#!/bin/bash
export MAVEN_OPTS=-Xmx512m
# LARKC_PATH=/tmp/cleanrun # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc
# OPS_PATH=/tmp/cleanrun # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

currentdir=`pwd`

cp $OPS_PATH/openphacts/Larkc_fix/logback.xml $LARKC_PATH/larkc/platform/src/main/resources/
cp $OPS_PATH/openphacts/Larkc_fix/pom.xml $LARKC_PATH/larkc/platform/

cd $LARKC_PATH/larkc/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true
mvn install -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/SparqlQueryEvaluationReasoner
mvn assembly:assembly -Dmaven.test.skip=true
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../../platform/endpoints/endpointsSourceCode/endpoint.sparql/
mvn install

cd $OPS_PATH
cd openphacts/ops-platform/larkc-plugins/

cd plugin.conceptwiki/
mvn assembly:assembly -Dmaven.test.skip=true 
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.imsSparqlExpand/
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.chemcallout/ChemSpiderServices
mvn install -Dmaven.test.skip=true
cd ..
mvn assembly:assembly -Dmaven.test.skip=true
mv ./target/*SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/platform/plugins

cd ..
cd ../larkc-endpoints/endpoint.opsapi
mvn assembly:assembly
mv ./target/*SNAPSHOT-LarkcEndpointAssembly.jar $LARKC_PATH/larkc/platform/endpoints

cd $currentdir
