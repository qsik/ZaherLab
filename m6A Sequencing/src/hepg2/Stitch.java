package hepg2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Stitch {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		String zaherPath = "C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/";
		File excelFile = new File(zaherPath + "HepG2 Dataset.xlsx");

		XSSFWorkbook data = new XSSFWorkbook(new FileInputStream(excelFile));
		XSSFSheet sites = data.getSheet("Sites");
		XSSFSheet transcripts = data.getSheet("Transcripts");

		Iterator<Row> iterator = transcripts.iterator();
		Set<Transcript> transcriptSet = new HashSet<Transcript>();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String chrom = Util.getStringValue(row.getCell(0));
			String strand = Util.getStringValue(row.getCell(1));
			double start = Util.getNumberValue(row.getCell(2));
			double end = Util.getNumberValue(row.getCell(3));
			String seq = Util.getStringValue(row.getCell(4));
			String ref = Util.getStringValue(row.getCell(5));
			transcriptSet.add(new Transcript(chrom, strand, start, end, seq, ref));
		}
		
		iterator = sites.iterator();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String chrom = Util.getStringValue(row.getCell(0));
			String strand = Util.getStringValue(row.getCell(3));
			double start = Util.getNumberValue(row.getCell(1));
			double end = Util.getNumberValue(row.getCell(2));
			for (Transcript transcript : transcriptSet) {
				if (chrom.equals(transcript.chrom)) {
					if (strand.equals(transcript.strand)) {
						if (start == transcript.start && end == transcript.end) {
							row.createCell(9).setCellValue(transcript.sequence);
							row.createCell(10).setCellValue(transcript.ref);
						}
					}
				}
			}
		}
		
		OutputStream outputStream = new FileOutputStream(excelFile);
		data.write(outputStream);
		data.close();
	}
}

class Transcript {
	public final String chrom;
	public final String strand;
	public final double start;
	public final double end;
	public final String sequence;
	public final String ref;
	
	public Transcript(String chrom, String strand, double start, double end, String sequence, String ref) {
		this.chrom = chrom;
		this.strand = strand;
		this.start = start;
		this.end = end;
		this.sequence = sequence;
		this.ref = ref;
	}	
}