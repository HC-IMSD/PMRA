package phase1;

import prz.PrzFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * <p>Title: Extract_Archive</p>
 * <p>Description: Extracts enties from a archive file(zip, jar, prz).</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */


public class Extract_Archive {
    
	
    /**
     * unZip.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String unZip( PrzFile prz_directory ){
		return extract(prz_directory);
	}
	
    /**
     * unZip.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String unZip( String directory ){
		PrzFile prz_directory = new PrzFile(directory);
		return extract(prz_directory);
	}
	
    /**
     * extract.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if the archive file was extracted successfully or not.
     */
    private static String extract( PrzFile przfile ) {

        // global variables 
        File[] filelist       = null;
        ZipFile prz_file      = null;
        InputStream in        = null;
        FileOutputStream out  = null;
        String zipEntryName   = null;
        String przFileName	  = null;
        ZipEntry entry        = null;
        int substring_begin   = 0;
        int substring_end     = 0;
        int len               = 0;
        byte[] buf            = new byte[1024];
        StringBuffer log	  = new StringBuffer();
        
        // info/error messages
        String infoArchiveExtracted     = "INFO: Successfully extracted contents of ";  
        String xMultipleFilesFound      = "ERROR: The directory specified contains multiple files. A directory containing a single PRZ file must be specified.";
        String exOpeningArchiveFile     = "ERROR: ZipException: An error was encountered while attempting to open the prz file.";
        String exIOArchiveFile          = "ERROR: IOException: An error was encountered while attempting to open the prz file.";
        String exArchiveEntryNotFound   = "ERROR: FileNotFoundException: An error was encountered while attempting to extract a entry from the prz file";
        String exIOArchiveEntry         = "ERROR: IOException: An error was encountered while attempting to read a entry from the prz file";
        String exIOExtractArchiveEntry  = "ERROR: IOException: An error was encountered while attempting to write a entry from the prz file";
        String exCloseOutStream         = "ERROR: IOException: An error was encountered while attempting to close a entry from the prz file";
        String exCloseInStream          = "ERROR: IOException: An error was encountered while attempting to close the prz file";  
        
        System.out.println("INFO: EXTRACT PRZ FILE");
        log.append("INFO: EXTRACT PRZ FILE\r\n");

        if( ! przfile.isValid() ){
        	System.out.println(""+przfile.error());
        	log.append(""+przfile.error()+"\r\n");
        	return log.toString();
        }
        
        if(przfile.listFiles().length > 1){
        	System.out.println(xMultipleFilesFound);
        	log.append(xMultipleFilesFound+"\r\n");
        	return log.toString();
        }

        // Generate list of files contained in the specified directory
        filelist = przfile.listFiles();
        przFileName = filelist[0].getName();
        
        // get archive file
        try {
            prz_file = new ZipFile( filelist[0] );
        } 
        catch ( ZipException e ) {
        	System.out.println(exOpeningArchiveFile);
        	log.append(exOpeningArchiveFile+"\r\n");
        	return log.toString();
        } 
        catch ( IOException e ) {
        	System.out.println(exIOArchiveFile);
        	log.append(exIOArchiveFile+"\r\n");
        	return log.toString();
        } 

        //System.out.println("INFO: Extracting the following files from '"+ przFileName +"':");
        log.append("INFO: Extracted the following files from '"+ przFileName +"':\r\n");
        // look at each entry in archive file
        int cnt = 1;
        
        for( Enumeration entries = prz_file.entries(); entries.hasMoreElements(); ) {          
            
            // get entry
            zipEntryName = ((ZipEntry) entries.nextElement()).getName();
            entry        = prz_file.getEntry(zipEntryName);
            
            prz_file.entries();
            
            log.append("INFO:    FILE "+cnt+": '"+zipEntryName+"'\r\n");
            //System.out.println("INFO:     FILE "+cnt+": '"+zipEntryName+"' ");
            
            cnt ++;
            // Handle directories in the archive file
            if( !entry.isDirectory() ) {
                
                // if entry is inside a folder retrieve only the entry not the folder/full path
                if( zipEntryName.indexOf("/") > 0 ){
                    substring_begin = zipEntryName.lastIndexOf("/")+1;
                    substring_end = zipEntryName.length();
                    zipEntryName = zipEntryName.substring(substring_begin, substring_end) ;
                }
                
                // open up io streams
                try {
                    in = prz_file.getInputStream(entry);
                    out = new FileOutputStream(przfile.getPath() + zipEntryName);
                } 
                catch ( FileNotFoundException e ) {
                	System.out.println(exArchiveEntryNotFound);
                	log.append(exArchiveEntryNotFound+"\r\n");
                	return log.toString();
                } 
                catch ( IOException e ) {
                	System.out.println(exIOArchiveEntry);
                	log.append(exIOArchiveEntry+"\r\n");
                	return log.toString();
                }

                // write out entries
                try {
                    while ( (len = in.read(buf, 0, 1024) ) > -1)
                        out.write(buf, 0, len);                
                } 
                catch ( IOException e ) {
                	System.out.println(exIOExtractArchiveEntry);
                	log.append(exIOExtractArchiveEntry+"\r\n");
                	return log.toString();
                }
                
                // close output stream (Zip Entry)
                try {
                    out.close();
                } 
                catch ( IOException e ) {
                	System.out.println(exCloseOutStream);
                	log.append(exCloseOutStream+"\r\n");
                	return log.toString();
                }

                // close input stream (Prz file)
                try {
                    in.close();
                } 
                catch ( IOException e ) {
                	System.out.println(exCloseInStream);
                	log.append(exCloseInStream+"\r\n");
                	return log.toString();
                }
            }
        }
        
        System.out.println(infoArchiveExtracted+"'"+prz_file.getName()+"'");
        System.out.println("INFO: EXTRACT PRZ FILE COMPLETE");
        System.out.println(" ");

        log.append(infoArchiveExtracted+prz_file.getName()+"\r\n");
        log.append("INFO: EXTRACT PRZ FILE COMPLETE");
        log.append("\r\n");
        return log.toString();
    }

    /**
     * The main method.
     * 
     * @param args an absolute path giving the base location of the archive file
     */
    public static void main( String[] args ) {

        FileWriter msg_writer = null;
        StringBuffer success  = new StringBuffer();
        String dir            = null;
        String out_file       = null;
        String result         = null;

        if( args.length == 1 ){
            dir = args[0];
        }
        else{
        	System.err.println("ERROR: No base directory specified");
        	System.err.println("Usage: java -jar phase1.jar [directory path]");
        	System.exit(-1);
        }
        
        result = unZip(dir);
        success.append( result+"\r\n" );
        
        if( success.toString().indexOf("ERROR:") > 0 ){
            out_file = "PHASE_1_FAIL.txt";
            File pass_file = new File(dir+"/"+"PHASE_1_PASS.txt");
            
            if(pass_file.exists())
                pass_file.delete();
        }
        else{
            out_file = "PHASE_1_PASS.txt";
            File fail_file = new File(dir+"/"+"PHASE_1_FAIL.txt");
            
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
    }
}