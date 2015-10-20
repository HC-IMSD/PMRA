package phase3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import prz.PrzFile;

import com.adobe.pdf.FormType;
import com.adobe.pdf.PDFDocument;
import com.adobe.pdf.PDFFactory;
import com.adobe.pdf.exceptions.PDFException;
    
/**
 * <p>Title: Extract_Files</p>
 * <p>Description: Based on the files contained in the specified directory parameter
 * create a holding directory for each identified xml pdf form. Any files that are 
 * found and identified as not being a xml pdf form are copied to each form directory 
 * created.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class Extract_Files {

    /**
     * extractFiles.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String extractFiles( PrzFile prz_directory ){
		return create(prz_directory);
	}
	
    /**
     * extractFiles.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String extractFiles( String directory ){
		PrzFile prz_directory = new PrzFile(directory);
		return create(prz_directory);
	}	

    /**
     * create.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if the eindex.xml file persed successfully or not.
     */
    public static String create( PrzFile prz_directory ) {
        
    	// global variables
        final String pdf_ext        = ".pdf";
        final String pdf_form       = "untype.xfa.pdf";
        final String form_dir_name  = "IRX";
        final String pdf_dir_name   = "PDF";
        final String zip_dir_name   = "ZIP";
        final String prz_ext        = ".prz";
        final String pass_log       = "_PASS.txt";
        final String fail_log       = "_FAIL.txt";
        
        File new_file               = null;
        File pdf_dir                = null;
        File zip_dir                = null;
        File[] file_list            = null;
        File[] other_fileslist      = null;
        String irzstr_dir           = null;
        File irxfile_dir            = null;            
        FileInputStream in_xfapdf   = null;
        PDFDocument pdf_doc         = null;
        Vector form_location        = new Vector();
        InputStream in_otherfiles   = null;
        OutputStream out_otherfiles = null;
        int form_cnt                = 0;
        int len                     = 0;
        byte[] buf                  = new byte[1024];
        boolean xmlform_found       = false;
        StringBuffer log	        = new StringBuffer();
        
        // info/error messages        
        String xFormDirectoriesCreated  = "INFO: Successfully generated required directories.";
        String xNoXMLFormFound          = "ERROR: NoXMLFormFound: The required PDF Form could not be found.";
        String xNoFilesFound            = "ERROR: NoFilesFound: The specified directory contains no files (should at least be an eindex.xml file).";
        String xListedFileNotFound      = "ERROR: ListedFileNotFound: A listed file could not be found or is null.";
        String xPDFFileNotFound         = "ERROR: FileNotFoundException: An error was encountered while attempting to read a specified PDF File.";
        String xCreatePDFDocument       = "ERROR: PDFException: An error was encountered while attempting to open/read the specified PDF Document.";
        String xIOCreatePDFDocument     = "ERROR: IOException: An error was encountered while attempting to open/read the specified PDF Document.";
        String xIOClosePDFOutStream     = "ERROR: IOException: An error was encountered while attempting to close the PDF Document stream";
        String xIRXDirectoryNotCreated  = "ERROR: IRXDirectoryNotCreated: An error was encountered while attempting to create the IRX Directory.";
        String xPDFDirectoryNotCreated  = "ERROR: PDFDirectoryNotCreated: An error was encountered while attempting to create the PDF Directory.";
        String xZIPDirectoryNotCreated  = "ERROR: ZIPDirectoryNotCreated: An error was encountered while attempting to create the ZIP Directory.";
        String xFormFileNotMoved        = "ERROR: FormFileNotMoved: An error was encountered while attempting to move the PDF Form to the ZIP Directory.";
        String xInStreamFileNotFound    = "ERROR: FileNotFoundException: An error was encountered while attempting to read a specified File.";
        String xOutStreamFileNotFound   = "ERROR: OutStreamFileNotFound: An error was encountered while attempting to write a specified file to the ZIP directory.";
        String xIOWriteFileOut          = "ERROR: IOException: An error was encountered while attempting to write a specified file to the ZIP directory.";
        String xIOCloseInStream         = "ERROR: IOException: An error was encountered while attempting to close the input stream for a specified file";
        String xIOCloseOutStream        = "ERROR: IOException: An error was encountered while attempting to close the output stream for a specified file";
 
        System.out.println("INFO: GENERATE REQUIRED DIRECTORIES");
        log.append("INFO: GENERATED REQUIRE DIRECTORIES\r\n");
        
        if( ! prz_directory.isValid() ){
        	System.out.println(""+prz_directory.error());
        	log.append(""+prz_directory.error()+"\r\n");
        	return log.toString(); 
        }
        
        // Generate list of files contained in the specified directory
        file_list = prz_directory.listFiles();

        // look at each file in the specified directory
        for( int a=0;a<file_list.length;a++ ){
            
            // return error if file does not exist or is null
            if( !file_list[a].exists() || file_list[a] == null ){
            	System.out.println(xListedFileNotFound);
            	log.append(xListedFileNotFound+"\r\n");
            	return log.toString(); 
            }
            
            // if a pdf file
            if( file_list[a].getName().endsWith(pdf_ext) ){
                // read in pdf
                try {
                    in_xfapdf = new FileInputStream(file_list[a].getPath());
                } 
                catch (FileNotFoundException e) {
                	System.out.println(xPDFFileNotFound);
                	log.append(xPDFFileNotFound+"\r\n");
                	return log.toString(); 
                }
                
                // create/open pdf Document
                try {
                    pdf_doc = PDFFactory.openDocument(in_xfapdf);                    
                } 
                catch (PDFException e) {
                	System.out.println(xCreatePDFDocument);
                	log.append(xCreatePDFDocument+"\r\n");
                	return log.toString(); 
                } 
                catch (IOException e) {
                	System.out.println(xIOCreatePDFDocument);
                	log.append(xIOCreatePDFDocument+"\r\n");
                	return log.toString(); 
                }
                
                // close input stream
                try {
                    in_xfapdf.close();
                } 
                catch (IOException e) {
                	System.out.println(xIOClosePDFOutStream);
                	log.append(xIOClosePDFOutStream+"\r\n");
                	return log.toString();
                }
                
                // if pdf is a xml Form
                if ( pdf_doc.getFormType() == FormType.XML_FORM ){
                     
                    // create new IRX directory
                    irxfile_dir  = new File(prz_directory.getPath() + form_dir_name + form_cnt);

                    // return error if IRX directory is not created
                    if ( !irxfile_dir.mkdir() ) {
                    	System.out.println(xIRXDirectoryNotCreated);
                    	log.append(xIRXDirectoryNotCreated+"\r\n");
                    	return log.toString();
                    }
                    
                    //System.out.println("INFO: Successfully created \\"+form_dir_name + form_cnt+" directory.");
                    log.append("INFO: Successfully created \\"+form_dir_name + form_cnt+" directory.\r\n");

                    // create PDF under new IRX directory
                    pdf_dir = new File(irxfile_dir.getPath() +"/"+ pdf_dir_name);
                    
                    // return error if PDF directory is not created
                    if ( !pdf_dir.mkdir() ) {
                    	System.out.println(xPDFDirectoryNotCreated);
                    	log.append(xPDFDirectoryNotCreated+"\r\n");
                    	return log.toString();
                    }
                    
                    //System.out.println("INFO: Successfully created \\"+form_dir_name + form_cnt+"\\"+pdf_dir_name+" directory.");
                    log.append("INFO: Successfully created \\"+form_dir_name + form_cnt+"\\"+ pdf_dir_name+" directory.\r\n");
                    
                    // create ZIP under new IRX directory
                    zip_dir = new File(irxfile_dir.getPath() +"/"+ zip_dir_name);
                    
                    // return error if ZIP directory is not created
                    if ( !zip_dir.mkdir() ) {
                    	System.out.println(xZIPDirectoryNotCreated);
                    	log.append(xZIPDirectoryNotCreated+"\r\n");
                    	return log.toString();
                    }
                    
                    //System.out.println("INFO: Successfully created \\"+form_dir_name + form_cnt+"\\"+zip_dir_name+" directory.");
                    log.append("INFO: Successfully created \\"+form_dir_name + form_cnt+"\\"+ zip_dir_name+" directory.\r\n");
                    
                    // add path of new directory for later use when moving non-xfa files over
                    form_location.addElement(irxfile_dir.getPath());
                    new_file = new File(zip_dir.getPath() +"/"+ pdf_form);                
                    
                    //System.out.println("INFO: Successfully renamed '"+file_list[a].getName()+"' to '"+pdf_form+"'.");
                    log.append("INFO: Successfully renamed '"+file_list[a].getName()+"' to '"+pdf_form+"'.\r\n");

                    //System.out.println("INFO: Successfully moved '"+pdf_form+"' to \\"+ form_dir_name + form_cnt +"\\"+zip_dir_name+" directory.");
                    log.append("INFO: Successfully moved '"+pdf_form+"' to \\"+ form_dir_name + form_cnt +"\\"+zip_dir_name+" directory.\r\n");
                    
                    // return error if form is not moved to new directory
                    if( !file_list[a].renameTo(new_file) ){
                    	System.out.println(xFormFileNotMoved);
                    	log.append(xFormFileNotMoved+"\r\n");
                    	return log.toString();
                    }
                    
                    // counter to uniquely identify new form folder(s)
                    form_cnt++;
                    xmlform_found = true;
                }                
            }
        }
        
        // return error if no xml form is found
        if(!xmlform_found){
        	System.out.println(xNoXMLFormFound);
        	log.append(xNoXMLFormFound+"\r\n");
        	return log.toString();
        }
        
        // Generate list of files that are not an xml form in the specified directory
        other_fileslist = prz_directory.listFiles();

        // return error if the directory contains no files (should at least be an eindex.xml file)
        if ( other_fileslist.length == 0 ){
        	System.out.println(xNoFilesFound);
        	log.append(xNoFilesFound+"\r\n");
        	return log.toString();
        }
        
        // look at each file in the specified file list
        for( int b=0;b<other_fileslist.length;b++ ){
            
            // skip the form directories, prz file and log files
            if( !other_fileslist[b].isDirectory() 
                    && !other_fileslist[b].getName().endsWith(prz_ext) 
                    && !other_fileslist[b].getName().endsWith(pass_log)
                    && !other_fileslist[b].getName().endsWith(fail_log)){
                
                // for each form/form folder
                for(int c=0;c<form_location.size();c++){
                    
                    // retrieve form directory path
                    irzstr_dir = (String)form_location.elementAt(c);
                    
                    // input stream to read in file
                    try {
                        in_otherfiles = new FileInputStream(other_fileslist[b].getPath());
                    } 
                    catch (FileNotFoundException e) {
                    	System.out.println(xInStreamFileNotFound);
                    	log.append(xInStreamFileNotFound+"\r\n");
                    	return log.toString();
                    }
                    
                    // output stream to write file to /IRX#/ZIP directory
                    try {
                        out_otherfiles = new FileOutputStream(irzstr_dir+"\\"+zip_dir_name+"\\"+other_fileslist[b].getName());
                    } 
                    catch (FileNotFoundException e) {
                    	System.out.println(xOutStreamFileNotFound);
                    	log.append(xOutStreamFileNotFound+"\r\n");
                    	return log.toString();
                    }
                    
                    // write file to the ZIP directory
                    try {
                        while ((len = in_otherfiles.read(buf)) > 0) {
                            out_otherfiles.write(buf, 0, len);
                        }
                    } 
                    catch (IOException e) {
                    	System.out.println(xIOWriteFileOut);
                    	log.append(xIOWriteFileOut+"\r\n");
                    	return log.toString();
                    }
                    
                    // close input stream
                    try {
                        in_otherfiles.close();
                    } 
                    catch (IOException e) {
                    	System.out.println(xIOCloseInStream);
                    	log.append(xIOCloseInStream+"\r\n");
                    	return log.toString();
                    }
                    
                    // close output stream
                    try {
                        out_otherfiles.close();
                    } 
                    catch (IOException e) {
                    	System.out.println(xIOCloseOutStream);
                    	log.append(xIOCloseOutStream+"\r\n");
                    	return log.toString();
                    }
                    
                    // delete original file
                    other_fileslist[b].deleteOnExit();
                    
                    //System.out.println("INFO: Successfully moved '"+other_fileslist[b].getName()+"' to the "+irzstr_dir+"\\"+zip_dir_name+"\\ directory.");
                    log.append("INFO: Successfully moved '"+other_fileslist[b].getName()+"' to the "+irzstr_dir+"\\"+zip_dir_name+"\\ directory.\r\n");
                }
            }
        }

        System.out.println(xFormDirectoriesCreated);
        System.out.println("INFO: GENERATE REQUIRED DIRECTORIES COMPLETE");
        System.out.println(" ");
        
        log.append(xFormDirectoriesCreated+"\r\n");       
        log.append("INFO: GENERATE REQUIRED DIRECTORIES COMPLETE");
        log.append("\r\n");
        // return success
        return log.toString();
    }
    
    /**
     * @param args an absolute path giving the base location of the archive file
     */
    public static void main(String[] args) {
        FileWriter msg_writer = null;
        StringBuffer success  = new StringBuffer();
        String dir            = null;
        String out_file       = null;
        String result		  = null;

        if( args.length == 1 ){
            dir = args[0];
        }
        else{
        	System.err.println("ERROR: No base directory specified");
        	System.err.println("Usage: java -jar phase3.jar [directory path]");
        	System.exit(-1);
        }
        
        result = extractFiles(dir);
        success.append(result+"\r\n" );
        
        if( success.toString().indexOf("ERROR:") > 0){
            out_file = "PHASE_3_FAIL.txt";
            File pass_file = new File(dir+"/"+"PHASE_3_PASS.txt");
            
            if(pass_file.exists())
                pass_file.delete();
        }
        else{
            out_file = "PHASE_3_PASS.txt";
            File fail_file = new File(dir+"/"+"PHASE_3_FAIL.txt");
            
            if(fail_file.exists())
                fail_file.delete();
        }

        try {
            msg_writer = new FileWriter(dir+"/"+out_file);
            msg_writer.write(success +"\r\n");
            msg_writer.flush();
            msg_writer.close();
        } 
        catch (IOException e) {
            System.err.println("ERROR: IOException: A error was encountered in Main attempting to write log file.");
        }
        
        System.exit(1);
    }    
}