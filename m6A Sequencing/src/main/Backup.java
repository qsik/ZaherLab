package main;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Workbook;

public class Backup {

	public static void backup(Workbook workbook) {
		try {
			File backup = new File("Z:/Kyusik/m6A sequencing/m6a-backup.xlsx");
			FileOutputStream backupStream = new FileOutputStream(backup);
			workbook.write(backupStream);
			backupStream.close();
		} catch (Exception e) {
			System.out.println("Unable to create backup");
		}
	}
}
