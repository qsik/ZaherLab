package m6a;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CheckA {
	public static void main(String[] args) {
		File transcriptFolder = new File("C:/Users/Zaher Lab/Google Drive/Zaher Lab/RMBase/Transcripts");
		int first = 0;
		int second = 0;
		int third = 0;
		for (File transcript : transcriptFolder.listFiles()) {
			System.out.println(transcript.getName());
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(transcript);
				String mrna = document.getRootElement().element("GBSeq").elementText("GBSeq_sequence").toUpperCase();
				int start = 0;
				int end = 0;
				List<Element> elements = document.getRootElement().element("GBSeq").element("GBSeq_feature-table").elements();
				for (Element element : elements) {
					Element cds = element.element("GBFeature_key");
					if (cds != null && cds.getText().equals("CDS")) {
						String[] location = element.elementText("GBFeature_location").split("\\..");
						start = Integer.parseInt(location[0]);
						end = Integer.parseInt(location[1]);
						break;
					}
				}
				if (start == 0 || end == 0) {
				}
				else {
					for (int i = start - 1; i < end; i++) {
						if (mrna.charAt(i) == 'A') {
							switch ((i + 1 - start) % 3 + 1) {
							case 1:
								first++;
								break;
							case 2:
								second++;
								break;
							case 3:
								third++;
								break;
							default:
								break;
							}
						}
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		System.out.println(first);
		System.out.println(second);
		System.out.println(third);
	}

}
