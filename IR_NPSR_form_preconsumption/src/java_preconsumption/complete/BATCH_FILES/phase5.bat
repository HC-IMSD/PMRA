@ECHO ON
:: ********************************
:: Phase 5 Extract Attachments
:: *******************************
@ECHO OFF

cd ..
set OLDDIR=%CD%
cd phase5_build\lib
java -jar phase5.jar %OLDDIR%\PDF_EXTRACTION
pause

