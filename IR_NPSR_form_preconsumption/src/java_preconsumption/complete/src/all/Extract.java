package all;

import phase1.Extract_Archive;
import phase2.Extract_eIndex;
import phase3.Extract_Files;
import phase4.Extract_FormData;
import phase5.Extract_Attachments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>Title: Extract</p>
 * <p>Description: Runs all phases of the form extraction process: Phase 1-Extract
 * Archive, Phase 2-Parse eindex file, Phase 3-Create result directories, Phase
 * 4-Extract form xml and Phase 5 extract attachments</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */
public class Extract {

	/**
	 * @param args an absolute path giving the base location of the archive file
	 */
	public static void main(String[] args) {

		FileWriter msg_writer = null;
		StringBuffer success = new StringBuffer();
		String dir = null;
		String out_file = null;
		String schemaName = null;
		boolean phase4 = false;
		
		if (args.length == 1) {
			dir = args[0];
		} 
		else if (args.length == 2) {
			dir = args[0];
			schemaName = args[1];
		} 
		else {
			System.err.println("No base directory specified");
			System.err.println("Usage: java -jar extract.jar [directory path] [XML Schema path]");
			System.exit(-1);
		}
		
		success.append(Extract_Archive.unZip(dir));

		if (!success.toString().contains("ERROR"))
			success.append("\r\n" + Extract_eIndex.parseXML(dir));

		if (!success.toString().contains("ERROR"))
			success.append("\r\n" + Extract_Files.extractFiles(dir));

		if (!success.toString().contains("ERROR")){
			success.append("\r\n" + Extract_FormData.extractXML(dir, schemaName));
			phase4 = true;
		}
		
		if (phase4){
			success.append("\r\n" + Extract_Attachments.checkAttachments(dir));
		}
				
		if (success.toString().contains("ERROR")) {
			out_file = "FAIL.txt";
			File pass_file = new File(dir + "/" + "PASS.txt");
			
			System.out.println("EXTRACTION COMPLETED WITH ERRORS\r\n");
			System.out.println(" ");
			success.append("EXTRACTION COMPLETED WITH ERRORS\r\n");
			success.append("\r\n");
			
			if (pass_file.exists())
				pass_file.delete();
		} 
		else {
			out_file = "PASS.txt";
			File fail_file = new File(dir + "/" + "FAIL.txt");
			
			System.out.println("EXTRACTION COMPLETED SUCCESSFULLY\r\n");
			System.out.println(" ");
			success.append("EXTRACTION COMPLETED SUCCESSFULLY\r\n");
			success.append("\r\n");
			
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