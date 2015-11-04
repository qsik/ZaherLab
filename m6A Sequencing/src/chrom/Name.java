package chrom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Name {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File CIMS = new File("Z:/Kyusik/m6A sequencing/CIMS.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(CIMS));
		Util.backup(workbook, "CIMS");


		XSSFSheet proteins = workbook.getSheet("Proteins");
		Iterator<Row> iterator = proteins.rowIterator();
		iterator.next();
		Set<String> refs = new HashSet<String>();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			try {
				String ref = row.getCell(0).getStringCellValue();
				refs.add(ref);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Refs parsed");

		XSSFSheet data = workbook.getSheet("Raw Data");
		iterator = data.rowIterator();
		iterator.next();
		Map<String, String> genes = new HashMap<String, String>();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			String ref = Util.getCellData(row.getCell(13));
			String gene = Util.getCellData(row.getCell(15));
			if (refs.contains(ref)) {
				if (genes.containsKey(ref)) {
					if (!genes.get(ref).equals(gene)) {
						System.out.println("WARNING!: " + ref);
					}
					else {
						genes.put(ref, gene);
					}
				}
				else {
					genes.put(ref, gene);
				}
			}
		}

		System.out.println("Genes parsed");

		iterator = proteins.rowIterator();
		iterator.next();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			String ref = Util.getCellData(row.getCell(0));
			row.createCell(1).setCellValue(genes.get(ref));
		}

		FileOutputStream outputStream = new FileOutputStream(CIMS);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}
}
