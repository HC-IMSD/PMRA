package phase2;

import prz.PrzFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * <p>Title: Parse_eIndex</p>
 * <p>Description: Used to varify that files specified in the eindex file under  
 * the attribute idref='DM_EFILE_NAME' exist and are all accounted for .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class Extract_eIndex {

    /**
     * parseXML.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String parseXML( PrzFile prz_directory ){
		return parse(prz_directory);
	}
	
    /**
     * parseXML.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if files and directories were successfully created or not.
     */
	public static String parseXML( String directory ){
		PrzFile prz_directory = new PrzFile(directory);
		return parse(prz_directory);
	}
	
    /**
     * parse.
     * @param directory an absolute path giving the base location of the archive file
     * @return a String indicating if the eindex.xml file parsed successfully or not.
     */
    public static String parse( PrzFile prz_directory ) {
        
        // global variables
        final String filename = "eindex.xml";
        final String xpath = "//field[@idref='DM_EFILE_NAME']/value";                      

        File eindex                       = null;
        File pdf_file                     = null;        
        DocumentBuilderFactory docFactory = null;
        DocumentBuilder docBuild          = null;
        Document eindex_doc               = null;
        Element efile_element             = null;
        Text efile_textNode               = null;
        String pdf_name                   = null;
        NodeList efile_list               = null;
        StringBuffer log	              = new StringBuffer();

        // info/error messages        
        String xeIndexParsed            = "INFO: Successfully parsed eindex XML File. The eindex XML file is Valid.";
        String xFileNotFound            = "ERROR: FileNotFound: An error was encountered while attempting to open the specifies eindex.xml file.";
        String xCreateDocBuilder        = "ERROR: ParserConfigurationException: An error was encountered while attempting to produce the DOM object trees from the eindex.xml document.";
        String xSaxParsingXML           = "EEROR: SAXException: An error was encountered while attempting to parse the specified eindex.xml file.";
        String xIOParsingXML            = "ERROR: IOException: An error was encountered while attempting to open the specified eindex.xml file.";
        String xXPathError              = "ERROR: TransformerException: An error was encountered while attempting to find the idref='DM_EFILE_NAME' attribute in the eindex.xml document.";
        String xRefAttributeNotFound    = "ERROR: RefAttributeNotFound: An error was encountered while attempting to find the idref='DM_EFILE_NAME' attribute in the eindex.xml document.";
        String xElementNotFound         = "ERROR: ElementNotFound: The selected eindex element is NULL.";
        String xNotElementType          = "ERROR: NotElementType: The selected eindex node is not of type Element.";
        String xNotTextType             = "ERROR: NotTextType: The selected eindex node is not of type Text.";
        String xAttributeNotFound       = "ERROR: AttributeNotFound: The idref='DM_EFILE_NAME' attribute text element could not be found.";
        String xAttachedFileNotFound    = "ERROR: AttachedFileNotFound: The attached file specified by the idref='DM_EFILE_NAME' attribute can not be found or is missing.";
        
        System.out.println("INFO: PARSE eindex.xml FILE");
        log.append("INFO: PARSE eindex.xml FILE \r\n");
        
        if( ! prz_directory.isValid() ){
        	System.out.println(""+prz_directory.error());
        	log.append(""+prz_directory.error()+"\r\n");
        	return log.toString();     
        }

        eindex = new File(prz_directory + filename);
        
        // return error if the e-index file does not exist
        if ( !eindex.exists() ){
        	System.out.println(xFileNotFound);
        	log.append(xFileNotFound+"\r\n");
        	return log.toString(); 
        }
        
        docFactory = DocumentBuilderFactory.newInstance();
        
        // setup document builder
        try {
            docBuild = docFactory.newDocumentBuilder();
        } 
        catch (ParserConfigurationException e) {
        	System.out.println(xCreateDocBuilder);
        	log.append(xCreateDocBuilder+"\r\n");
        	return log.toString(); 
        }
        
        // parse eindex.xml document
        try {
            eindex_doc = docBuild.parse(eindex);
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

        // find all elements with the idref='DM_EFILE_NAME' attribute
        try {
            efile_list = XPathAPI.selectNodeList(eindex_doc, xpath);
        } 
        catch (TransformerException e) {
        	System.out.println(xXPathError);
        	log.append(xXPathError+"\r\n");
        	return log.toString(); 
        }
        
        // return error if no elements with the idref='DM_EFILE_NAME' attribute found
        if( efile_list.getLength() == 0 ){
        	System.out.println(xRefAttributeNotFound);
        	log.append(xRefAttributeNotFound+"\r\n");
        	return log.toString(); 
        }
        
        // look at each element returned by xpath query
        for ( int i=0; i<efile_list.getLength(); i++ ) {
            
            // return error if element in list is null
            if( (Element)efile_list.item(i) == null ){
            	System.out.println(xElementNotFound);
            	log.append(xElementNotFound+"\r\n");
            	return log.toString(); 
            }
            
            // return error if not of element type
            if( efile_list.item(i).getNodeType() != Node.ELEMENT_NODE ){
            	System.out.println(xNotElementType);
            	log.append(xElementNotFound+"\r\n");
            	return log.toString();       
            }
                
            efile_element = (Element)efile_list.item(i);
            
            // return error if element has no children
            if( efile_element.getFirstChild() == null ){
            	System.out.println(xAttributeNotFound);
            	log.append(xAttributeNotFound+"\r\n");
            	return log.toString();
            }
            
            // return error if first child is not of text type
            if( efile_element.getFirstChild().getNodeType()!= Node.TEXT_NODE ){
            	System.out.println(xNotTextType);
            	log.append(xNotTextType+"\r\n");
            	return log.toString();
            }

            efile_textNode = (Text)efile_element.getFirstChild();
            pdf_name = efile_textNode.getData();
            pdf_file = new File(prz_directory.getPath()+pdf_name);
            
            // return error if specified file does not exist
            if( !pdf_file.exists() ){
            	System.out.println(xAttachedFileNotFound);
            	log.append(xAttachedFileNotFound+"\r\n");
            	return log.toString();
            }
        }
        
        System.out.println(xeIndexParsed);
        System.out.println("INFO: PARSE eindex.xml FILE COMPLETE");  
        System.out.println(" ");
        log.append(xeIndexParsed+"\r\n");        
        log.append("INFO: PARSE eindex.xml FILE COMPLETE");
        log.append("\r\n");

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
        String result         = null;

        if( args.length == 1 ){
            dir = args[0];
        }
        else{
        	System.err.println("ERROR: No base directory specified");
        	System.err.println("Usage: java -jar phase2.jar [directory path]");
        	System.exit(-1);
        }
        
        result = parseXML(dir);
        success.append( result+"\r\n" );     

        if( success.toString().indexOf("ERROR:") > 0 ){
            out_file = "PHASE_2_FAIL.txt";
            File pass_file = new File(dir+"/"+"PHASE_2_PASS.txt");
            
            if(pass_file.exists())
                pass_file.delete();
        }
        else{
            out_file = "PHASE_2_PASS.txt";
            File fail_file = new File(dir+"/"+"PHASE_2_FAIL.txt");
           
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