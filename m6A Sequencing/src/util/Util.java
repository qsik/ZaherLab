package util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

public class Util {
	public static void backup(Workbook workbook, File file) {
		try {
			File backup = new File(file.getParentFile() + "/" + file.getName() + "-backup.xlsx");
			FileOutputStream backupStream = new FileOutputStream(backup);
			workbook.write(backupStream);
			backupStream.close();
		} catch (Exception e) {
			System.out.println("Unable to create backup");
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
			try {
				return Double.parseDouble(cell.getStringCellValue());
			}
			catch (Exception e) {
				return -1;
			}
		default:
			return -1;
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

}
