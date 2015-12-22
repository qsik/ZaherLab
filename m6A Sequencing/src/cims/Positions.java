package cims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Positions {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int f = 1;
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/CIMS/";
		File pos = new File(zaherPath + "positions.xlsx");
		File output = new File(zaherPath + "positions" + f + ".txt");
		output.delete();
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(pos));
		XSSFSheet sheet = wb.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		int i = 0;
		
		
		while(iterator.hasNext()) {
			if (i == 500) {
				writer.close();
				f++;
				output = new File(zaherPath + "positions" + f + ".txt");
				writer = new BufferedWriter(new FileWriter(output));
				i = 0;
			}
			
			Row row = iterator.next();
			String chr = Util.getStringValue(row.getCell(0));
			double start = Util.getNumberValue(row.getCell(1));
			double end = Util.getNumberValue(row.getCell(2));
			String strand = Util.getStringValue(row.getCell(3));
			strand = strand.replace("+", "1");
			strand = strand.replace("-", "-1");
			String line = chr.replace("chr", "") + ":" + start + ":" + end + ":" + strand + ",";
			writer.write(line);
			writer.newLine();
			i++;
		}
		
		writer.close();		
		wb.close();
	}
}
