package phase4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import prz.PrzFile;
import xml.XsdSchemaValidator;

import com.adobe.pdf.FormDataFormat;
import com.adobe.pdf.FormType;
import com.adobe.pdf.PDFDocument;
import com.adobe.pdf.PDFFactory;
import com.adobe.pdf.exceptions.PDFException;
import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * <p>Title: Extract_FormData</p>
 * <p>Extracts the form xml from pdf file.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class Extract_FormData {

    /**
     * extractXML.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String extractXML( PrzFile prz_directory, String schemaName ){
		return extract(prz_directory, schemaName);
	}
	
    /**
     * extractXML.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String extractXML( String directory, String schemaName ){
		PrzFile prz_directory = new PrzFile(directory);
		return extract(prz_directory, schemaName);
	}	
	
    /**
     * extract.
     * @param directory an absolute path giving the base location of the archive file
     * @param directory an absolute path giving the base location of the XML Schema file
     * @return a String indicating if the eindex.xml file persed successfully or not.
     */
	public static String extract(PrzFile prz_directory, String schemaName) {

		// global variables
		final String pdf_form 		= "untype.xfa.pdf";
		final String pdf_dir_name 	= "PDF";
		final String zip_dir_name 	= "ZIP";
		final String prz_ext 		= ".prz";
		final String form_xml 		= "FormData.xml";
		final String IR_form 		= "IR.pdf";
		final String NPSR_form 		= "NPSR.pdf";
		final String NODE_NAME_IR 	= "IRep";
		final String NODE_NAME_NPSR = "npsr";
		final String pass_log 		= "_PASS.txt";
		final String fail_log 		= "_FAIL.txt";

		File pdf_file 						= null;
		File[] file_list					= null;
		FileInputStream in_xfapdf 			= null;
		PDFDocument pdf_doc 				= null;
		DocumentBuilderFactory docFactory 	= null;
		DocumentBuilder docBuild 			= null;
		File formxml_file 					= null;
		StringBuffer log	        		= new StringBuffer();
		InputStream pdf_doc_stream 			= null;
		Writer out 							= null;

		// error messages
		String xFormDirectoriesCreated 	= "INFO: Successfully extracted PDF Form XML.";
		String xNoFoldersFound 			= "ERROR: NoFoldersFound: The specified directory contains no folders (should at least be an IRX0 folder).";
		String xFileNotFound 			= "ERROR: FileNotFound: A specified file does not exist.";
		String xPDFFileNotFound 		= "ERROR: PDFFileNotFound: The specified PDF File could not be read.";
		String xCreatePDFDocument 		= "ERROR: CreatePDFDocument: The specified PDF File could not be read.";
		String xIOCreatePDFDocument 	= "ERROR: IOCreatePDFDocument: The specified PDF File could not be opened.";
		String xIOClosePDFOutStream 	= "ERROR: IOClosePDFOutStream: The specified PDF File could not be closed.";
		String xCreateDocBuilder 		= "ERROR: CreateDocBuilder: PDF Document builder factory could not be created.";
		String xSaxParsingXML 			= "ERROR: SaxParsingXML: The PDF Form XML could not be parsed.";
		String xIOParsingXML 			= "ERROR: IOParsingXML: The PDF Form XML could not be parsed.";
		String xFileRename 				= "ERROR: FileRename: Could not rename PDF File.";
		
		String xPDFXMLStream 			= "ERROR: PDFXMLStream: Could not open form xml stream and extract XML.";
		String xIOXMLStream 			= "ERROR: IOXMLStream: Could not write form xml stream to file.";
		String xTransformerException1	= "ERROR: XMLNameSpace1: An error occured performing an xpath lookup for /IRep/SUBTAA/IR_S_FRM_IND.";
		String xTransformerException2	= "ERROR: XMLNameSpace2: An error occured performing an xpath lookup";
		
		
        System.out.println("INFO: EXTRACT PDF FORM XML");
        log.append("INFO: EXTRACT PDF FORM XML\r\n");
        
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
		
        XsdSchemaValidator schemaValidator = new XsdSchemaValidator();
        Schema xmlSchema = schemaValidator.loadSchema(schemaName);
		int cnt =0;
		
		// check if any files were left behind
		for (int c = 0; c < file_list.length; c++) {

			// get form directories and skip the prz file and log files
			if (file_list[c].isDirectory()
					&& !file_list[c].getName().endsWith(prz_ext)
					&& !file_list[c].getName().endsWith(pass_log)
					&& !file_list[c].getName().endsWith(fail_log)) {

				// get pdf file
				pdf_file = new File(file_list[c].getPath() + "/" + zip_dir_name+ "/" + pdf_form);

				// return error if 'pdf_file' file dose not exist
				if (!pdf_file.exists()){
					System.out.println(xFileNotFound);
		        	log.append(xFileNotFound+"\r\n");
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

				// close pdf input stream
				try {
					in_xfapdf.close();
				} 
				catch (IOException e) {
					System.out.println(xIOClosePDFOutStream);
		        	log.append(xIOClosePDFOutStream+"\r\n");
		        	return log.toString();
				}

				// if pdf is a xml Form
				if (pdf_doc.getFormType() == FormType.XML_FORM) {

					cnt++;
		            log.append("INFO: FILE "+cnt+"\r\n");
		            System.out.println("INFO: FILE "+cnt);

					try {
						// open form xml stream
						pdf_doc_stream = pdf_doc.exportFormData(FormDataFormat.XFA);

						// open xml outputstream
						formxml_file = new File(file_list[c].getPath() + "/"+ pdf_dir_name + "/" + form_xml);
				        
						//System.out.println("    INFO: Successfully extracted '"+form_xml+"' from PDF "+file_list[c].getPath() + "/"+ pdf_dir_name + "/" +pdf_form+".");
				        log.append("    INFO: Successfully extracted '"+form_xml+"' from PDF "+file_list[c].getPath() + "/"+ pdf_dir_name + "/" + pdf_form+".\r\n");					

						out = new OutputStreamWriter(new FileOutputStream(formxml_file), "UTF-8");

						// write form xml to xml file
						int x;
						while ((x = pdf_doc_stream.read()) != -1) {
							out.write(x);
						}

						out.close();
						pdf_doc_stream.close();	
						
						//System.out.println("    INFO: Successfully saved '"+form_xml+"' to "+file_list[c].getPath() + "/"+ pdf_dir_name + "/.");
				        log.append("    INFO: Successfully saved '"+form_xml+"' to "+ file_list[c].getPath() + "/"+ pdf_dir_name + "/" +".\r\n");
					}
					catch (PDFException e1) {
						System.out.println(xPDFXMLStream);
			        	log.append(xPDFXMLStream+"\r\n");
			        	return log.toString();
					} 
					catch (IOException e1) {
						System.out.println(xIOXMLStream);
						return xIOXMLStream;
					}

					docFactory = DocumentBuilderFactory.newInstance();
					docBuild = null;

					try {
						docBuild = docFactory.newDocumentBuilder();
					} 
					catch (ParserConfigurationException e) {
						System.out.println(xCreateDocBuilder);
			        	log.append(xCreateDocBuilder+"\r\n");
			        	return log.toString();
					}

					Document doc = null;
					try {
						doc = docBuild.parse(formxml_file);
					} 
					catch (SAXException e) {
						System.out.println(xSaxParsingXML);
			        	log.append(xSaxParsingXML+"\r\n");
			        	return log.toString();
					} 
					catch (IOException e) {
						System.out.println(xIOParsingXML);
			        	log.append(xIOParsingXML+"\r\n");
			        	return log.toString();
					}

					Element root = doc.getDocumentElement();
					String rootName = root.getTagName();
					boolean success = false;
					String oldpdfname = pdf_file.getName();

					if (rootName == NODE_NAME_IR){
				        
						boolean sci_study = false;
						// IR_S_FRM_IND
						Node IR_S_FRM_IND;
						try {
							IR_S_FRM_IND = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_S_FRM_IND");
							if (IR_S_FRM_IND != null && IR_S_FRM_IND instanceof Element) {
								String IR_REGT_REFtxt = IR_S_FRM_IND.getTextContent();
								
								if(IR_REGT_REFtxt.matches("[y|Y]{0,1}|[n|N]{0,1}")){
									sci_study = true;
								}
							}
						
						} catch (TransformerException e1) {
							System.out.println(xTransformerException1);
				        	log.append(xTransformerException1+"\r\n");
				        	return log.toString();
						}

						String XSDinfo = schemaValidator.validateXml(xmlSchema, formxml_file.getPath(), sci_study);
				        log.append(XSDinfo);

				        if(XSDinfo.contains("XSD_ERROR:")){
							try {
								
								Node IR_REGT_REF = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_REGT_REF");
								Node IR_REGT_NAME = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_REGT_NAME");
								Node IR_CNTCT_NAME = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_CNTCT_NAME");
								Node IR_CNTCT_TEL = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_CNTCT_TEL");
								Node IR_CNTCT_EMAIL = XPathAPI.selectSingleNode(doc, "/IRep/SUBTAA/IR_CNTCT_EMAIL");
								
					            if (IR_REGT_REF != null && IR_REGT_REF instanceof Element) {               
					                   String IR_REGT_REFtxt = IR_REGT_REF.getTextContent();
					                   log.append("    ERROR: REGISTRANT REFERENCE NUMBER: "+IR_REGT_REFtxt+"\r\n");
					                   System.out.println("    ERROR: REGISTRANT REFERENCE NUMBER: "+IR_REGT_REFtxt);
					            } 
								
					            if (IR_REGT_NAME != null && IR_REGT_NAME instanceof Element) {               
					                   String IR_REGT_NAMEtxt = IR_REGT_NAME.getTextContent();
					                   log.append("    ERROR: REGISTRANT COMPANY: "+IR_REGT_NAMEtxt+"\r\n");
					                   System.out.println("    ERROR: REGISTRANT COMPANY: "+IR_REGT_NAMEtxt);
					            }     
					            
					            if (IR_CNTCT_NAME != null && IR_CNTCT_NAME instanceof Element) {               
					                   String IR_CNTCT_NAMEtxt = IR_CNTCT_NAME.getTextContent();
					                   log.append("    ERROR: REGISTRANT CONTACT: "+IR_CNTCT_NAMEtxt+"\r\n");
					                   System.out.println("    ERROR: REGISTRANT CONTACT: "+IR_CNTCT_NAMEtxt);
					            }   
					            
					            if (IR_CNTCT_TEL != null && IR_CNTCT_TEL instanceof Element) {               
					                   String IR_CNTCT_TELtxt = IR_CNTCT_TEL.getTextContent();
					                   log.append("    ERROR: REGISTRANT TEL: "+IR_CNTCT_TELtxt+"\r\n");
					                   System.out.println("    ERROR: REGISTRANT TEL: "+IR_CNTCT_TELtxt);
					            }   
					            
					            if (IR_CNTCT_EMAIL != null && IR_CNTCT_EMAIL instanceof Element) {               
					                   String IR_CNTCT_EMAILtxt = IR_CNTCT_EMAIL.getTextContent();
					                   log.append("    ERROR: REGISTRANT EMAIL: "+IR_CNTCT_EMAILtxt+"\r\n");
					                   System.out.println("    ERROR: REGISTRANT EMAIL: "+IR_CNTCT_EMAILtxt);
					            }   
								
							} catch (TransformerException e) {
								System.out.println(xTransformerException2);
					        	log.append(xTransformerException2+"\r\n");
					        	return log.toString();
							}	
				        }
				        
						success = pdf_file.renameTo(new File(file_list[c].getPath() + "/" + zip_dir_name + "/" + IR_form));
						//System.out.println("    INFO: Successfully renamed pdf file '"+oldpdfname+"' to '"+ file_list[c].getPath() + "/" + zip_dir_name + "/" + IR_form +"'.");
				        log.append("    INFO: Successfully renamed pdf file '"+oldpdfname+"' to '"+ file_list[c].getPath() + "/" + zip_dir_name + "/" + IR_form +"'.");
				        log.append("\r\n");
					}

					if (rootName == NODE_NAME_NPSR){
						
						String XSDinfo = schemaValidator.validateXml(xmlSchema, formxml_file.getPath());
				        log.append(XSDinfo);

						success = pdf_file.renameTo(new File(file_list[c].getPath() + "/" + zip_dir_name + "/" + NPSR_form));
						//System.out.println("    INFO: Successfully renamed pdf file '"+oldpdfname+"' to '"+ file_list[c].getPath() + "/" + zip_dir_name + "/" + NPSR_form +"'.");
				        log.append("    INFO: Successfully renamed pdf file '"+oldpdfname+"' to '"+ file_list[c].getPath() + "/" + zip_dir_name + "/" + NPSR_form +"'.");
				        log.append("\r\n");
					}

					if (!success){
						System.out.println(xFileRename);
			        	log.append(xFileRename+"\r\n");
			        	return log.toString();
					}
				}
			}
		}

        System.out.println(xFormDirectoriesCreated);
        System.out.println("INFO: EXTRACT PDF FORM XML COMPLETE");
        System.out.println(" ");
        log.append(xFormDirectoriesCreated+"\r\n");       
        log.append("INFO: EXTRACT PDF FORM XML COMPLETE");
        log.append("\r\n");
        
        return log.toString();		
	}
	
	/**
	 * @param args an absolute path giving the base location of the archive file
	 */
	public static void main(String[] args) {

		FileWriter msg_writer = null;
		StringBuffer success = new StringBuffer();
		String dir = null;
		String out_file = null;
		String result = null;
		String schemaName = null;

		//System.out.println("args[0]"+args[0]);
		//System.out.println("args[1]"+args[1]);
		
		if (args.length == 1) {
			dir = args[0];
		} else if (args.length == 2) {
			dir = args[0];
			schemaName = args[1];
		} else {
			System.err.println("ERROR: No base directory specified");
			System.err.println("Usage: java -jar extract.jar [directory path] [XML Schema path]");
			System.exit(-1);
		}

        result = extractXML(dir, schemaName);
        success.append(result+"\r\n");	

		if (success.toString().indexOf("ERROR:") > 0) {
			out_file = "PHASE_4_FAIL.txt";
			File pass_file = new File(dir + "/" + "PHASE_4_PASS.txt");
			
			if (pass_file.exists())
				pass_file.delete();
		} 
		else 
		{
			out_file = "PHASE_4_PASS.txt";
			File fail_file = new File(dir + "/" + "PHASE_4_FAIL.txt");
			
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