                                README

                ePRS Manual PRZ package extractor 
         
                       
The ePRS Manual PRZ package extractor is intended for manually extracting the
contents of a *.prz file and the pdf Adobe Form xml contained within.


=======================================================================
                            Prerequisites
=======================================================================

Ant version 1.7 or higher is required in order to build/compile the source.


=======================================================================
                                General
=======================================================================

Phase 1: Extracts entries from a archive file(zip, jar, prz).
Phase 2: Used to varify that files specified in the eindex file under  
         the attribute idref='DM_EFILE_NAME' exist and are all accounted for .
Phase 3: Based on the files contain in the specified directory parameter
         create a holding directory for each identified xml pdf form. Any 
         files that are found and identified as not being a xml pdf form are 
         copied to each form directory created.
Phase 4: Extracts the form xml from an Adobe Form.
Phase 5: Checks for Adobe Form attachments.

all    : Phase 1 - 5.


=======================================================================
                               Building
=======================================================================

The extractor can be built in five "phases" with five separate runnable jar
files, or can be built as a single runnable jar with all five phases self
contained within.

To build on command line using ant:

ant -f build.xml all
ant -f build.xml phase 1
ant -f build.xml phase 2
ant -f build.xml phase 3
ant -f build.xml phase 4
ant -f build.xml phase 5


=======================================================================
                               Running
=======================================================================

Once compiled all jars can be run from the command line and require a single
argument/parameter. The argument/parameter is the directory path that contains
a *.prz file. The specified directory may only contain a single file and that
file must be of type *.prz.

To execute from the command line:

java -jar [phase].jar C:\path\to\*prz


=======================================================================
                        Files and Directories
=======================================================================

/src/ : Contains the Java source for all phases
/PDF_EXTRACTION/ : Sample prz file to test
/lib/ : required libraries to compile source
/BATCH_FILES/ : Batch files to run Phases once built. Defaults to /PDF_EXTRACTION/
                directory for testing.
