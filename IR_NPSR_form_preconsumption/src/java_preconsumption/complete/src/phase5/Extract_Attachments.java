package phase5;

import prz.PrzFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.adobe.pdf.FileAttachment;
import com.adobe.pdf.PDFDocument;
import com.adobe.pdf.PDFFactory;
import com.adobe.pdf.exceptions.PDFException;

/**
 * <p>Title: Extract_Attachments</p>
 * <p>Extracts attachments, if there are any, from the pdf form xml file.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class Extract_Attachments {

    /**
     * checkAttachments.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String checkAttachments( PrzFile prz_directory ){
		return attachments(prz_directory);
	}
	
    /**
     * checkAttachments.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String checkAttachments( String directory ){
		PrzFile prz_directory = new PrzFile(directory);
		return attachments(prz_directory);
	}	
	
    /**
     * attachments.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if the eindex.xml file persed successfully or not.
     */
	public static String attachments(PrzFile prz_directory) {

		// global variables
		final String pdf_dir_name = "PDF";
		final String zip_dir_name = "ZIP";
		final String prz_ext = ".prz";
		final String IR_form = "IR.pdf";
		final String NPSR_form = "NPSR.pdf";
		final String pass_log = "_PASS.txt";
		final String fail_log = "_FAIL.txt";

		File pdf_file = null;
		PDFDocument pdf_doc = null;
		File[] file_list = null;
		InputStream inputStream = null;
		byte[][] names = null;
		FileInputStream in_xfapdf = null;
		FileAttachment embeddedFile = null;
		FileOutputStream outStream = null;
		byte[] buffer = new byte[10240];
		StringBuffer log	    = new StringBuffer();

		// error messages
		String xFormDirectoriesCreated 			= "INFO: Successfully checked for PDF attachments.";
		String xNoFoldersFound 					= "ERROR: NoFoldersFound: The specified directory contains no folders (should at least be an IRX0 folder).";
		String xFormNotFound 					= "ERROR: FormNotFound: The required PDF Form could not be found";
		String xPDFFileNotFound         		= "ERROR: FileNotFoundException: An error was encountered while attempting to read a specified PDF File.";
		String xCreatePDFDocument       		= "ERROR: PDFException: An error was encountered while attempting to open/read the specified PDF Document.";
		String xIOCreatePDFDocument     		= "ERROR: IOCreatePDFDocument: An error was encountered while attempting to open/read the specified PDF Document.";
		String xCreateAttachmentDocument 		= "ERROR: CreateAttachmentDocument: An error was encountered while attempting to create an Attachment document.";
		String xIOCreateAttachmentDocument 		= "ERROR: IOCreateAttachmentDocument: An error was encountered while attempting to create an Attachment document.";
		String xIOInputAttachmentDocument 		= "ERROR: IOInputAttachmentDocument: An error was encountered while attempting to read the Attachment document.";
		String xIOOutputAttachmentDocument 		= "ERROR: IOOutputAttachmentDocument: An error was encountered while attempting to write the Attachment document.";
		String xIOCloseOutputAttachmentDocument = "ERROR: IOCloseOutputAttachmentDocument: An error was encountered while attempting to close the output stream for the Attachment document.";
		String xIOAttachmentFileNotFound 		= "ERROR: IOAttachmentFileNotFound: An error was encountered while attempting to create the Attachment file.";
		String xIOCloseInputAttachmentDocument 	= "ERROR: IOCloseInputAttachmentDocument: An error was encountered while attempting to close the input stream for the Attachment.";
		

        System.out.println("CHECK AND EXTRACT PDF ATTACHMENTS");
        log.append("CHECK AND EXTRACT PDF ATTACHMENTS\r\n");
		
        // return error if directory was not specified
        if( ! prz_directory.isValid() ){
        	System.out.println(""+prz_directory.error());
        	log.append(""+prz_directory.error()+"\r\n");
        	return log.toString();
        }

		// Generate list of files contained in the specified directory
		file_list = prz_directory.listFiles();

		// return error if the directory contains no files
		if (file_list.length == 0){
			System.out.println(xNoFoldersFound);
        	log.append(xNoFoldersFound+"\r\n");
        	return log.toString();
		}

		for (int c = 0; c < file_list.length; c++) {
			// get form directories and skip the prz file and log files
			if (file_list[c].isDirectory()
					&& !file_list[c].getName().endsWith(prz_ext)
					&& !file_list[c].getName().endsWith(pass_log)
					&& !file_list[c].getName().endsWith(fail_log)) {

				// get pdf file
				pdf_file = new File(file_list[c].getPath() + "/" + zip_dir_name + "/" + IR_form);

				if (!pdf_file.exists()){
					pdf_file = new File(file_list[c].getPath() + "/" + zip_dir_name + "/" + NPSR_form);
				}

				// return error if pdf file dose not exist
				if (!pdf_file.exists()){
					System.out.println(xFormNotFound);
		        	log.append(xFormNotFound+"\r\n");
		        	return log.toString();
				}

				// read in pdf
				try {
					in_xfapdf = new FileInputStream(pdf_file.getPath());
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

				// get list of file attachments
				names = pdf_doc.getFileAttachmentNames();

				if (names != null){

					//System.out.println("INFO: Attachments found for /"+file_list[c].getName()+"/" + zip_dir_name + "/" +pdf_file.getName()+" file.");
			        log.append("INFO: Attachments found for /"+file_list[c].getName()+"/" + zip_dir_name + "/" +pdf_file.getName()+" file.\r\n");						

					for (int i = 0; i < names.length; i++) {
	
						try {
							// get file attachment
							embeddedFile = pdf_doc.exportFileAttachment(names[i]);
						} 
						catch (PDFException e) {
							System.out.println(xCreateAttachmentDocument);
				        	log.append(xCreateAttachmentDocument+"\r\n");
				        	return log.toString();
						} 
						catch (IOException e) {
							System.out.println(xIOCreateAttachmentDocument);
				        	log.append(xIOCreateAttachmentDocument+"\r\n");
				        	return log.toString();
						}
						
						// output attachment file name
						String attachment_output_file = file_list[c].getPath() + "/" + pdf_dir_name+ "/" + embeddedFile.getFilename();
						inputStream = embeddedFile.getData();
	
						try {
							outStream = new FileOutputStream(attachment_output_file);
							int len = 0;
							
							while (true) {
								try {
									len = inputStream.read(buffer);
								} 
								catch (IOException e) {
									System.out.println(xIOInputAttachmentDocument);
						        	log.append(xIOInputAttachmentDocument+"\r\n");
						        	return log.toString();
								}
								
								if (len == -1) {
									break;
								}
								
								try {
									outStream.write(buffer, 0, len);
								} 
								catch (IOException e) {
									System.out.println(xIOOutputAttachmentDocument);
						        	log.append(xIOOutputAttachmentDocument+"\r\n");
						        	return log.toString();
								}
							}
							
							try {
								outStream.close();
							} 
							catch (IOException e) {
								System.out.println(xIOCloseOutputAttachmentDocument);
					        	log.append(xIOCloseOutputAttachmentDocument+"\r\n");
					        	return log.toString();
							}
							
						} 
						catch (FileNotFoundException e) {
							System.out.println(xIOAttachmentFileNotFound);
				        	log.append(xIOAttachmentFileNotFound+"\r\n");
				        	return log.toString();
						}
						
						try 
						{
							inputStream.close();
						} 
						catch (IOException e) {
							System.out.println(xIOCloseInputAttachmentDocument);
				        	log.append(xIOCloseInputAttachmentDocument+"\r\n");
				        	return log.toString();
						}
						
						System.out.println("INFO: Successfully saved attachment "+embeddedFile.getFilename()+" to the /"+ file_list[c].getName() +"/"+ pdf_dir_name +"/ directory.");
				        log.append("INFO: Successfully saved attachment "+embeddedFile.getFilename()+" to the /"+ file_list[c].getName() +"/"+ pdf_dir_name +"/ directory.\r\n");
					}
				}
				else{
					System.out.println("INFO: There are no attachments associated with the "+file_list[c].getName() +"/"+ pdf_dir_name +"/ file.");
			        log.append("INFO: There are no attachments associated with the "+file_list[c].getName()+"/"+ pdf_dir_name +"/ file.\r\n");
				}
			}
		}

        System.out.println("INFO: CHECK AND EXTRACT PDF ATTACHMENTS COMPLETE");
        System.out.println(" ");
        log.append(xFormDirectoriesCreated+"\r\n"); 		
        log.append("INFO: COMPLETE\r\n");
        log.append("\r\n");
        
        return log.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FileWriter msg_writer = null;
		StringBuffer success = new StringBuffer();
		String dir = null;
		String out_file = null;
		String result = null;

		if (args.length == 1) {
			dir = args[0];
		} else {
			System.err.println("INFO: No base directory specified");
			System.err.println("Usage: java -jar phase4.jar [directory path]");
			System.exit(-1);
		}

		result = checkAttachments(dir);
        success.append( result+"\r\n" );	
		
		if (success.toString().indexOf("ERROR:") >= 0) {
			out_file = "PHASE_5_FAIL.txt";
			File pass_file = new File(dir + "/" + "PHASE_5_PASS.txt");
			
			if (pass_file.exists())
				pass_file.delete();
						
		} 
		else {
			out_file = "PHASE_5_PASS.txt";
			File fail_file = new File(dir + "/" + "PHASE_5_FAIL.txt");
			
			if (fail_file.exists())
				fail_file.delete();
		}

		try {
			msg_writer = new FileWriter(dir + "/" + out_file);
			msg_writer.write(success + "\r\n");
			msg_writer.flush();
			msg_writer.close();
		} 
		catch (IOException e) {
			System.err.println("ERROR: IOException: A error was encountered in Main attempting to write log file.");
		}
	}
}