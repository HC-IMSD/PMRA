@ECHO ON
:: ********************************
:: Phase 4 Extract Form Data
:: *******************************
@ECHO OFF

cd ..
set OLDDIR=%CD%
cd phase4_build\lib
java -jar phase4.jar %OLDDIR%\PDF_EXTRACTION
pause

