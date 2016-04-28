package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProteinAnalysis {
	public static void main(String[] args) throws Exception {
		String transcriptBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String proteinBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
		String urlCap = "&retmode=xml";
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		XSSFSheet analysis = workbook.getSheet("Analysis");

		Iterator<Row> rowIterator = analysis.rowIterator();
		rowIterator.next();
		SAXReader reader = new SAXReader();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			try {
				String id = getCellData(row.getCell(6));			
				File transcriptFile = new File(transcriptFolder.getPath() + "/" + id + ".xml");
				URL ncbi = new URL(transcriptBase + id + urlCap);
				if (!transcriptFile.exists()) {
					Files.copy(ncbi.openStream(), transcriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					Thread.sleep(350);
				}
				Document document = reader.read(transcriptFile);
				List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
				for (Element element : elements) {
					Element cds = element.element("GBFeature_key");
					if (cds != null && cds.getText().equals("CDS")) {
						System.out.println(id);
						for (Element e : element.element("GBFeature_quals").elements()) {
							Element proteinId = e.element("GBQualifier_value");
							if (proteinId != null && proteinId.getText().contains("GI")) {
								String gi = proteinId.getText().replace("GI:", "");
								System.out.println(gi);
								File proteinFile = new File(proteinFolder.getPath() + "/" + gi + ".xml");
								if (!proteinFile.exists()) {
									ncbi = new URL(proteinBase + gi + urlCap);
									Files.copy(ncbi.openStream(), proteinFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
									Thread.sleep(350);
								}
								row.createCell(11).setCellValue(gi);
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				row.createCell(11).setCellValue("");
			}
		}
		System.out.println("Written!");
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
