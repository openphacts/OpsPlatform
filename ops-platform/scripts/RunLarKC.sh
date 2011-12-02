#LARKC_PATH #- path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# Launch LarKC
currentdir=`pwd`
cd $LARKC_PATH/larkc/branches/openphacts/platform/
rm -rf ordi-trree
java -cp plugins/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly/lib/derby.jar:./target/platform-3.0-SNAPSHOT-jar-with-dependencies.jar -Xmx1024M eu.larkc.core.Larkc

cd $currentdir
