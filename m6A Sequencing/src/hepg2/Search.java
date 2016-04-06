package hepg2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Search {
	public static void main(String[] args) throws InvalidFormatException, IOException, ClassNotFoundException, SQLException {
		String zaherPath = "C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/";
		File excelFile = new File(zaherPath + "HepG2 Dataset.xlsx");
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/Hg19.accdb");


		XSSFWorkbook data = new XSSFWorkbook(new FileInputStream(excelFile));
		XSSFSheet sites = data.getSheet("Sites");

		Iterator<Row> iterator = sites.iterator();
		iterator.next();

		while (iterator.hasNext()) {
			Row row = iterator.next();
			String id = Util.getStringValue(row.getCell(0));
			double peaksNum = Util.getNumberValue(row.getCell(7));
			String peaks = Util.getStringValue(row.getCell(8));
			System.out.println(Util.getStringValue(row.getCell(5)));
			String[] peaksArray = peaks.split(" ");
			PreparedStatement query = conn.prepareStatement("SELECT * FROM Genes WHERE chrom = ? AND strand = ? AND txStart <= ? AND txEnd >= ?");
			query.setString(1, id);
			query.setString();
			query.setString(parameterIndex, x);
			query.setString(parameterIndex, x);
			ResultSet result = query.executeQuery();
			while (result.next()) {
//				String tChrom = result.getString(1);
//				String tStrand = result.getString(4);
//				double tStart = result.getDouble(2);
//				double tEnd = result.getDouble(3);
				String tSeq = result.getString(6);
				String tRef = result.getString(7);
				for (String peak : peaksArray) {
					int p = Integer.valueOf(peak);
					try {
						String motif = tSeq.substring(p - 2, p + 3);
						System.out.println(motif);
					}
					catch (Exception e) {
						System.out.println(e);
					}
				}				
			}
			result.close();
		}
		data.close();
	}
}