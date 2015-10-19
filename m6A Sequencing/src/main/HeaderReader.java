package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class HeaderReader {
	public static void main(String[] args) throws Exception {
		File headers = new File("Z:/Kyusik/m6A sequencing/headers.txt");
		String urlBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=";
		String urlCap = "&retmode=xml";
		File transcriptFolder = new File("Z:/Kyusik/m6A sequencing/Transcripts");

		BufferedReader reader = new BufferedReader(new FileReader(headers));
		String line;
		while ((line = reader.readLine()) != null) {
			String id = line.split("\\|")[1];
			URL ncbi = new URL(urlBase + id + urlCap);
			File transcript = new File(transcriptFolder.getPath() + "/" + id + ".xml");
			Files.copy(ncbi.openStream(), transcript.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Thread.sleep(350);
		}
		reader.close();
	}
}
