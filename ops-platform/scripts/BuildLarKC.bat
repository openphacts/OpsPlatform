rem THIS bat was built by Christain based on BuildLarKC,sh
rem If there are difference between the bat and the sh assume the sh is the correct version.
rem Please contact Christian if you find any errors or if this version is out of date.

rem !/bin/bash
set MAVEN_OPTS=-Xmx512m

rem depends on Environment variables 
rem LARKC_PATH path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk 
rem Note my SVN did not included the parent folders larkc/trunk add them if required.
rem OPS_PATH- path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
rem note My SVN did not include the parent folder openphacts add it if required.
rem assumes mvn.bat directory is in your "Path" Environment variable.

cd "%LARKC_PATH%\platform\"
call mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true
call mvn install -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

cd "%LARKC_PATH%/plugins/NewFileIdentifier"
call mvn install 
ECHO ON
move target\*SNAPSHOT.jar   ..\..\platform\plugins

cd "%LARKC_PATH%/plugins/SparqlQueryEvaluationReasoner"
call mvn install 
ECHO ON
move target\*SNAPSHOT.jar   ..\..\platform\plugins

cd "%LARKC_PATH%/plugins/RDFReader
call mvn install
ECHO ON
dir target
move target\*SNAPSHOT.jar   ..\..\platform\plugins

cd "%OPS_PATH%\ops-platform\larkc-plugins\plugin.querymapper\"
call mvn install
call mvn assembly:assembly
ECHO ON
move target\plugin.QueryMapper-0.0.1-SNAPSHOT-LarkcPluginAssembly.jar "%LARKC_PATH%\platform\plugins"

cd "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edffilter/"
call mvn install
ECHO ON
move target\plugin.EDFFilter-0.0.1-SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

cd "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edfquerytransformer/"
call mvn install
ECHO ON
move target\plugin.EDFQueryTransformer-0.0.1-SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

cd "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edfsearch/"
call mvn install
ECHO ON
move target\plugin.EDFSearch-0.0.1-SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

rem back to here to rerun
cd "%OPS_PATH%\ops-platform\scripts"
