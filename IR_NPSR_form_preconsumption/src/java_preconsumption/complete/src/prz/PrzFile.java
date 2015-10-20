package prz;

import java.io.File;

/**
 * <p>Title: PrzFile</p>
 * <p>PRZ file Utility.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class PrzFile extends File{

	private String directory = null;
	private String error = null;
	
    static String xDirectoryNotSpecified   = "ERROR: There was no directory specified. A directory containing a PRZ file must be specified.";
    static String xDirectoryNotFound       = "ERROR: The directory specified does not exist. A valid directory path containing a prz file must be specified.";
    static String xNotDirectory            = "ERROR: The directory specified is not a valid directory. A valid directory containing a prz file must be specified.";
    static String xNoFilesFound            = "ERROR: The directory specified does not contain any files. A directory containing a single PRZ file must be specified.";

	public PrzFile(String dir){
		super(dir);
		directory = dir;
	}
	
	public String getName(){
		
        // return error if the directory contains no files
        if ( this.listFiles().length == 0 ){
        	error = xNoFilesFound;	
        	return null;
        }	

		File[] filelist = this.listFiles();
		return filelist[0].getName();
	}	
	
	
	public String getPath(){
		
        // check if full path  
        if ( !directory.endsWith("/") )
            return(directory + "/");
        	
		return directory;
	}
	
	public boolean isValid(){
		
        // return error if directory was not specified
        if ( directory == null ) {
            error = xDirectoryNotSpecified;
            return false;
        }

        // return if the directory does not exist
        if ( !this.exists() ){
        	error = xDirectoryNotFound;
        	return false;
        }

        // return error if the directory is not a directory
        if ( !this.isDirectory() ){
        	error = xNotDirectory;
        	return false;
        }

        // return error if the directory contains no files
        if ( this.listFiles().length == 0 ){
        	error = xNoFilesFound;	
        	return false;
        }

		return true;
	}
	
	public String error(){
		return error;
	}
}
