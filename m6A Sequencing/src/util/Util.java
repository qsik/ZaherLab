package util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

public class Util {
	public static void backup(Workbook workbook, String name) {
		try {
			File backup = new File("Z:/Kyusik/m6A sequencing/" + name + "-backup.xlsx");
			FileOutputStream backupStream = new FileOutputStream(backup);
			workbook.write(backupStream);
			backupStream.close();
		} catch (Exception e) {
			System.out.println("Unable to create backup");
		}
	}
	
	public static String getStringValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			Double d = cell.getNumericCellValue();
			return String.valueOf(d.intValue());
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		default:
			return "";
		}
	}
	
	public static double getNumberValue(Cell cell) {
		if (cell == null) {
			return -1;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			return Double.parseDouble(cell.getStringCellValue());
		default:
			return -1;
		}
	}

}
