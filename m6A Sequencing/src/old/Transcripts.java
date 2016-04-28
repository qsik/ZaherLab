package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Transcripts {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String urlCap = "&retmode=xml";
		File CITS = new File("Z:/Kyusik/m6A sequencing/CITS.xlsx");
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(CITS));
		Util.backup(workbook, "CITS");
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Iterator<Row> iterator = analysis.rowIterator();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String refseq = row.getCell(0).getStringCellValue();
			File transcript = new File(transcriptFolder.getPath() + "/" + refseq + ".xml");
			System.out.println(refseq);
			if (!transcript.exists()) {
				try {
					URL url = new URL(urlBase + refseq + urlCap);
					Files.copy(url.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
					Thread.sleep(350);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		workbook.close();
	}
}
