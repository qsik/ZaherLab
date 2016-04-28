package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		//		String urlCap = "&retmode=xml";
		File chromFolder = new File("Z:/Kyusik/m6A sequencing/Chromosomes");
		File CITS = new File("Z:/Kyusik/m6A sequencing/CITS.xlsx");
//		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(CITS));
		Util.backup(workbook, "CITS");
		XSSFSheet data = workbook.getSheet("Raw Data");
		XSSFSheet analysis = workbook.getSheet("Analysis") == null? workbook.createSheet("Analysis") : workbook.getSheet("Analysis");
		analysis.createRow(0);

		Iterator<Row> iterator = data.rowIterator();
		iterator.next();
		Map<String, Set<Site>> sites = new HashMap<String, Set<Site>>();

		for (String chrom : chromFolder.list()) {
			sites.put(chrom.replace(".fa", ""), new HashSet<Site>());
		}

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String annot = row.getCell(7).getStringCellValue();
			String dAnnot = row.getCell(8).getStringCellValue();
			String refseq = Util.getCellData(row.getCell(13));
			if (annot.equalsIgnoreCase("Intergenic") || dAnnot.equalsIgnoreCase("Intergenic") || refseq.equalsIgnoreCase("")) {
			}
			else {
				String chromosome = row.getCell(1).getStringCellValue();
				double position = row.getCell(2).getNumericCellValue();
				String strand = Util.getCellData(row.getCell(4));
				Site site = new Site(chromosome, position, strand, refseq);
				sites.get(chromosome).add(site);
			}
		}

		for (Entry<String, Set<Site>> entry : sites.entrySet()) {
			try {
				Chromosome chromosome = new Chromosome(new File(chromFolder.getPath() + "/" + entry.getKey() + ".fa"));
				System.out.println(chromosome.name);
				for (Site site : entry.getValue()) {
					int pos = (int) site.position;
					String strand = site.strand;
					char verify = chromosome.sequence.charAt(pos - 1);
					if (strand.equalsIgnoreCase("-")) {
						switch (verify) {
						case 'A':						
						case 'C':
						case 'G':
							verify = 'N';
						case 'T':
							verify = 'A';
							break;
						}
					}
					String motif = chromosome.sequence.substring(pos - 6, pos + 5);
					motif = strand.equals("+")? motif : reverse(motif);
					Row row = analysis.createRow(analysis.getLastRowNum() + 1);
					row.createCell(1).setCellValue(site.chromosome);
					row.createCell(2).setCellValue(site.position);
					row.createCell(3).setCellValue(site.strand);
					row.createCell(4).setCellValue(motif);
					row.createCell(0).setCellValue(site.refseq);
					row.createCell(5).setCellValue("" + verify);
				}
				chromosome =  null;
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		FileOutputStream outputStream = new FileOutputStream(CITS);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}

	private static String reverse(String motif) {
		char[] c = motif.toCharArray();
		char[] rc = new char[c.length]; 
		for (int i = 0; i < c.length; i++) {
			switch (c[i]) {
			case 'C':
				rc[c.length - i - 1] = 'G';
				break;
			case 'A':
				rc[c.length - i - 1] = 'T';
				break;
			case 'T':
				rc[c.length - i - 1] = 'A';
				break;
			case 'G':
				rc[c.length - i - 1] = 'C';
				break;
			default:
				break;
			}
		}
		return new String(rc);
	}
}
