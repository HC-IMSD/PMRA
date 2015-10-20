package xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>Title: XSDErrorHandler</p>
 * <p>XML Schema Error Handler Utility.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.1
 */

public class XSDErrorHandler implements ErrorHandler{

	private static int errorCount = 0;
	String log = "";
	String setType = null;
	
	public void warning(SAXParseException e) throws SAXException {
		printException("     WARNING: ", e);
		log = log+"    WARNING: "+ e.getMessage()+"\r\n";
	}

	public void error(SAXParseException e) throws SAXException {
		//TODO rework this mess
		if(!e.getMessage().startsWith("cvc-pattern-valid:") && !e.getMessage().startsWith("cvc-enumeration-valid")){
			if(!e.getMessage().contains("IR_SITE_CDE") && !e.getMessage().contains("SITE_") && !e.getMessage().contains("IR_A_AGE_RANGE") 
					&& !e.getMessage().contains("IR_A_WEIGHT")&& !e.getMessage().contains("IR_A_SYMTOM_DURATION") && !e.getMessage().contains("IR_FRML_LIQ_IND")
					&& !e.getMessage().contains("IR_H_SYMTOM_DURATION") && !e.getMessage().contains("SUBTAAA_IR_PROD_ACTV_USER")
					&& !e.getMessage().contains("IR_P_PCKG") ){
				if(setType.equals(setType)){
					if(!e.getMessage().contains("IR_DTE_FIRST_INF") && !e.getMessage().contains("IR_CNTY") && !e.getMessage().contains("IR_PROV")){
						printException("    XSD_ERROR: ", e);
						log = log+"    XSD_ERROR: "+ e.getMessage()+"\n";						
					}
				}
				else{
					printException("    XSD_ERROR: ", e);
					log = log+"    XSD_ERROR: "+ e.getMessage()+"\n";				
				}
			}
		}
	}

	public void fatalError(SAXParseException e) throws SAXException {
		printException("    FATAL ERROR: ", e);
		log = log+"    FATAL ERROR: "+ e.getMessage()+"\r\n";
	}
	
	public int getErrorCnt(){
		return errorCount;
	}
	
	public void clearErrorCnt(){
		errorCount = 0;
	}
	
	public String getErrorLog(){
		return log;
	}
	
	public void setType(String type){
		setType = type;
	}
	
	private void printException(String msg, SAXParseException e) {
		errorCount++;
		System.out.println(msg + e.getMessage());
	}
}