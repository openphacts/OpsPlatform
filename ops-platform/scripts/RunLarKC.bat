rem This bat was built by Christain based on RinLarKC,sh
rem If there are difference between the bat and the sh assume the sh is the correct version.
rem Please contact Christian if you find any errors or if this version is out of date.

rem depends on Environment variables 
rem LARKC_PATH path where LarKC is checked out: svn co https://larkc.svn.sourceforge.net/svnroot/larkc/trunk 
rem Note my SVN did not included the parent folders larkc/trunk add them if required.
rem OPS_PATH- path where OPS repository is checked out: svn co  https://trac.nbic.nl/svn/openphacts
rem note My SVN did not include the parent folder openphacts add it if required.

rem Launch LarKC
set OLDDIR=%CD%

call:changeDirectory "%LARKC_PATH%\platform\"
del ordi-trree
call:check target\platform-2.5.0-SNAPSHOT-jar-with-dependencies.jar 

java -cp target\platform-2.5.0-SNAPSHOT-jar-with-dependencies.jar -Xmx1G eu.larkc.core.Larkc

rem restore current directory
call:changeDirectory /d "%OLDDIR%" 

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

:check
IF EXIST %~1 (
   echo. found "%~1"
   goto:eof
) ELSE (
   echo. ERROR Unable to find %~1. 
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

