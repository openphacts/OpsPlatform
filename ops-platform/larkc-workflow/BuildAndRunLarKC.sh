#!/bin/bash
export MAVEN_OPTS=-Xmx512m
#LARKC_PATH - path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
#OPS_PATH - path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts

cd $LARKC_PATH/larkc/trunk/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/NewFileIdentifier
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../NewSparqlQueryEvaluationReasoner
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins

cd ../RDFReader
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   ../../platform/plugins
cd $OPS_PATH
cd openphacts/ops-platform/larkc-plugins/
cd plugin.querymapper/
mvn assembly:assembly
mv ./target/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edffilter/
mvn assembly:assembly
mv ./target/plugin.EDFFilter-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edfquerytransformer/
mvn assembly:assembly
mv ./target/plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins
cd ../plugin.edfsearch/
mvn assembly:assembly
mv ./target/plugin.EDFSearch-0.0.1-SNAPSHOT.jar $LARKC_PATH/larkc/trunk/platform/plugins


# Launch LarKC
cd $LARKC_PATH/larkc/trunk/platform/
java -jar -Xmx1024M ./target/platform-2.0.0-SNAPSHOT-jar-with-dependencies.jar

