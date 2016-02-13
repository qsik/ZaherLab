package intron;

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

public class Search {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		String zaherPath = "C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/";
		File excelFile = new File(zaherPath + "Data.xlsx");

		XSSFWorkbook data = new XSSFWorkbook(new FileInputStream(excelFile));
		XSSFSheet sites = data.getSheet("Sites");
		XSSFSheet genes = data.getSheet("Genes");
		XSSFSheet matches = data.getSheet("Matches");
		if (matches != null) {
			int index = data.getSheetIndex("Matches");
			data.removeSheetAt(index);
		}
		matches = data.createSheet("Matches");
		matches.createRow(0);

		Iterator<Row> siteIterator = sites.iterator();
		siteIterator.next();
		Set<Site> siteSet = new HashSet<Site>();

		Iterator<Row> geneIterator = genes.iterator();
		geneIterator.next();
		Set<Gene> geneSet = new HashSet<Gene>();

		while (siteIterator.hasNext()) {
			Row row = siteIterator.next();
			String motif = Util.getStringValue(row.getCell(0));
			String chrom = Util.getStringValue(row.getCell(1));
			double start = Util.getNumberValue(row.getCell(2));
			double end = Util.getNumberValue(row.getCell(3));
			String strand = Util.getStringValue(row.getCell(4));
			double distance = Util.getNumberValue(row.getCell(5));
			Site site = new Site(motif, chrom, start, end, strand, distance);
			siteSet.add(site);
		}

		while (geneIterator.hasNext()) {
			Row row = geneIterator.next();
			String ref = Util.getStringValue(row.getCell(0));
			String chrom = Util.getStringValue(row.getCell(1));
			String strand = Util.getStringValue(row.getCell(2));
			double start = Util.getNumberValue(row.getCell(3));
			double end = Util.getNumberValue(row.getCell(4));
			Gene gene = new Gene(ref, chrom, strand, start, end);
			geneSet.add(gene);
		}

		for (Site site : siteSet) {
			for (Gene gene : geneSet) {
				if (site.chrom.equals(gene.chrom)) {
					if (site.strand.equals(gene.strand)) {
						if (site.start >= gene.start && site.end <= gene.end) {
							double d = Math.abs(gene.start - site.start);
							d = Math.abs(site.distance) - d;
							if (d >= -1 && d <= 1) {
								Row row  = matches.createRow(matches.getLastRowNum() + 1);
								row.createCell(0).setCellValue(gene.ref);
								row.createCell(1).setCellValue(site.chrom);
								row.createCell(2).setCellValue(site.start);
								row.createCell(3).setCellValue(site.end);
								row.createCell(4).setCellValue(site.strand);
								row.createCell(5).setCellValue(site.motif);
							}
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

class Site {
	public final String motif;
	public final String chrom;
	public final double start;
	public final double end;
	public final String strand;
	public final double distance;

	public Site(String motif, String chrom, double start, double end, String strand, double distance) {
		this.motif = motif;
		this.chrom = chrom;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.distance = distance;
	}	
}

class Gene {
	public final String ref;
	public final String chrom;
	public final String strand;
	public final double start;
	public final double end;

	public Gene(String ref, String chrom, String strand, double start, double end) {
		this.ref = ref;
		this.chrom = chrom;
		this.strand = strand;
		this.start = start;
		this.end = end;
	}
}
