package m6a;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import util.Util;

public class Cims {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		String fetch = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String fetchCap = "&retmode=xml";
		File cims = new File("C:/Users/Kyusik Kim/Google Drive/Zaher Lab/CIMS.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(cims));
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Util.backup(workbook, cims);

		Iterator<Row> iterator = analysis.iterator();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String ref = Util.getStringValue(row.getCell(0));
			String motif = Util.getStringValue(row.getCell(4));
			double start = Util.getNumberValue(row.getCell(7));
			double end = Util.getNumberValue(row.getCell(8));
			if (start != -1 && end != -1) {
				File transcript = new File("C:/Users/Kyusik Kim/Google Drive/Zaher Lab/Transcripts/" + ref + ".xml");

				if (!transcript.exists()) {
					try {
						URL url = new URL(fetch + ref + fetchCap);
						Files.copy(url.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
						Thread.sleep(350);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (transcript.exists()) {
					try {
						SAXReader reader = new SAXReader();
						Document document = reader.read(transcript);
						String sequence = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
						Pattern regex = Pattern.compile(motif);
						Matcher matcher = regex.matcher(sequence);
						while (!matcher.find() && motif.length() > 5) {
							motif = motif.substring(1, motif.length() - 1);
							regex = Pattern.compile(motif);
							matcher = regex.matcher(sequence);
						}
						if (motif.length() >= 5) {
							matcher.reset();
							int instance = 0;
							while (matcher.find()) {
								instance++;
								if (instance > 1) {
									System.out.println("WARNING! " + ref);
									break;
								}
							}

							if (instance == 1) {
								matcher.reset();
								matcher.find();
								row.createCell(9).setCellValue(matcher.start() + (motif.length() / 2) + 1);
								System.out.println(ref);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(cims);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}
}
