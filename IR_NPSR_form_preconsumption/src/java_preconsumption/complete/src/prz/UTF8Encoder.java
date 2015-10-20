package prz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * <p>Title: UTF8Encoder</p>
 * <p>Encode XML Utility.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Health Canada|Santé Canada, Solutions Centre, Information Management Services Directorate | Direction des services de gestion de l'information</p>
 * 
 * @author Chris Senack
 * @version 2.0
 */

public class UTF8Encoder {

	public UTF8Encoder(){}
	
	public static void main(String[] args) {
		
		FileInputStream in_xml = null;
		OutputStreamWriter out_xml = null;
		
		try {
			in_xml = new FileInputStream(args[0]);
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			out_xml = new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			int x;

			while ((x = in_xml.read()) != -1) {
				out_xml.write(x);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			in_xml.close();
			out_xml.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
