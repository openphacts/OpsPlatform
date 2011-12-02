#!/bin/bash
export MAVEN_OPTS=-Xmx512m
INSTALLDIR="/home/lupin/projects"
echo $INSTALLDIR
#svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk $INSTALLDIR/larkc/trunk
cd $INSTALLDIR/larkc/trunk/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/NewFileIdentifier
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform/plugins

cd ../NewSparqlQueryEvaluationReasoner
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform/plugins

cd ../RDFReader
mvn assembly:assembly
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform/plugins
cd $INSTALLDIR
#svn co  https://trac.nbic.nl/svn/openphacts 
cd openphacts/ops-platform/larkc-plugins/
cd plugin.querymapper/
mvn assembly:assembly
mv ./target/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly.jar $INSTALLDIR/larkc/trunk/platform/plugins
cd ../plugin.edffilter/
mvn assembly:assembly
mv ./target/plugin.EDFFilter-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform/plugins
cd ../plugin.edfquerytransformer/
mvn assembly:assembly
mv ./target/plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform/plugins
cd ../plugin.edfsearch/
mvn assembly:assembly
mv ./target/plugin.EDFSearch-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform/plugins


# Launch LarKC
cd $INSTALLDIR/larkc/trunk/platform/
java -jar -Xmx1024M ./target/platform-2.0.0-SNAPSHOT-jar-with-dependencies.jar

