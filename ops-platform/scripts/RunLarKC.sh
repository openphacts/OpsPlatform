#LARKC_PATH #- path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk larkc/trunk
# Launch LarKC
currentdir=`pwd`
cd $LARKC_PATH/larkc/platform/
#rm -rf ordi-trree

# Removed December 8th 2011 By Christian
# Integeration tests where broken.
# Not used by current workflow according to Antonis
#java -cp plugins/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly/lib/derby.jar:./target/platform-3.0-SNAPSHOT-jar-with-dependencies.jar -Xmx1024M eu.larkc.core.Larkc
#eplacement without QueryMapper
java -cp ./target/platform-3.0-SNAPSHOT-jar-with-dependencies.jar -Xmx4G eu.larkc.core.Larkc

cd $currentdir
