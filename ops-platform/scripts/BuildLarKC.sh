#!/bin/bash
export MAVEN_OPTS=-Xmx512m
# LARKC_PATH=/tmp/cleanrun # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# OPS_PATH=/tmp/cleanrun # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

currentdir=`pwd`

cd $LARKC_PATH/larkc/trunk/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/NewFileIdentifier
mvn install
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../SparqlQueryEvaluationReasoner
mvn install
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../RDFReader
mvn install
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd $OPS_PATH
cd openphacts/ops-platform/larkc-plugins/
cd plugin.querymapper/
mvn assembly:assembly
mv ./target/plugin.QueryMapper-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edffilter/

mvn install
mv ./target/plugin.EDFFilter-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edfquerytransformer/

mvn install
mv ./target/plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edfsearch/

mvn install
mv ./target/plugin.EDFSearch-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins

cd $currentdir
