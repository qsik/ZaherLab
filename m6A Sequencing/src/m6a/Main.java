package m6a;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
		String zaherPath = "C:/Users/Zaher Lab/Google Drive/Zaher Lab/RMBase/";
		String qsikpath = "C:/Users/Kyusik Kim/Google Drive/Zaher Lab/RMBase/";
		File rmbase = new File(zaherPath + "Modified RMBase.xlsx");
		File output = new File(zaherPath + "output.txt");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(rmbase));

		XSSFSheet rawData = workbook.getSheet("Raw Data");
		//		XSSFSheet analysis = workbook.getSheet("Analysis");
		//		if (analysis == null) {
		//			analysis = workbook.createSheet("Analysis");
		//			analysis.createRow(0);
		//		}

		Iterator<Row> iterator = rawData.iterator();
		iterator.next();
		Map<String, Set<Site>> genes = new HashMap<String, Set<Site>>();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String chromosome = Util.getStringValue(row.getCell(0));
			String supportList = Util.getStringValue(row.getCell(4));
			String pubmedIds = Util.getStringValue(row.getCell(5));
			String geneSymbol = Util.getStringValue(row.getCell(6));
			String predRegion = Util.getStringValue(row.getCell(8));
			String sequence = Util.getStringValue(row.getCell(9));
			String motif = Util.getStringValue(row.getCell(10));

			Site site = new Site(chromosome, supportList, pubmedIds, geneSymbol, predRegion, sequence, motif);

			if (genes.containsKey(geneSymbol)) {
				genes.get(geneSymbol).add(site);
			}
			else {
				Set<Site> sites = new HashSet<Site>();
				sites.add(site);
				genes.put(geneSymbol, sites);
			}
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(output), 4096);

		for (Entry<String, Set<Site>> entry : genes.entrySet()) {
			System.out.println(entry.getKey());
			File transcript = new File(zaherPath + "Transcripts/" + entry.getKey() + ".xml");
			if (!transcript.exists()) {
				try {
					URL url = new URL(search + entry.getKey() + searchCap);
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
				try {
					SAXReader reader = new SAXReader();
					Document document = reader.read(transcript);
					for (Site site : entry.getValue()) {
						String data = site.deserialize();
						List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
						for (Element element : elements) {
							Element cds = element.element("GBFeature_key");
							if (cds != null && cds.getText().equals("CDS")) {
								String[] location = element.elementText("GBFeature_location").split("\\..");
								data += "," + location[0];
								data += "," + location[1];
								break;
							}
						}
						String mrna = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
						int sequenceMatches = 0;
						Pattern sequenceSearch = Pattern.compile(site.sequence);
						Matcher searchSequence = sequenceSearch.matcher(mrna);
						while (searchSequence.find()) {
							sequenceMatches++;
						}
						if (sequenceMatches == 1) {
							Pattern motifSearch = Pattern.compile(site.motif);
							Matcher innerSearch = motifSearch.matcher(site.sequence);
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
								data += "," + pos;
								writer.write(data);
								writer.newLine();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		writer.close();
		workbook.close();
	}
}

class Site {
	public final String chromosome;
	public final String supportList;
	public final String pubmedIds;
	public final String geneSymbol;
	public final String predRegion;
	public final String sequence;
	public final String motif;

	public Site(String chromosome, String supportList, String pubmedIds, 
			String geneSymbol, String predRegion, String sequence, String motif) {
		this.chromosome = chromosome;
		this.supportList = supportList.replaceAll(",", ";");
		this.pubmedIds = pubmedIds.replaceAll(",", ";");
		this.geneSymbol = geneSymbol;
		this.predRegion = predRegion;
		this.sequence = sequence.toUpperCase();
		this.motif = motif.toUpperCase();
	}

	public String deserialize() {
		return chromosome + "," + supportList + "," + pubmedIds + "," + geneSymbol + "," 
				+ predRegion + "," + sequence + "," + motif;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((chromosome == null) ? 0 : chromosome.hashCode());
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((motif == null) ? 0 : motif.hashCode());
		result = prime * result
				+ ((predRegion == null) ? 0 : predRegion.hashCode());
		result = prime * result
				+ ((pubmedIds == null) ? 0 : pubmedIds.hashCode());
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result
				+ ((supportList == null) ? 0 : supportList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (chromosome == null) {
			if (other.chromosome != null)
				return false;
		} else if (!chromosome.equals(other.chromosome))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (motif == null) {
			if (other.motif != null)
				return false;
		} else if (!motif.equals(other.motif))
			return false;
		if (predRegion == null) {
			if (other.predRegion != null)
				return false;
		} else if (!predRegion.equals(other.predRegion))
			return false;
		if (pubmedIds == null) {
			if (other.pubmedIds != null)
				return false;
		} else if (!pubmedIds.equals(other.pubmedIds))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (supportList == null) {
			if (other.supportList != null)
				return false;
		} else if (!supportList.equals(other.supportList))
			return false;
		return true;
	}
}
