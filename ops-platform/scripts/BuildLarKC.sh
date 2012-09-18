#!/bin/bash
export MAVEN_OPTS=-Xmx512m
# LARKC_PATH=/tmp/cleanrun # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc
# OPS_PATH=/tmp/cleanrun # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

currentdir=`pwd`

cd $LARKC_PATH/openphacts/Larkc_fix/

cp logback.xml $LARKC_PATH/larkc/platform/src/main/resources/
cp pom.xml $LARKC_PATH/larkc/platform/
cp DataFactoryImpl.java $LARKC_PATH/larkc/platform/src/main/java/eu/larkc/core/data/
cp SAILRdfStoreConnectionImpl.java $LARKC_PATH/larkc/platform/src/main/java/eu/larkc/core/data/
cp SparqlQueryEvaluationReasoner.java $LARKC_PATH/larkc/plugins/SparqlQueryEvaluationReasoner/src/main/java/eu/larkc/plugin
cp VariableBindingBase.java $LARKC_PATH/larkc/platform/src/main/java/eu/larkc/core/query/
mvn install:install-file -Dfile=virt_sesame2.jar -DgroupId=virtuoso.sesame2 -DartifactId=virt_sesame2 -Dversion=2.6.5 -Dpackaging=jar
mvn install:install-file -Dfile=virtjdbc3.jar -DgroupId=virtuoso.jdbc3 -DartifactId=virtjdbc3 -Dversion=3.0.0 -Dpackaging=jar

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

#cd plugin.conceptwiki/
#mvn assembly:assembly -Dmaven.test.skip=true 
#mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd plugin.imsSparqlExpand/
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

cd ../plugin.VirtuosoSparqlFormatter/
mvn assembly:assembly 
mv ./target/*SNAPSHOT.jar $LARKC_PATH/larkc/platform/plugins

#cd ../plugin.chemcallout/ChemSpiderServices
#mvn install -Dmaven.test.skip=true
#cd ..
#mvn assembly:assembly -Dmaven.test.skip=true
#mv ./target/*SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/platform/plugins

cd ..
cd ../larkc-endpoints/endpoint.opsapi
mvn assembly:assembly
mv ./target/*SNAPSHOT-LarkcEndpointAssembly.jar $LARKC_PATH/larkc/platform/endpoints

cd ../endpoint.lda
mvn assembly:assembly
mv ./target/*SNAPSHOT-LarkcEndpointAssembly.jar $LARKC_PATH/larkc/platform/endpoints

cd $currentdir
