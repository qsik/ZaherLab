package cims;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Ensembl {
	public static void main(String[] args) throws IOException {
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/CIMS/";
		File positions = new File(zaherPath + "positions.xlsx");
		File output = new File(zaherPath + "results.txt");
		output.delete();
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(positions));
		XSSFSheet sheet = wb.getSheetAt(1);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		Iterator<Row> iterator = sheet.iterator();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			String chr = Util.getStringValue(row.getCell(0));
			String start = Util.getStringValue(row.getCell(1));
			String end = Util.getStringValue(row.getCell(2));
			String strand = Util.getStringValue(row.getCell(3));
			String location = chr + ":" + start + ":" + end + ":" + strand;
			URL transcript = new URL("http://grch37.rest.ensembl.org/overlap/region/human/" + location + "?feature=transcript");
			URLConnection connection = transcript.openConnection();
		    HttpURLConnection httpConnection = (HttpURLConnection)connection;
		    
		    httpConnection.setRequestProperty("Content-Type", "application/json");
			
		    InputStream response = connection.getInputStream();
		    int responseCode = httpConnection.getResponseCode();
		 
		    if(responseCode != 200) {
		      throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
		    }
		    
			BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        String inputLine;
	        while ((inputLine = reader.readLine()) != null) {
	            writer.write("{" + location + "};" + inputLine);
	            writer.newLine();
	            System.out.println(inputLine);
	        }
	        reader.close();
		}
		writer.close();
		wb.close();
	}
}
