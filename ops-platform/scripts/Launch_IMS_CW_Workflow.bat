ECHO OFF

rem depends on Environment variables 
rem LARKC_PATH path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk 
rem Note my SVN did not included the parent folders larkc/trunk add them if required.
rem OPS_PATH- path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
rem note My SVN did not include the parent folder openphacts add it if required.

set WORKFLOW="%OPS_PATH%\ops-platform\larkc-workflow\IMS_CW_workflow.ttl

call dir "%OPS_PATH%\ops-platform\larkc-workflow\IMS_CW_workflow.ttl

call:changeDirectory "%OPS_PATH%\OPSWorkflowUtils\target\classes"
call java -cp ..\OPSWorkflowUtils.jar; eu.ops.utils.WorkflowEndpointCreator %WORKFLOW%

rem back to here to rerun
call:changeDirectory "%OPS_PATH%\ops-platform\scripts"

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

