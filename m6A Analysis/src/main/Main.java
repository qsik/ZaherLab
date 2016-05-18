package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
	public static void main(String[] args) {
		File excel = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/Analysis.xlsx");
		File output = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/Analysis-output.txt");
		
		try {
			XSSFWorkbook data = new XSSFWorkbook(new FileInputStream(excel));
			XSSFSheet siteSheet = data.getSheet("Sites");
			XSSFSheet exonSheet = data.getSheet("Exons");
			XSSFSheet sequenceSheet = data.getSheet("Sequences");
			
			Set<Site> sites = new HashSet<Site>();
			Set<Exon> exons = new HashSet<Exon>();
			Map<String, String> sequences = new HashMap<String, String>();
			
			Iterator<Row> iterator = exonSheet.rowIterator();
			while (iterator.hasNext()) {
				
			}
			
			iterator = exonSheet.rowIterator();
			while (iterator.hasNext()) {
				
			}
			
			iterator = sequenceSheet.rowIterator();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				String ref = row.getCell(0).getStringCellValue();
				String seq = row.getCell(1).getStringCellValue();
				sequences.put(ref, seq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Exon {
	public final String chr;
	public final String strand;
	public final String ref;
	public final double start;
	public final double end;
	public final String seq;
	
	public Exon(String chr, String strand, String ref, double start, double end, String seq) {
		this.chr = chr;
		this.strand = strand;
		this.ref = ref;
		this.start = start;
		this.end = end;
		this.seq = seq;
	}	
}

class Site {
	
}
