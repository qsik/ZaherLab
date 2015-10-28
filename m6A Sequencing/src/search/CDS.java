package search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CDS {
	public static void main(String[] args) throws Exception {
		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String urlCap = "&retmode=xml";
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts/CDS");
		//		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");
		File fasta = new File("Z:/Kyusik/m6A sequencing/v36.1mrna.fa");
				
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		Backup.backup(workbook);
		
		Map<String, String> accession = new HashMap<String, String>();
		FASTAFileReader fastaReader = new FASTAFileReaderImpl(fasta);
		FASTAElementIterator iterator = fastaReader.getIterator();
		while (iterator.hasNext()) {
			FASTAElement element = iterator.next();
			String header = element.getHeader();
			String[] splitHeader = header.split("\\|");
			String gi = splitHeader[1];
			header = splitHeader[3];
			header = header.replaceFirst("\\.[0-9]+", "");
			accession.put(header, gi);
		}
		
		System.out.println("Fasta parsed");
		fastaReader.close();
		
		XSSFSheet data = workbook.getSheet("Data");
		Map<String, String> refseqs = new HashMap<String, String>();
		Iterator<Row> dataIterator = data.rowIterator();
		dataIterator.next();
		while (dataIterator.hasNext()) {
			Row row = dataIterator.next();
			String geneSymbol = getCellData(row.getCell(5));
			String refseq = row.getCell(11).getStringCellValue();
			refseqs.put(geneSymbol, refseq);
		}
		
		System.out.println("Data parsed");
		
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Iterator<Row> rowIterator = analysis.rowIterator();
		rowIterator.next();
		SAXReader reader = new SAXReader();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String geneSymbol = getCellData(row.getCell(0));
//			String motif = getCellData(row.getCell(1));
//			double position = row.getCell(2).getNumericCellValue();
//			double found = row.getCell(3).getNumericCellValue();
			try {
				System.out.println(geneSymbol);
				File transcript = new File(transcriptFolder.getPath() + "/" + geneSymbol + ".xml");
				if (!transcript.exists()) {
					String gi = accession.get(refseqs.get(geneSymbol));
					if (gi == null) {
						gi = refseqs.get(geneSymbol);
						System.out.println("Modern ncbi record used");
					}
					URL url = new URL(urlBase + gi + urlCap);
					Files.copy(url.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
					Thread.sleep(350);					
				}
				Document document = reader.read(transcript);
				List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
				for (Element element : elements) {
					Element cds = element.element("GBFeature_key");
					if (cds != null && cds.getText().equals("CDS")) {
						String[] location = element.elementText("GBFeature_location").split("\\..");
						row.createCell(5).setCellValue(location[0]);
						row.createCell(6).setCellValue(location[1]);
						break;
					}
				}
			} catch (Exception e) {
				System.out.println(e);
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
