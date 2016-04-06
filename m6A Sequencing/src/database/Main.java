package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {
	public final static String TAB = "\t";

	public static void main(String[] args) throws InvalidFormatException, IOException {
		File sites = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/sites.txt");
		File output = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/output.txt");
		File ucsc = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/UCSC Sequences.tabular");
		Set<Site> siteSet = new HashSet<Site>();

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
			BufferedReader reader = new BufferedReader(new FileReader(ucsc));
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			String data;
			while ((data = reader.readLine()) != null) {
				String[] ref = data.split("\t");
				for (Site site : siteSet) {
					if (site.chrom.equals(ref[1])) {
						double cStart = Double.parseDouble(ref[2]);
						if (site.start >= cStart) {
							double cEnd = Double.parseDouble(ref[3]);
							if (site.end <= cEnd) {
								try {
									int start = Math.max(0, (int) (site.start - cStart - 4));
									int end = Math.min(ref[4].length(), start + 8);
									Pattern pattern = Pattern.compile(site.motif);
									Matcher matcher = pattern.matcher(ref[4]).region(start, end);
									if (matcher.find()) {
										String out = site.motif + TAB + site.chrom + TAB + site.start +
												TAB + site.end + TAB + site.strand + TAB + site.annot1 + 
												TAB + site.annot2 +	TAB + site.distance + TAB + 
												matcher.start() + TAB + ref[0] + TAB + ref[1] + TAB + ref[2] +
												TAB + ref[3] + TAB + ref[4];
										System.out.println(out);
										writer.write(out);
										writer.newLine();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			writer.close();
			reader.close();
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