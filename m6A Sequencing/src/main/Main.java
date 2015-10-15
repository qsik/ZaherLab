package main;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Main {
	private static final String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
	private static final String urlCap = "&retmode=xml";
	private static final File genesFolder = new File("Z:/Kyusik/m6A sequencing/Genes");
	private static final File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
	private static final File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		for (File file : genesFolder.listFiles()) {
			Document xml = reader.read(file);
			List<Element> list = xml.getRootElement().element("Entrezgene").element("Entrezgene_comments").elements();
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				if (e.element("Gene-commentary_heading") != null 
						&& e.elementText("Gene-commentary_heading").equals("NCBI Reference Sequences (RefSeq)")) {
					e.element("Gene-commentary_)
				}
			}
//			URL url = new URL(urlBase + file.getName() + urlCap);	
//			Files.copy(url.openStream(), transcriptFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return;
		}
	}
}
