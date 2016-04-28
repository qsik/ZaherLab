package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UniqueHits {
	public static void main(String args[]) throws FileNotFoundException, IOException {
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");
		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Iterator<Row> iterator = analysis.rowIterator();
		iterator.next();
		Set<String> genes0 = new HashSet<String>();
		Set<String> genes1 = new HashSet<String>();
		Set<String> genes2 = new HashSet<String>();
		Set<String> genes3 = new HashSet<String>();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String geneSymbol = getCellData(row.getCell(0));
			if (row.getRowNum() == 1230) {
				System.out.println(geneSymbol);
				break;
			}			
			int index = Integer.parseInt(getCellData(row.getCell(4)));
			String id = getCellData(row.getCell(11));
			File protein = new File(proteinFolder.getPath() + "/" + id + ".xml");
			boolean cds = getCellData(row.getCell(10)).equals("1")? true : false;
			if (cds && protein.exists()) {
				switch (index) {
				case 0:
					if (!genes0.contains(geneSymbol)) {
						genes0.add(geneSymbol);
					}
					break;
				case -1:
				case 1:
					if (!genes1.contains(geneSymbol)) {
						genes1.add(geneSymbol);
					}
					break;
				case -2:
				case 2:
					if (!genes2.contains(geneSymbol)) {
						genes2.add(geneSymbol);
					}
					break;
				case -3:
				case 3:
					if (!genes3.contains(geneSymbol)) {
						genes3.add(geneSymbol);
					}
					break;
				}
			}
		}

		System.out.println("0: " + genes0.size());
		System.out.println("1: " + genes1.size());
		System.out.println("2: " + genes2.size());
		System.out.println("3: " + genes3.size());
		workbook.close();
	}

	private static String getCellData(Cell cell) {
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_FORMULA:
			case Cell.CELL_TYPE_NUMERIC:
				Double d = cell.getNumericCellValue();
				return String.valueOf(d.intValue());
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			}
		}
		return "";
	}
}
