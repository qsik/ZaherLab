package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Backup;
import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class Search {
	public static void main(String[] args) throws Exception {
		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String urlCap = "&retmode=xml";
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
		//		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");
		File fasta = new File("Z:/Kyusik/m6A sequencing/v36.1mrna.fa");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		Backup.backup(workbook);
		XSSFSheet data = workbook.getSheet("Data");
		XSSFSheet analysis = workbook.getSheet("Analysis") == null? workbook.createSheet("Analysis") : workbook.getSheet("Analysis");

		Map<String, String> fastaSequences = new HashMap<String, String>();
		FASTAFileReader fastaReader = new FASTAFileReaderImpl(fasta);
		FASTAElementIterator iterator = fastaReader.getIterator();
		while (iterator.hasNext()) {
			FASTAElement element = iterator.next();
			String header = element.getHeader();
			header = header.split("\\|")[3];
			header = header.replaceFirst("\\.[0-9]+", "");
			String sequence = element.getSequence();
			fastaSequences.put(header, sequence);
		}

		System.out.println("Fasta parsed");
		fastaReader.close();

		Iterator<Row> rowIterator = data.rowIterator();
		rowIterator.next();
		SAXReader reader = new SAXReader();

		Row headerRow = analysis.createRow(0);
		headerRow.createCell(0).setCellValue("Gene Symbol");
		headerRow.createCell(1).setCellValue("Motif");
		headerRow.createCell(2).setCellValue("Position");
		headerRow.createCell(3).setCellValue("Found");
		headerRow.createCell(4).setCellValue("Displacement");

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String refseq = row.getCell(11).getStringCellValue();
			String geneSymbol = getCellData(row.getCell(5));
			//			String description = row.getCell(6).getStringCellValue();
			double peaksNum = row.getCell(8).getNumericCellValue();
			String[] peaks = getCellData(row.getCell(9)).split(" ");
			//			String[] annot = getCellData(row.getCell(10)).split(" ");
			if (peaksNum != peaks.length) {
				System.out.println("WARNING! " + geneSymbol);
				break;
			}
			System.out.println(geneSymbol);
			if (!refseq.equals("")) {
				String sequence = fastaSequences.get(refseq);
				if (sequence == null) {
					File transcript = new File(transcriptFolder.getPath() + "/" + geneSymbol + ".xml");
					if (!transcript.exists()) {
						URL url = new URL(urlBase + refseq + urlCap);
						Files.copy(url.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
						Thread.sleep(350);
					}
					Document document = reader.read(transcript);
					sequence = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
					System.out.println("Used ncbi record");
				}
				try {
					for (String peak : peaks) {
						int pos = Integer.valueOf(peak) - 1;
						Pattern regex = Pattern.compile("[AG][AG]AC[ACT]");
						Matcher matcher = regex.matcher(sequence);
						while (matcher.find()) {
							if (Math.abs(matcher.start() - (pos - 2)) <= 3) {
								Row dataRow = analysis.createRow(analysis.getLastRowNum() + 1);
								dataRow.createCell(0).setCellValue(geneSymbol);
								dataRow.createCell(1).setCellValue(matcher.group());
								dataRow.createCell(2).setCellValue(pos + 1);
								dataRow.createCell(3).setCellValue(matcher.start() + 3);
								dataRow.createCell(4).setCellValue(matcher.start() - pos + 2);
								System.out.println(pos);
							}
						}
					}						
				} catch (Exception e) {
					System.out.println(geneSymbol);
					System.out.println(e);
				}
			}


		}
		FileOutputStream outputStream = new FileOutputStream(excel);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
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
}