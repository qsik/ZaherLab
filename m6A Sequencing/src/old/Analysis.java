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
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Analysis {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		final double SPACING = 100;
		File CIMS = new File("Z:/Kyusik/m6A sequencing/CIMS.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(CIMS));
		Util.backup(workbook, "CIMS");
		XSSFSheet analysis = workbook.getSheet("Analysis");

		Iterator<Row> iterator = analysis.rowIterator();
		iterator.next();
		Map<String, Set<Protein>> sites = new HashMap<String, Set<Protein>>();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			try {
				String ref = Util.getStringValue(row.getCell(0));
				double start = Util.getNumberValue(row.getCell(7));
				double end = Util.getNumberValue(row.getCell(8));
				double position = Util.getNumberValue(row.getCell(9));
				double region = Util.getNumberValue(row.getCell(10));
				if (position == -1 || region != 0) {
				}
				else {
					double codonPos = Util.getNumberValue(row.getCell(11));
					String codon = Util.getStringValue(row.getCell(12));
					String geneSymbol = Util.getStringValue(row.getCell(13));
					Protein protein = new Protein(ref, start, end, position, codonPos, codon, geneSymbol);
					if (sites.containsKey(ref)) {
						for (Protein p : sites.get(ref)) {
							if (Math.abs(p.position - protein.position) > SPACING) {
								sites.get(ref).add(protein);
								break;
							}
						}

					}
					else {
						sites.put(ref, new HashSet<Protein>());
						sites.get(ref).add(protein);
					}
					System.out.println(protein.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		XSSFSheet candidates = workbook.createSheet("Candidates");
		candidates.createRow(0);

		for (Set<Protein> proteins : sites.values()) {
			for (Protein protein : proteins) {
				Row row = candidates.createRow(candidates.getLastRowNum() + 1);
				row.createCell(0).setCellValue(protein.ref);
				row.createCell(1).setCellValue(protein.start);
				row.createCell(2).setCellValue(protein.end);
				row.createCell(3).setCellValue(protein.position);
				row.createCell(4).setCellValue(protein.codonPos);
				row.createCell(5).setCellValue(protein.codon);
				row.createCell(6).setCellValue(protein.geneSymbol);
			}
		}

		FileOutputStream outputStream = new FileOutputStream(CIMS);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}
}
