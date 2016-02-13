package cims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Ids {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/CIMS/";
		File results = new File(zaherPath + "results.xlsx");
		File output = new File(zaherPath + "transcriptIds.txt");
		output.delete();
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(results));
		XSSFSheet sheet = wb.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		while(iterator.hasNext()) {
			try {
				Row row = iterator.next();
				String location = row.getCell(0).getStringCellValue();
				String data = row.getCell(1).getStringCellValue();
				String[] multi = data.split("\\},\\{");
				for (String m : multi) {
					String[] d = m.split(",");
					for (String a : d) {
						if (a.contains("transcript_id")) {
							String tId = a.split(":")[1];
							writer.write(location + "\t" + tId);
							writer.newLine();
							System.out.println(location + "\t" + tId);
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		writer.close();
		wb.close();
	}
}

class Id {
	public final String location;
	public final String transcriptId;
	
	public Id(String location, String transcriptId) {
		this.location = location;
		this.transcriptId = transcriptId;
	}	
}
