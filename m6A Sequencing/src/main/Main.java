package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Main {
	//	private static final String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
	//	private static final String urlCap = "&rettype=fasta";
	private static final File mrna = new File("Z:/Kyusik/m6A sequencing/v36.1mrna.fa");
	private static final File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xls");
	private static final File errors = new File("Z:/Kyusik/m6A sequencing/errors.txt");
	//	private static final File position = new File("Z:/Kyusik/m6a sequencing/positions.txt");
	//	private static final File genesFolder = new File("Z:/Kyusik/m6A sequencing/Genes");
	//	private static final File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
	//	private static final File transcriptFastaFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts/Fasta");
	//	private static final File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
	//	private static final File proteinFastaFolder = new File("Z:/Kyusik/m6A sequencing/Proteins/Fasta");

	//	public static final File transcriptFolder = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/Transcripts");
	//	public static final File position = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/positions.txt");
	//	public static final File transcriptFastaFolder = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/Transcripts/Fasta");
	//	public static final File mrna = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/v36.1mrna.fa");
	//	public static final File data = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/m6a.xls");
	//	public static final File sequences = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/sequences.xls");

	public static void main(String[] args) throws Exception {
		if (!errors.exists()) {
			errors.createNewFile();
		}

		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errors), "utf-8"));
		FASTAFileReader reader = new FASTAFileReaderImpl(mrna);
		FASTAElementIterator elementIterator = reader.getIterator();

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(excel));
		HSSFSheet data = workbook.getSheet("Data");
		HSSFSheet analysis = workbook.getSheet("Analysis") == null? workbook.createSheet("Analysis") : workbook.getSheet("Analysis");
		Set<FASTAElement> sequences = new HashSet<FASTAElement>();
		while (elementIterator.hasNext()) {
			sequences.add(elementIterator.next());
		}
		System.out.println("Done parsing FASTA");

		Iterator<Row> rowIterator = data.rowIterator();
		rowIterator.next();
		Row headerRow = analysis.createRow(0);
		headerRow.createCell(0).setCellValue("Gene Symbol");
		headerRow.createCell(1).setCellValue("Motif");
		headerRow.createCell(2).setCellValue("Position");
		headerRow.createCell(3).setCellValue("Displacement");
		headerRow.createCell(4).setCellValue("Header");
		headerRow.createCell(5).setCellValue("Id");
		int rownum = 1;
		while (rowIterator.hasNext()) {
			Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
			cellIterator.next();
			cellIterator.next();
			cellIterator.next();
			cellIterator.next();
			String geneSymbol = getCellData(cellIterator.next());
			String description = getCellData(cellIterator.next());
			description = description.substring(0, description.length() / 2);
			cellIterator.next();
			double numPeaks = cellIterator.next().getNumericCellValue();
			String[] peakPos = getCellData(cellIterator.next()).split(" ");
			if (numPeaks != peakPos.length) {
				System.out.println("WARNING! " + geneSymbol);
				break;
			}
			System.out.println(geneSymbol);
			for (FASTAElement element : sequences) {
				String sequence = element.getSequence();
				String header = element.getHeader();
				if (header.contains(geneSymbol)) {
					if (header.contains(description.substring(0, description.length() / 2))) {
						boolean proceed = true;
						if (description.contains("isoform")) {
							if (header.contains("transcript variant")) {
								int isoform = getIsoform(description);
								int variant = getVariant(header);
								System.out.println(geneSymbol + " : " + isoform + "/" + variant);
								proceed = isoform == variant;
							}
						}
						if (proceed) {
							for (String peak : peakPos) {
								try {
									int pos = Integer.valueOf(peak) - 1;
									Pattern regex = Pattern.compile("[AG][AG]AC[ACT]");
									Matcher matcher = regex.matcher(sequence);
									while (matcher.find()) {
										if (Math.abs(matcher.start() - (pos - 2)) <= 3) {
											Row row = analysis.createRow(rownum);
											row.createCell(0).setCellValue(geneSymbol);
											row.createCell(1).setCellValue(matcher.group());
											row.createCell(2).setCellValue(peak);
											row.createCell(3).setCellValue(matcher.start() + 1);
											row.createCell(4).setCellValue(header);
											row.createCell(5).setCellValue(header.split("\\|")[1]);
											rownum++;
											break;
										}
									}
								} catch (Exception e) {
									writer.append(geneSymbol + " : " + peak + " : " + sequence.length());
									writer.append(System.lineSeparator());
								}							
							}
						}
					}
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(excel);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
		writer.close();
		reader.close();
	}

	private static String getCellData(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			Double d = cell.getNumericCellValue();
			return String.valueOf(d.intValue());
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		default:
			return "";
		}
	}

	private static int getVariant(String variant) {
		Pattern regex = Pattern.compile("(transcript variant )[0-9]+");
		Matcher matcher = regex.matcher(variant);
		while (matcher.find()) {
			Pattern digit = Pattern.compile("[0-9]+");
			Matcher m  = digit.matcher(matcher.group());
			while (m.find()) {
				return Integer.parseInt(m.group());
			}
		}
		return 0;
	}

	private static int getIsoform(String isoform) {
		String transcribe = "abcdefghijk";
		for (char c : transcribe.toCharArray()) {
			if (isoform.contains("isoform " + c)) {
				isoform.replace("isoform " + c, "isoform " + (transcribe.indexOf(c) + 1));
			}
		}
		Pattern regex = Pattern.compile("(isoform )[0-9]+");
		Matcher matcher = regex.matcher(isoform);
		while (matcher.find()) {
			Pattern digit = Pattern.compile("[0-9]+");
			Matcher m  = digit.matcher(matcher.group());
			while (m.find()) {
				return Integer.parseInt(m.group());
			}
		}
		return 0;
	}
}