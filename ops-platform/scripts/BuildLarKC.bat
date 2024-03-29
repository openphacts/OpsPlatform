ECHO OFF
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

call:changeDirectory "%LARKC_PATH%\platform\"
call mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true
call mvn install -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true

call:changeDirectory "%LARKC_PATH%\plugins\NewFileIdentifier"
call mvn assembly:assembly 
call:moveFile target\*SNAPSHOT.jar   ..\..\platform\plugins

call:changeDirectory "%LARKC_PATH%\plugins\SparqlQueryEvaluationReasoner"
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar   ..\..\platform\plugins

call:changeDirectory "%LARKC_PATH%\plugins\RDFReader
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar   ..\..\platform\plugins

call:changeDirectory "%LARKC_PATH%\platform\endpoints\endpointsSourceCode\endpoint.sparql\
call mvn install
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar   ..\..

rem Removed December 8th 2011 By Christian
rem Integeration tests where broken.
rem Not used by current workflow according to Antonis
rem call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.querymapper"
rem call mvn assembly:assembly
rem call:moveFile target\*SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edffilter"
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edfquerytransformer"
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.edfsearch"
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.conceptwiki"
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.imsSqarqlExpand"
call mvn assembly:assembly -DdescriptorId=jar-with-dependencies -Dmaven.test.skip=true
call:moveFile target\*SNAPSHOT-LarkcPluginAssembly.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.chemcallout\ChemSpiderServices"
call mvn install -Dmaven.test.skip=true
call:changeDirectory "%OPS_PATH%\ops-platform\larkc-plugins\plugin.chemcallout"
call mvn assembly:assembly -Dmaven.test.skip=true
call:moveFile target\*SNAPSHOT-LarkcPluginAssembly.jar "%LARKC_PATH%\platform\plugins"

call:changeDirectory "%OPS_PATH%\ops-platform\larkc-endpoints\endpoint.opsapi
call mvn assembly:assembly
call:moveFile target\*SNAPSHOT-LarkcEndpointAssembly.jar "%LARKC_PATH%\platform\endpoints"

rem back to here to rerun
call:changeDirectory "%OPS_PATH%\ops-platform\scripts"

echo. finished script if it did not pause no bat level error noticed.
goto:eof

::--------------------------------------------------------
::-- Function section starts below here
::--------------------------------------------------------

:changeDirectory
IF EXIST %~1 (
   echo. changing dir to "%~1"
   chdir "%~1"
   goto:eof
) ELSE (
   echo. ERROR Unable to find the Directory %~1. 
   pause
)
goto:eof

:moveFile 
IF EXIST %~1 (
   IF EXIST %~2 (
   echo. moving "%~1" to "%~2"
   move "%~1" "%~2"
   goto:eof
   ) ELSE (
      echo. ERROR Unable to find target Directory %~2. 
      pause
      goto:eof
   )
) ELSE (
   echo. ERROR Unable to find file %~1 ."
   pause
   goto:eof
)
goto:eof

:copyFile 
IF EXIST %~1 (
   IF EXIST %~2 (
   echo. moving "%~1" to "%~2"
   copy "%~1" "%~2"
   goto:eof
   ) ELSE (
      echo. ERROR Unable to find target Directory %~2. 
      pause
      goto:eof
   )
) ELSE (
   echo. ERROR Unable to find file %~1 ."
   pause
   goto:eof
)
goto:eof

