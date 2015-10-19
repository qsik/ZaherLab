package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Main {
	//	private static final String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=";
	//	private static final String urlCap = "&rettype=fasta";
	//	private static final File mrna = new File("Z:/Kyusik/m6a sequencing/refMrna.fa");
	//	private static final File position = new File("Z:/Kyusik/m6a sequencing/positions.txt");
	//	private static final File genesFolder = new File("Z:/Kyusik/m6A sequencing/Genes");
	//	private static final File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");
	//	private static final File transcriptFastaFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts/Fasta");
	//	private static final File proteinFolder = new File("Z:/Kyusik/m6A sequencing/Proteins");
	//	private static final File proteinFastaFolder = new File("Z:/Kyusik/m6A sequencing/Proteins/Fasta");

	public static final File transcriptFolder = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/Transcripts");
	public static final File position = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/positions.txt");
	public static final File transcriptFastaFolder = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/Transcripts/Fasta");
	public static final File mrna = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/v36.1mrna.fa");
	public static final File data = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/m6a.xls");
	//	public static final File sequences = new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/sequences.xls");

	public static void main(String[] args) throws Exception {
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(position), "utf-8"));
		FASTAFileReader reader = new FASTAFileReaderImpl(mrna);
		FASTAElementIterator elementIterator = reader.getIterator();

		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(data));
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();

		Map<String, String> sequences = new HashMap<String, String>();

		while (elementIterator.hasNext()) {
			FASTAElement element = elementIterator.next();
			sequences.put(element.getHeader(), element.getSequence());
		}

		System.out.println("Done parsing FASTA");

		while (rowIterator.hasNext()) {
			Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
			List<Object> data = new ArrayList<Object>();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					data.add(cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					data.add(cell.getNumericCellValue());
				}
			}
			if (data.get(4) instanceof Double) {
				double geneSym = (double) data.get(4);
				data.remove(4);
				data.add(4, String.valueOf(geneSym));
			}
			if (data.get(8) instanceof Double) {
				int peak = ((Double) data.get(8)).intValue();
				data.remove(8);
				data.add(8, String.valueOf(peak));
			}
			String chromosome = ":" + ((String) data.get(0)).replace("chr", "") + ":";
			String start = String.valueOf(((Double) data.get(1)).longValue());
			System.out.println(data.get(4));
			for (Entry<String, String> entry : sequences.entrySet()) {
				if (entry.getKey().contains((String) data.get(4))) {
					String[] peaks = ((String) data.get(8)).split(" ");
					for (String peak : peaks) {
						try {
							int pos = Integer.parseInt(peak);
							if (entry.getValue().charAt(pos) == 'A' || entry.getValue().charAt(pos - 1) == 'A' || entry.getValue().charAt(pos + 1) == 'A') {
								writer.append("" + entry.getKey() + " : " + entry.getValue());
								writer.append(System.lineSeparator());
							}
						} catch (Exception e) {
							System.out.println(entry.getKey());
							System.out.println(entry.getValue());
							System.out.println(e);
						}
					}
				}
				else if (entry.getKey().contains(chromosome) && entry.getKey().contains(start)) {
					String[] peaks = ((String) data.get(8)).split(" ");
					for (String peak : peaks) {
						try {
							int pos = Integer.parseInt(peak);
							String sequence = entry.getValue().substring(pos - 3, pos + 1);
							writer.append("" + entry.getKey() + " : " + sequence);
							writer.append(System.lineSeparator());
						} catch (Exception e) {
							System.out.println(e);
						}
					}
				}
			}
		}

		//		FileOutputStream outputStream = new FileOutputStream(sequences);
		//		workbook.write(outputStream);
		//		outputStream.close();
		workbook.close();
		writer.close();
		reader.close();

		//		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(data));
		//		HSSFSheet sheet = workbook.getSheetAt(0);
		//		Iterator<Row> rowIterator = sheet.rowIterator();
		//		rowIterator.next();
		//
		//		Map<Transcript> sequences = new HashSet<Transcript>();
		//		Map<String, Transcript> chromosomes = new HashMap<String, Map<String, String>>();
		//
		//		while (elementIterator.hasNext()) {
		//			FASTAElement element = elementIterator.next();
		//			String[] header = element.getHeader().split(" ");
		//			if (header[1].equalsIgnoreCase("cdna:known")) {
		//				chromosomes.put(header[2].split(":")[2], (sequences.put(element.getHeader(), element.getSequence());
		//			}
		//
		//		}
		//
		//		System.out.println("Sequences parsed");
		//
		//		if (!position.exists()) {
		//			position.createNewFile();
		//		}
		//
		//		while (rowIterator.hasNext()) {
		//			Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
		//			String chromosome = cellIterator.next().getStringCellValue().replace("chr", "");
		//			String start = String.valueOf(cellIterator.next().getNumericCellValue());
		//			String end = String.valueOf(cellIterator.next().getNumericCellValue());
		//			String strand = cellIterator.next().getStringCellValue().equals("+")? "1" : "-1";
		//			for (String header : sequences.keySet()) {
		//				System.out.println(header);
		//				System.out.println(chromosome + ":" + start + ":" + end + ":" + strand);
		//				if (header.contains(chromosome) && header.contains(start) && header.contains(end) && header.contains(strand)) {
		//					Cell geneCell = cellIterator.next();
		//					geneCell.setCellType(Cell.CELL_TYPE_STRING);
		//					String geneSymbol = geneCell.getStringCellValue();
		//					cellIterator.next();
		//					cellIterator.next();
		//					Cell numPeaksCell = cellIterator.next();
		//					numPeaksCell.setCellType(Cell.CELL_TYPE_NUMERIC);
		//					double numPeaks = numPeaksCell.getNumericCellValue();
		//					Cell peaksCell = cellIterator.next();
		//					peaksCell.setCellType(Cell.CELL_TYPE_STRING);
		//					String[] peaks = peaksCell.getStringCellValue().split(" ");
		//					if (numPeaks != peaks.length) {
		//						System.out.println(geneSymbol + " : Peaks != Num Peaks!");
		//						break;
		//					}
		//					String[] annot = cellIterator.next().getStringCellValue().split(" ");
		//					System.out.println("test!");
		//				}
		//			}
		//		}
		//
		//		reader.close();
		//		writer.close();
		//		workbook.close();
	}
}

//		for (File protein : proteinFolder.listFiles()) {
//			try {
//				Document xml = reader.read(protein);
//				String name = protein.getName().replace(".xml", "");
//				String proteinId = xml.getRootElement().element("GBSeq").element("GBSeq_locus").getText();
//				URL url = new URL(urlBase + proteinId + urlCap);
//				File proteinFasta = new File(proteinFastaFolder.getPath() + "/" + name + ".fasta");
//				Files.copy(url.openStream(), proteinFasta.toPath(), StandardCopyOption.REPLACE_EXISTING);
//				System.out.println(name);
//				Thread.sleep(350);
//			} catch (Exception e) {
//				System.out.println(e);
//			}
//		}

//		Queue<FASTAElement> fastaElements = new ConcurrentLinkedQueue<FASTAElement>();
//		FASTAFileReader fastaReader = new FASTAFileReaderImpl(mrna);
//		FASTAElementIterator iterator = fastaReader.getIterator();
//		while (iterator.hasNext()) {
//			fastaElements.add(iterator.next());
//		}
//		fastaReader.close();
//
//		int i = 0;
//		for (File transcript : transcriptFolder.listFiles()) {
//			Document xml = reader.read(transcript);
//			String name = transcript.getName().replace(".xml", "");
//			List<Element> elements = xml.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
//			String[] cds = {"0", "0"};
//			for (Element element : elements) {
//				if (element.element("GBFeature_key") != null && element.elementText("GBFeature_key").equals("CDS")) {
//					cds = element.elementText("GBFeature_location").split("\\.\\.");
//				}
//			}
//			int start = Integer.parseInt(cds[0]) - 1;
//			int end = Integer.parseInt(cds[1]);
//			File fasta = new File(transcriptFastaFolder.getPath() + "/" + name + ".fasta");
//			if (fasta.exists()) {
//				System.out.println(name);
//				fastaReader = new FASTAFileReaderImpl(fasta);
//				iterator = fastaReader.getIterator();
//				FASTAElement fastaElement = iterator.next();
//				String codingSequence = fastaElement.getSequence().substring(start, end);
//				fastaReader.close();
//				for (FASTAElement sequence : fastaElements) {
//					int loc = sequence.getSequence().toUpperCase().indexOf(codingSequence);
//					if (loc != -1) {
//						try {
//							writer.append(name + ":" + loc + ":" + sequence.getHeader());
//							writer.append(System.lineSeparator());
//							System.out.println("Complete");
//							fastaElements.remove(sequence);
//						} catch (Exception e) {
//							System.out.println(e);
//						}
//					}
//				}
//			}
//			i++;
//			if (i == 10) {
//				break;
//			}
//		}
//		writer.close();
