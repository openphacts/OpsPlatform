# Launch LarKC
currentdir=`pwd`
cd $LARKC_PATH/larkc/trunk/platform/
rm -rf ordi-trree
java -cp plugins/plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly/lib/derby.jar:./target/platform-2.0.0-SNAPSHOT-jar-with-dependencies.jar -Xmx1G eu.larkc.core.Larkc

cd $currentdir
