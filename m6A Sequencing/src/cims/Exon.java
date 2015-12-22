package cims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Exon {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/CIMS/";
		File pos = new File(zaherPath + "positions.xlsx");
		File exons = new File(zaherPath + "exons.xlsx");
		File output = new File(zaherPath + "PosToExon.txt");
		output.delete();
		
		Set<Msite> sites = new HashSet<Msite>();
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(pos));
		XSSFSheet data = wb.getSheetAt(0);
		Iterator<Row> rowIterator = data.iterator();
		rowIterator.next();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String chr = Util.getStringValue(row.getCell(0));
			double position = Util.getNumberValue(row.getCell(1));
			String strand = Util.getStringValue(row.getCell(2));
			sites.add(new Msite(chr, position, strand));
		}
		wb.close();

		Set<Exons> ex = new HashSet<Exons>();
		wb = new XSSFWorkbook(new FileInputStream(exons));
		data = wb.getSheetAt(0);
		rowIterator = data.iterator();
		rowIterator.next();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String chr = Util.getStringValue(row.getCell(0));
			String strand = Util.getStringValue(row.getCell(1));
			String[] starts = Util.getStringValue(row.getCell(2)).split(",");
			String[] ends = Util.getStringValue(row.getCell(3)).split(",");
			for(int i = 0; i < starts.length - 1; i++) {
				ex.add(new Exons(chr, strand, starts[i], ends[i]));
			}
		}
		wb.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		for (Msite site : sites) {
			for (Exons e : ex) {
				if (site.chr.equals(e.chr)) {
					if (site.strand.equals(e.strand)) {
						if (site.pos >= e.start && site.pos <= e.end) {
							writer.write(site.chr + "," + site.strand + "," + site.pos + "," + e.start + "," + e.end);
							writer.newLine();
							break;
						}
					}
				}
			}
		}
		
		writer.close();		
	}
}

class Msite {
	public final String chr;
	public final double pos;
	public final String strand;
	
	public Msite(String chr, double pos, String strand) {
		this.chr = chr;
		this.pos = pos;
		this.strand = strand;
	}
}

class Exons {
	public final String chr;
	public final String strand;
	public final double start;
	public final double end;
	
	public Exons(String chr, String strand, String start, String end) {
		this.chr = chr;
		this.strand = strand;
		this.start = Double.parseDouble(start);
		this.end = Double.parseDouble(end);
	}
}