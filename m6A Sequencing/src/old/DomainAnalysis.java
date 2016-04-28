package old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DomainAnalysis {	
	public static void main(String args[]) throws Exception {
		//		String transcriptBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		//		String proteinBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
		//		String urlCap = "&retmode=xml";
		//		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
		File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
		File excel = new File("Z:/Kyusik/m6A sequencing/m6a.xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excel));
		XSSFSheet analysis = workbook.getSheet("Analysis");
		Iterator<Row> iterator = analysis.rowIterator();
		iterator.next();
		SAXReader reader = new SAXReader();
		while (iterator.hasNext()) {
			Row row = iterator.next();
			String id = getCellData(row.getCell(11));
			if (!id.equals("")) {
				System.out.println(id);
				int position = Integer.parseInt(getCellData(row.getCell(2)));
				int displacement = Integer.parseInt(getCellData(row.getCell(4)));
				position = position + displacement;
				int start = Integer.parseInt(getCellData(row.getCell(7)));
				int end = Integer.parseInt(getCellData(row.getCell(8)));
				if (position > start && position < end) {
					File proteinFile = new File(proteinFolder.getPath() + "/" + id + ".xml");
					if (proteinFile.exists()) {
						Document document = reader.read(proteinFile);
						int aaPos = (position - start) / 3;
						if (getCellData(row.getCell(12)).equals("")) {
							String sequence = document.getRootElement().element("GBSeq").element("GBSeq_sequence").getText();
							char aa = sequence.charAt(aaPos);
							row.getCell(12).setCellValue("" + aa);
						}
						List<Integer> starts = new ArrayList<Integer>();
						List<Integer> ends = new ArrayList<Integer>();
						List<String> domainName = new ArrayList<String>();
						for (Element element : document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements()) {
							String name = element.elementText("GBFeature_key");
							if (name != null && name.equals("Region")) {
								try {
									String[] location = element.elementText("GBFeature_location").replaceAll("[<>]", "").split("\\..");
									starts.add(Integer.valueOf(location[0]));
									ends.add(Integer.valueOf(location[1]));
									for (Element e : element.element("GBFeature_quals").elements()) {
										String dName = e.elementText("GBQualifier_name");
										if (dName != null && dName.equals("region_name")) {
											domainName.add(e.elementText("GBQualifier_value"));
											break;
										}
									}
								} catch (Exception e) {
									System.out.println(e);
								}
							}
						}
						double length = ((double) end - start + 1) / 3.0D;
						int[] dRegion = {start, end};
						double dLength = 0;
						for (int i = 0; i < ends.size() ; i++) {
							if (dLength == 0 && dRegion[0] == start) {
								dRegion[0] = starts.get(i);
								dRegion[1] = ends.get(i);
							}
							else {
								if (starts.get(i) <= dRegion[0]) {
									dRegion[0] = starts.get(i);
								}
								if (starts.get(i) >= dRegion[0] && starts.get(i) <= dRegion[1] && ends.get(i) > dRegion[1]) {
									dRegion[1] = ends.get(i);
								}
								if (starts.get(i) > dRegion[1]) {
									dLength += dRegion[1] - dRegion[0] + 1;
									dRegion[0] = starts.get(i);
									dRegion[1] = ends.get(i);
								}
								System.out.println(Arrays.toString(dRegion));
								System.out.println(dLength);
							}
						}
						System.out.println(dLength / length);
						row.createCell(15).setCellValue(dLength > 0? dLength / length * 100 : 100);
						for (int i = 0; i < ends.size() - 1; i++) {
							if (aaPos > ends.get(ends.size() - 1) && aaPos <= (end/3)) {
								String domain = "[" + starts.get(starts.size() - 1) + ", " + ends.get(ends.size() - 1) + "]";
								int fromEnd = (end - 3) / 3 - aaPos;
								row.createCell(13).setCellValue(domain + ", From Stop: " + fromEnd);
								row.createCell(14).setCellValue(domainName.get(domainName.size() - 1));
								break;
							}
							else if (aaPos > ends.get(i) && aaPos < starts.get(i + 1)) {
								String domainOne = "[" + starts.get(i) + ", " + ends.get(i) + "]";
								String domainTwo = "[" + starts.get(i + 1) + ", " + ends.get(i + 1) + "]";
								row.createCell(13).setCellValue(domainOne + ", " + domainTwo);
								row.createCell(14).setCellValue(domainName.get(i) + " : " + domainName.get(i + 1));
								break;
							}
						}
					}
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(excel);
		workbook.write(outputStream);
		outputStream.close();
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
