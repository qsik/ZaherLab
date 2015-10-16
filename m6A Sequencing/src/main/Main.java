package main;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Main {
	private static final String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
	private static final String urlCap = "&rettype=fasta";
	private static final File mrna = new File("Z:/Kyusik/m6a sequencing/refMrna.fa");
	private static final File position = new File("Z:/Kyusik/m6a sequencing/positions.txt");
	private static final File genesFolder = new File("Z:/Kyusik/m6A sequencing/Genes");
	private static final File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
	private static final File transcriptFastaFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts/Fasta");
	private static final File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
	private static final File proteinFastaFolder = new File("Z:/Kyusik/m6A sequencing/Proteins/Fasta");
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		if (!position.exists()) {
			position.createNewFile();
		}
		List<String> pos = new ArrayList<String>();
//		for (File protein : proteinFolder.listFiles()) {
//			try {
//				Document xml = reader.read(protein);
//				String name = protein.getName().replace(".xml", "");
//				String proteinId = xml.getRootElement().element("GBSeq").element("GBSeq_locus").getText();
//				URL url = new URL(urlBase + proteinId + urlCap);
//				File proteinFasta = new File(proteinFastaFolder.getPath() + "/" + name + ".fasta");
//				Files.copy(url.openStream(), proteinFasta.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				System.out.println(name);
//				Thread.sleep(350);
//			} catch (Exception e) {
//				System.out.println(e);
//			}
//		}
		
		for (File transcript : transcriptFolder.listFiles()) {
			Document xml = reader.read(transcript);
			String name = transcript.getName().replace(".xml", "");
			List<Element> elements = xml.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
			String[] cds = {"0", "0"};
			for (Element element : elements) {
				if (element.element("GBFeature_key") != null && element.elementText("GBFeature_key").equals("CDS")) {
					cds = element.elementText("GBFeature_location").split("\\.\\.");
				}
			}
			int start = Integer.parseInt(cds[0]) - 1;
			int end = Integer.parseInt(cds[1]);
			File fasta = new File(transcriptFastaFolder.getPath() + "/" + name + ".fasta");
			if (fasta.exists()) {
				FASTAFileReader fastaReader = new FASTAFileReaderImpl(fasta);
				FASTAElementIterator iterator = fastaReader.getIterator();
				FASTAElement element = iterator.next();
				String codingSequence = element.getSequence().substring(start, end);
				fastaReader.close();
				fastaReader = new FASTAFileReaderImpl(mrna);
				iterator = fastaReader.getIterator();
				System.out.println(name);
				while (iterator.hasNext()) {
					FASTAElement transcriptSequence = iterator.next();
					int loc = transcriptSequence.getSequence().toUpperCase().indexOf(codingSequence);
					if (loc != -1) {
						System.out.println(transcriptSequence.getHeader());
						pos.add(name + ":" + loc);
						break;
					}
				}
				fastaReader.close();
				System.out.println("Complete");
			}
		}
		Files.write(position.toPath(), pos, StandardOpenOption.APPEND);		
	}
}
