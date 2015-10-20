package xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;

/**
 * <p>Title: XsdSchemaValidator</p>
 * <p>XML Schema Validator Utility.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class XsdSchemaValidator {
	
	String xsdlog = null;
	boolean sci_study = false;
	
	
	public XsdSchemaValidator(){}
	
	public String validateXml(Schema schema, String xmlName, boolean type) {
		sci_study = type;
		return validateXml(schema, xmlName);
	}
	
	public String validateXml(Schema schema, String xmlName) {
		try {

			// creating a Validator instance
			Validator validator = schema.newValidator();

			// setting my own error handler
			XSDErrorHandler errorhandler = new XSDErrorHandler();
			
			if(sci_study){
				errorhandler.setType("sci_study");
			}
			
			validator.setErrorHandler(errorhandler);
			errorhandler.clearErrorCnt();

			// preparing the XML file as a SAX source
			SAXSource source = new SAXSource(new InputSource(new java.io.FileInputStream(xmlName)));

			// validating the SAX source against the schema
			validator.validate(source);
			
			if (errorhandler.getErrorCnt() > 0) {
				System.out.println("    ERROR: "+xmlName+" does not conform to the specified XML Schema and Failed with errors.");
				System.out.println("    ERROR: Contact Registrant:");
				
				xsdlog = errorhandler.getErrorLog();
				xsdlog = xsdlog+"    ERROR: "+xmlName+" does not conform to the specified XML Schema and Failed with errors.\r\n";
				xsdlog = xsdlog+"    ERROR: Contact Registrant:\r\n";
			} 
			else {
				System.out.println("    INFO: Successfully conforms to the specified XML Schema for file "+xmlName+".");
				xsdlog = "    INFO: Successfully conforms to the specified XML Schema for file "+xmlName+".\r\n";
			}
		} 
		catch (Exception e) {
			// catching all validation exceptions
			System.out.println();
			System.out.println(e.toString());
		}
		
		return xsdlog;
	}

	public Schema loadSchema(String name) {
		
		Schema schema = null;
		
		try {
			String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory factory = SchemaFactory.newInstance(language);
			schema = factory.newSchema(new File(name));
		} 
		catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return schema;
	}
}