@ECHO ON
:: ********************************
:: Phase 1 Extract Files from PRZ
:: *******************************
@ECHO OFF

cd ..
set OLDDIR=%CD%
cd phase1_build\lib
java -jar phase1.jar %OLDDIR%\PDF_EXTRACTION
pause


