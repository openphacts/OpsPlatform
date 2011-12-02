#!/bin/bash
export MAVEN_OPTS=-Xmx512m
INSTALLDIR="/tmp"
echo $INSTALLDIR
svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
cd larkc/trunk/platform/
mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd ../plugins/NewFileIdentifier
mvn install
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform

cd ../NewSparqlQueryEvaluationReasoner
mvn install
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform

cd ../RDFReader
mvn install
mv ./target/*SNAPSHOT.jar   $INSTALLDIR/larkc/trunk/platform
cd $INSTALLDIR
pwd
svn co  https://trac.nbic.nl/svn/openphacts
cd openphacts/ops-platform/larkc-plugins/
cd plugin.querymapper/
mvn install
mv ./target/plugin.QueryMapper-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform
cd ../plugin.edffilter/
mvn install
mv ./target/plugin.EDFFilter-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform
cd ../plugin.edfquerytransformer/
mvn install
mv ./target/plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform
cd ../plugin.edfsearch/
mvn install
mv ./target/plugin.EDFSearch-0.0.1-SNAPSHOT.jar $INSTALLDIR/larkc/trunk/platform


# Launch LarKC
cd $INSTALLDIR/larkc/trunk/platform/
java -jar -Xmx1024M ./target/platform-2.0.0-SNAPSHOT-jar-with-dependencies.jar

