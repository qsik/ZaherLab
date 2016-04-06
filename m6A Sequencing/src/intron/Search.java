package intron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Util;

public class Search {
	public static void main(String[] args) throws InvalidFormatException, IOException, SQLException, ClassNotFoundException {
		String zaherPath = "C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/";
		File excelFile = new File(zaherPath + "Data.xlsx");
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/Hg19.accdb");

		XSSFWorkbook data = new XSSFWorkbook(new FileInputStream(excelFile));
		XSSFSheet sites = data.getSheet("Sites");
		XSSFSheet matches = data.getSheet("Matches");
		if (matches == null) {
			matches = data.createSheet("Matches");
		}
		
		Iterator<Row> iterator = sites.iterator();
		iterator.next();
		
		while (iterator.hasNext()) {
			Row row = iterator.next();
			String motif = Util.getStringValue(row.getCell(0));
			String chrom = Util.getStringValue(row.getCell(1));
			double start = Util.getNumberValue(row.getCell(2));
			double end = Util.getNumberValue(row.getCell(3));
			String strand = Util.getStringValue(row.getCell(4));
			double distance = Util.getNumberValue(row.getCell(7));
			PreparedStatement query = conn.prepareStatement("SELECT * FROM Genes WHERE chrom = ? AND strand = ? AND txStart <= ? AND txEnd >= ?");
			query.setString(1, chrom);
			query.setString(2, strand);
			query.setDouble(3, start);
			query.setDouble(4, end);
			ResultSet result = query.executeQuery();
			while (result.next()) {
				String ref = result.getString(1);
				PreparedStatement queryRef = conn.prepareStatement("SELECT * FROM Sequences WHERE Ref = ?");
				queryRef.setString(1, ref);
				ResultSet sequences = queryRef.executeQuery();
				while (sequences.next()) {
					String sequence = sequences.getString(2);
					try {
						System.out.println((start - result.getInt(4)));
						Pattern regex = Pattern.compile(motif);
						Matcher matcher = regex.matcher(sequence);
						while (matcher.find()) {
							System.out.println(matcher.group());
							System.out.println(matcher.start());
						}
					} catch (Exception e) {
//						System.out.println(ref + " : " + sequences.getString(1));
					}
				}
			}
			result.close();
		}
		
		OutputStream outputStream = new FileOutputStream(excelFile);
		data.write(outputStream);
		data.close();
		
		conn.close();
	}
}

class Site {
	public final String motif;
	public final String chrom;
	public final double start;
	public final double end;
	public final String strand;
	public final double distance;

	public Site(String motif, String chrom, double start, double end, String strand, double distance) {
		this.motif = motif;
		this.chrom = chrom;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.distance = distance;
	}	
}

class Gene {
	public final String ref;
	public final String chrom;
	public final String strand;
	public final double start;
	public final double end;

	public Gene(String ref, String chrom, String strand, double start, double end) {
		this.ref = ref;
		this.chrom = chrom;
		this.strand = strand;
		this.start = start;
		this.end = end;
	}
}
