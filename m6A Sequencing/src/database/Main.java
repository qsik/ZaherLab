package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {
	public final static String TAB = "\t";

	public static void main(String[] args) throws InvalidFormatException, IOException {
		File sites = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/sites.txt");
		File transcripts = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/transcripts.txt");
		File output = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/output.txt");

		Set<Transcript> transcriptSet = new HashSet<Transcript>();
		Set<Site> siteSet = new HashSet<Site>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(transcripts)));
			String data;
			reader.readLine();
			while ((data = reader.readLine()) != null) {
				String[] tData = data.split("\t");
				Transcript transcript = new Transcript(tData[0], tData[1], tData[2], Integer.parseInt(tData[3]),
						Integer.parseInt(tData[4]), 
						Arrays.stream(tData[5].substring(1, tData[5].length() - 2).split(",")).mapToInt(Integer::parseInt).toArray(), 
						Arrays.stream(tData[6].substring(1, tData[6].length() - 2).split(",")).mapToInt(Integer::parseInt).toArray());
				transcriptSet.add(transcript);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sites)));
			String data;
			reader.readLine();
			while ((data = reader.readLine()) != null) {
				String[] siteData = data.split("\t");
				Site site = new Site(siteData[0], siteData[1], Integer.parseInt(siteData[2]),
						Integer.parseInt(siteData[3]), siteData[4], siteData[5], siteData[6],
						Integer.parseInt(siteData[7]));
				siteSet.add(site);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for (Site site : siteSet) {
				for (Transcript transcript : transcriptSet) {
					if (site.chrom.equalsIgnoreCase(transcript.chrom)) {
						if (site.start >= transcript.start) {
							if (site.end <= transcript.end) {
								for (int i = 0; i < transcript.exonStarts.length; i++) {
									if (site.start >= transcript.exonStarts[i] && site.end <= transcript.exonEnds[i]) {
										String out = site.motif + TAB + site.chrom + TAB + site.start +
												TAB + site.end + TAB + site.strand + TAB + site.annot1 + 
												TAB + site.annot2 +	TAB + site.distance + TAB + 
												transcript.ref + TAB + transcript.chrom + TAB + 
												transcript.strand + TAB + transcript.start + TAB + transcript.end + 
												TAB + transcript.exonStarts[i] + TAB + transcript.exonEnds[i];
										System.out.println(out);
										writer.write(out);
										writer.newLine();
									}
								}
							}
						}
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Site {
	public final String motif;
	public final String chrom;
	public final int start;
	public final int end;
	public final String strand;
	public final String annot1;
	public final String annot2;
	public final int distance;
	public Site(String motif, String chrom, int start, int end,
			String strand, String annot1, String annot2, int distance) {
		this.motif = motif;
		this.chrom = chrom;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.annot1 = annot1;
		this.annot2 = annot2;
		this.distance = distance;
	}
}

class Transcript {
	public final String ref;
	public final String chrom;
	public final String strand;
	public final int start;
	public final int end;
	public final int[] exonStarts;
	public final int[] exonEnds;

	public Transcript(String ref, String chrom, String strand, int start, int end,
			int[] exonStarts, int[] exonEnds) {
		this.ref = ref;
		this.chrom = chrom;
		this.strand = strand;
		this.start = start;
		this.end = end;
		this.exonStarts = exonStarts;
		this.exonEnds = exonEnds;
	}	
}