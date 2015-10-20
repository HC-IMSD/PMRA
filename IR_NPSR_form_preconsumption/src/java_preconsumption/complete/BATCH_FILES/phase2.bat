@ECHO ON
:: ********************************
:: Phase 2 Parse eIndex
:: *******************************
@ECHO OFF

cd ..
set OLDDIR=%CD%
cd phase2_build\lib
java -jar phase2.jar %OLDDIR%\PDF_EXTRACTION
pause

