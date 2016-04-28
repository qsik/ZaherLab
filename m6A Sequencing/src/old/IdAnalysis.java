package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class IdAnalysis {	
	public static void main(String[] args) throws Exception {
		//		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		//		String urlCap = "&retmode=xml";
		//		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
		//		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");
		File backup = new File("Z:/Kyusik/m6A sequencing/m6a-backup.xlsx");
		FileOutputStream backupStream = new FileOutputStream(backup);

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		workbook.write(backupStream);
		backupStream.close();
		
		XSSFSheet data = workbook.getSheet("Data");
		XSSFSheet hgTables = workbook.getSheet("hgTables");

		Iterator<Row> dataIterator = data.rowIterator();
		dataIterator.next();
		Iterator<Row> tableIterator = hgTables.rowIterator();
		tableIterator.next();

		Map<String, Transcript> transcripts = new HashMap<String, Transcript>();

		while (tableIterator.hasNext()) {
			Row row = tableIterator.next();
			String chrom = row.getCell(0).getStringCellValue();
			double chromStart = row.getCell(1).getNumericCellValue();
			double chromEnd = row.getCell(2).getNumericCellValue();
			String id = "id" + row.getCell(4).getStringCellValue();
			String refseq = row.getCell(6) == null? "" : row.getCell(6).getStringCellValue();
			transcripts.put(id, new Transcript(chrom, chromStart, chromEnd, id, refseq));
		}
		
		System.out.println("Table parsed");
		
		while (dataIterator.hasNext()) {
			try {
				Row row = dataIterator.next();
				String id = row.getCell(0).getStringCellValue();
				String chrom = row.getCell(1).getStringCellValue();
				double chromStart = row.getCell(2).getNumericCellValue();
				double chromEnd = row.getCell(3).getNumericCellValue();
				String geneSymbol = getCellData(row.getCell(5));
				System.out.println(geneSymbol);
				Transcript transcript = transcripts.get(id);
				if (id != null) {
					if (transcript.matchTranscript(chrom, chromStart, chromEnd, id)) {
						row.createCell(11).setCellValue(transcript.refseq);
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
