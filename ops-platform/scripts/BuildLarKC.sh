#!/bin/bash
export MAVEN_OPTS=-Xmx512m
# LARKC_PATH=/tmp/cleanrun # path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/branches/openphacts larkc/branches/openphacts
# OPS_PATH=/tmp/cleanrun # - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

currentdir=`pwd`

cd $LARKC_PATH/larkc/branches/openphacts/platform/
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
mv ./target/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

cd ../plugin.edffilter/
mvn install
mv ./target/plugin.EDFFilter-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

cd ../plugin.sparqlexpand/
mvn install
mv ./target/plugin.SPARQLExpand-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

cd ../plugin.edfquerytransformer/
mvn install
mv ./target/plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

cd ../plugin.edfsearch/
mvn install
mv ./target/plugin.EDFSearch-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

# do not build ChemCallout, the jar is in the repository
cd ../plugin.chemcallout/
cp ./target/plugin.ChemCallout-0.0.1-SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/branches/openphacts/platform/plugins

cd $currentdir
