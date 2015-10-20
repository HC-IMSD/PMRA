@ECHO ON
:: ********************************
:: Phase 3 Extract Files
:: *******************************
@ECHO OFF

cd ..
set OLDDIR=%CD%
cd phase3_build\lib
java -jar phase3.jar %OLDDIR%\PDF_EXTRACTION
pause
