package m6a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String fetchGene = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=gene&id=";
		String fetchNuc = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String fetchCap = "&retmode=xml";
		String search = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term=";
		String searchCap = "[sym]+AND+human[organism]+AND+alive[property]";
		File rmbase = new File("C:/Users/Kyusik Kim/Google Drive/Zaher Lab/RMBase/Modified RMBase.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(rmbase));
		XSSFSheet rawData = workbook.getSheet("Raw Data");
		XSSFSheet analysis = workbook.createSheet("Analysis");
		analysis.createRow(0);

		Util.backup(workbook, rmbase);

		Iterator<Row> iterator = rawData.iterator();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String chromosome = Util.getStringValue(row.getCell(0));
			String supportList = Util.getStringValue(row.getCell(4));
			String pubmedIds = Util.getStringValue(row.getCell(5));
			String geneSymbol = Util.getStringValue(row.getCell(6));
			String predRegion = Util.getStringValue(row.getCell(8));
			String sequence = Util.getStringValue(row.getCell(9));
			String motif = Util.getStringValue(row.getCell(10));

			File transcript = new File("C:/Users/Kyusik Kim/Google Drive/Zaher Lab/RMBase/Transcripts/" + geneSymbol + ".xml");

			if (!transcript.exists()) {
				try {
					URL url = new URL(search + geneSymbol + searchCap);
					SAXReader reader = new SAXReader();
					Document document = reader.read(url);
					if (document.getRootElement().element("Count").getText().equals("1")) {
						String id = document.getRootElement().element("IdList").elementText("Id");
						url = new URL(fetchGene + id + fetchCap);
						document = reader.read(url);
						id = document.getRootElement().element("Entrezgene").element("Entrezgene_locus")
								.element("Gene-commentary").element("Gene-commentary_products").element("Gene-commentary")
								.elementText("Gene-commentary_accession");
						url = new URL(fetchNuc + id + fetchCap);
						Files.copy(url.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (transcript.exists()) {
				Row aRow = analysis.createRow(analysis.getLastRowNum() + 1);
				aRow.createCell(0).setCellValue(chromosome);
				aRow.createCell(1).setCellValue(supportList);
				aRow.createCell(2).setCellValue(pubmedIds);
				aRow.createCell(3).setCellValue(geneSymbol);
				aRow.createCell(4).setCellValue(predRegion);
				aRow.createCell(5).setCellValue(sequence);
				aRow.createCell(6).setCellValue(motif);

				try {
					SAXReader reader = new SAXReader();
					Document document = reader.read(transcript);
					List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
					for (Element element : elements) {
						Element cds = element.element("GBFeature_key");
						if (cds != null && cds.getText().equals("CDS")) {
							String[] location = element.elementText("GBFeature_location").split("\\..");
							double start = Double.parseDouble(location[0]);
							double end = Double.parseDouble(location[1]);
							aRow.createCell(7).setCellValue(start);
							aRow.createCell(8).setCellValue(end);
							break;
						}
					}

					String mrna = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
					int motifMatches = 0;
					Pattern motifSearch = Pattern.compile(motif);
					Matcher searchMotif = motifSearch.matcher(mrna);
					while (searchMotif.find()) {
						motifMatches++;
					}
					if (motifMatches == 1) {
						searchMotif.reset();
						searchMotif.find();
						double pos = searchMotif.start() + 4;
						aRow.createCell(9).setCellValue(pos);
					}
					else if (motifMatches > 1) {
						int sequenceMatches = 0;
						Pattern sequenceSearch = Pattern.compile(sequence);
						Matcher searchSequence = sequenceSearch.matcher(mrna);
						while (searchSequence.find()) {
							sequenceMatches++;
						}
						if (sequenceMatches == 1) {
							Matcher innerSearch = motifSearch.matcher(sequence);
							int innerMatch = 0;
							while (innerSearch.find()) {
								innerMatch++;
							}
							if (innerMatch == 1) {
								searchSequence.reset();
								searchSequence.find();
								innerSearch.reset();
								innerSearch.find();
								double pos = searchSequence.start() + innerSearch.start() + 4;
								aRow.createCell(9).setCellValue(pos);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
