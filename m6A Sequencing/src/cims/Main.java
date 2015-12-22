package cims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import util.Util;

public class Main {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		String fetchNuc = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String fetchCap = "&retmode=xml";
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/CIMS/";
		File cims = new File(zaherPath + "Data.xlsx");
		File transcriptList = new File(zaherPath + "transcripts.fa");
		File conversionList = new File(zaherPath + "ensembl to ref.xlsx");
		File output = new File(zaherPath + "output.txt");
//		output.delete();

		Map<String, String> ensemblToRef = new HashMap<String, String>();
		XSSFWorkbook convert = new XSSFWorkbook(new FileInputStream(conversionList));
		XSSFSheet data = convert.getSheetAt(0);
		Iterator<Row> rowIterator = data.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String ensembl = Util.getStringValue(row.getCell(0));
			String ref = Util.getStringValue(row.getCell(1)).split("\\.")[0];
			if (ref != null && ensembl != null) {
				ensemblToRef.put(ensembl, ref);
			}
		}
		convert.close();

		FASTAFileReader fastaReader = new FASTAFileReaderImpl(transcriptList);
		FASTAElementIterator iterator = fastaReader.getIterator();
		Map<String, Transcript> transcripts = new HashMap<String, Transcript>();

		while (iterator.hasNext()) {
			FASTAElement element = iterator.next();
			Transcript transcript = new Transcript(element.getHeader().split("\\|"), element.getSequence());
			if (!(transcript.start == 0) || !(transcript.end == 0)) {
				if (ensemblToRef.get(transcript.ensemblT) != null) {
					transcripts.put(ensemblToRef.get(transcript.ensemblT), transcript);
				}
			}
		}

		fastaReader.close();
		
		System.out.println(transcripts.size());

//		BufferedWriter writer = new BufferedWriter(new FileWriter(output));

//		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(cims));
//		data = workbook.getSheetAt(0);
//		rowIterator = data.iterator();
//		rowIterator.next();
//		while (rowIterator.hasNext()) {
//			Row row = rowIterator.next();
//			try {
//				String motif = Util.getStringValue(row.getCell(5));
//				String lMotif = Util.getStringValue(row.getCell(4));
//				String chromosome = Util.getStringValue(row.getCell(0));
//				double start = Util.getNumberValue(row.getCell(7));
//				double end = Util.getNumberValue(row.getCell(8));
//				String strand = Util.getStringValue(row.getCell(9));
//				String ref = Util.getStringValue(row.getCell(12));
//				String annot = Util.getStringValue(row.getCell(10));
//				String dAnnot = Util.getStringValue(row.getCell(11));
//				Site site = new Site(motif, lMotif, chromosome, start, end, strand, ref, annot, dAnnot);
//				if (transcripts.get(ref) != null) {
//					Transcript transcript = transcripts.get(ref);
//					for (int i = 0; i < site.lMotif.length() - site.motif.length(); i++) {
//						Pattern pattern = Pattern.compile(site.lMotif.substring(i, site.lMotif.length() - i));
//						Matcher matcher = pattern.matcher(transcript.sequence);
//						int matches = 0;
//						int pos = 0;
//						while (matcher.find()) {
//							matches++;
//							pos = matcher.start();
//						}
//						if (matches == 1 && pos != 0) {
//							writer.write(site.toString() + "," + transcript.toString() + "," + pos);
//							writer.newLine();
//							break;
//						}
//					}						
//				}
//			} catch (Exception e) {
//				System.out.println(row.getRowNum());
//				e.printStackTrace();
//			}
//		}
//		workbook.close();
//		writer.close();		
	}
}

class Site {
	public final String motif;
	public final String lMotif;
	public final String chromosome;
	public final double start;
	public final double end;
	public final String strand;
	public final String ref;
	public final String annot;
	public final String dAnnot;

	public Site(String motif, String lMotif, String chromosome, double start, double end, String strand, String ref, String annot, String dAnnot) {
		this.motif = motif.split("_")[5];
		this.lMotif = lMotif;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.ref = ref;
		this.annot = annot.replace(",", ";");
		this.dAnnot = dAnnot.replace(",", ";");
	}

	@Override
	public String toString() {
		return motif + "," + lMotif + "," + chromosome	+ "," + start + "," + end + "," + strand + "," + ref + "," + annot + "," + dAnnot;
	}
}

class Transcript {
	public final String ensemblT;
	public final String symbol;
	public final double length;
	public final double start;
	public final double end;
	public final String sequence;

	public Transcript(String[] header, String sequence) {
		if (header.length >= 10) {
			ensemblT = header[0];
			symbol = header[5];
			length = Double.parseDouble(header[6]);
			String[] cds = header[8].replace("CDS:", "").split("-");
			start = Double.parseDouble(cds[0]);
			end = Double.parseDouble(cds[1]);
		}
		else {
			ensemblT = null;
			symbol = null;
			length = 0;
			start = 0;
			end = 0;
		}
		this.sequence = sequence.toUpperCase();
	}

	@Override
	public String toString() {
		return ensemblT + "," + symbol + "," + length + "," + start + "," + end;
	}	
}
