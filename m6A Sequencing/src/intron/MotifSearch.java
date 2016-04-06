package intron;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

public class MotifSearch {
	public static void main(String[] args) throws IOException {
		String zaherPath = "C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/";
		File genes = new File(zaherPath + "Genes.fasta");

		FASTAFileReader fastaReader = new FASTAFileReaderImpl(genes);
		FASTAElementIterator iterator = fastaReader.getIterator();
		FASTAElement element;
		String sequence;
		double intronSites = 0;
		double exonSites = 0;
		List<Double> exonIntronRatios = new ArrayList<Double>();
		List<Double> exonRatios = new ArrayList<Double>();
		List<Double> intronRatios = new ArrayList<Double>();
		while (iterator.hasNext()) {
			double exonCount = 0;
			double intronCount = 0;
			double exonLength = 0;
			double intronLength = 0;
			element = iterator.next();
			sequence = element.getSequence();
			Pattern regex = Pattern.compile("[AGT][AG]AC[ACT]");
			Matcher matcher = regex.matcher(sequence);
			while (matcher.find()) {
				exonCount++;
				exonSites++;
			}
			regex = Pattern.compile("[agt][ag]ac[act]");
			matcher = regex.matcher(sequence);
			while (matcher.find()) {
				intronCount++;
				intronSites++;
			}
			exonLength = sequence.replaceAll("[a-z]", "").length();
			intronLength = sequence.replaceAll("[A-Z]", "").length();
			
			if (exonLength > 0 && intronLength > 0) {
				double totalLength = exonLength + intronLength;
				exonIntronRatios.add(exonLength / totalLength);
			}
			if (exonLength > 0) {
				exonRatios.add(exonCount / exonLength);
			}
			if (intronLength > 0) {
				intronRatios.add(intronCount / intronLength);
			}
		}
		
		double exonIntronRatio = 0;
		double exonRatio = 0;
		double intronRatio = 0;
		
		for (double ratio : exonIntronRatios) {
			exonIntronRatio += ratio;
		}
		for (double ratio : exonRatios) {
			exonRatio += ratio;
		}
		for (double ratio : intronRatios) {
			intronRatio += ratio;
		}
		
		exonIntronRatio = exonIntronRatio / exonIntronRatios.size();
		exonRatio = exonRatio / exonRatios.size();
		intronRatio = intronRatio / intronRatios.size();
		
		System.out.println("Total DRACH Motifs - Exons: " + exonSites + " Introns: " + intronSites);
		System.out.println("Exon / Intron Ratio: " + exonIntronRatio);
		System.out.println("Site to Length Ratios - Exons: " + exonRatio + " Introns: " + intronRatio);
		fastaReader.close();
	}
}
