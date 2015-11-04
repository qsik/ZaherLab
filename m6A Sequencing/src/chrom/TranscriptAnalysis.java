package chrom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.base.Splitter;

import util.Util;

public class TranscriptAnalysis {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File CITS = new File("Z:/Kyusik/m6A sequencing/CITS.xlsx");
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(CITS));
		Util.backup(workbook, "CITS");
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Iterator<Row> iterator = analysis.rowIterator();
		iterator.next();

		Map<String, File> transcripts = new HashMap<String, File>();
		for (File file : transcriptFolder.listFiles()) {
			transcripts.put(file.getName().replace(".xml", ""), file);
		}

		SAXReader reader = new SAXReader();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			try {
				String ref = row.getCell(0).getStringCellValue();
				String motif = row.getCell(4).getStringCellValue();		
				System.out.println(ref);
				File transcript = new File(transcriptFolder.getPath() + "/" + ref + ".xml");
				if (transcript.exists()) {
					Document document = reader.read(transcript);
					String sequence = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
					Pattern regex = Pattern.compile(motif);
					Matcher matcher = regex.matcher(sequence);
					int hits = 0;
					double start = -1;
					double end = -1;
					double position = -1;
					while (matcher.find()) {
						if (hits > 1) {
							row.createCell(7).setCellValue("DUPLICATES!");
						}
						else {
							position = matcher.start() + 6;
							row.createCell(7).setCellValue(position);
							hits++;
						}
					}
					List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
					for (Element element : elements) {
						Element cds = element.element("GBFeature_key");
						if (cds != null && cds.getText().equals("CDS")) {
							String[] location = element.elementText("GBFeature_location").split("\\..");
							start = Double.parseDouble(location[0]);
							end = Double.parseDouble(location[1]);
							row.createCell(5).setCellValue(start);
							row.createCell(6).setCellValue(end);
							break;
						}
					}
					if (position >= start && position <= end && position > 0 && start > 0 && end > 0) {
						System.out.println(start);
						System.out.println(end);
						System.out.println(position);
						List<String> codons = Splitter.fixedLength(3).splitToList(sequence.substring((int) (start - 1), sequence.length()));
						Double c = Math.floor((position - start) / 3);
						String codon = codons.get(c.intValue());
						row.createCell(8).setCellValue(codon);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		FileOutputStream outputStream = new FileOutputStream(CITS);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}
}
