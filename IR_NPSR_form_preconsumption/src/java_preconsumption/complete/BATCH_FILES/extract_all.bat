@ECHO ON
:: ********************************
:: Extract All Phases 1 - 5
:: *******************************
cd ..
set OLDDIR=%CD%
cd extract_all_build\lib
java -jar extract.jar %OLDDIR%\PDF_EXTRACTION
pause
