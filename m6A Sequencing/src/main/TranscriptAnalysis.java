package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class TranscriptAnalysis {
	public static void main(String[] args) throws Exception {
//		File headers = new File("C:/Users/Kyusik Kim/Downloads/m6A analysis/headers.txt");
		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String urlCap = "&retmode=xml";
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xls");

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(excel));
		HSSFSheet analysis = workbook.getSheet("Analysis");
		
		Iterator<Row> rowIterator = analysis.rowIterator();
		Row headerRow = rowIterator.next();
		headerRow.createCell(7).setCellValue("CDS Start");
		headerRow.createCell(8).setCellValue("CDS End");
		headerRow.createCell(9).setCellValue("Codon Position");
		SAXReader reader = new SAXReader();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			cellIterator.next();
			cellIterator.next();
			String position = getCellData(cellIterator.next());
			cellIterator.next();
			cellIterator.next();
			cellIterator.next();
			String id = getCellData(cellIterator.next());
			File transcriptFile = new File(transcriptFolder.getPath() + "/" + id + ".xml");
			URL ncbi = new URL(urlBase + id + urlCap);
			if (!transcriptFile.exists()) {
				Files.copy(ncbi.openStream(), transcriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			try {
				Document document = reader.read(transcriptFile);
				List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
				for (Element element : elements) {
					Element cds = element.element("GBFeature_key");
					if (cds != null && cds.getText().equals("CDS")) {
						System.out.println(id);
						String[] location = element.elementText("GBFeature_location").split("\\..");
						row.createCell(7).setCellValue(location[0]);
						row.createCell(8).setCellValue(location[1]);
						Thread.sleep(350);
						break;
//						for (Element e : element.element("GBFeature_quals").elements()) {
//							Element proteinId = e.element("GBQualifier_value");
//							if (proteinId != null && proteinId.getText().contains("GI")) {
//								System.out.println(id);
//								String gi = proteinId.getText().replace("GI:", "");
//								URL ncbi = new URL(urlBase + gi + urlCap);
//								File protein = new File(proteinFolder.getPath() + "/" + id + ".xml");
//								Files.copy(ncbi.openStream(), protein.toPath(), StandardCopyOption.REPLACE_EXISTING);
//								String annot = "CDS";
//								int start = Integer.parseInt(location[0]);
//								int end = Integer.parseInt(location[1]);
//								if (pos < start) {
//									annot = "5'UTR";
//								}
//								else if (pos > end) {
//									annot = "3'UTR";
//								}
//								else if (Math.abs(pos - end) <= 10) {
//									annot = "Near Stop: " + (pos - end);
//								}
//								row.createCell(5).setCellValue(annot);
//								int frame = pos > start && pos < end? (pos - start) % 3 : -1;
//								row.createCell(6).setCellValue(frame);
//								rownum++;
//								Thread.sleep(350);
//								break;
//							}
					}
				}
			} catch (Exception e) {
				row.createCell(7).setCellValue("");
				row.createCell(8).setCellValue("");
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
