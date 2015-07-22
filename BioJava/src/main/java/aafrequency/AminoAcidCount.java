package aafrequency;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class AminoAcidCount {
	public static final String ALPHABET = "DTSEPGACVMILYFHKRWQNUO";

	public static void main(String[] args) throws Exception {
		/*
		 * Method 1: With the FastaReaderHelper
		 */
		//Try with the FastaReaderHelper
		try {
			FileWriter writer = new FileWriter("C:/Users/Zaher Lab/Desktop/Kyusik/count.csv");
			writer.append("Amino Acid");
			writer.append(",");
			writer.append("Count");
			writer.append(",");
			writer.append("Frequency");
			writer.append("\n");
			
			Map<Character, Integer> aa = new HashMap<Character, Integer>();
			for (int i = 0; i < ALPHABET.length(); i++) {
				char c = ALPHABET.charAt(i);
				aa.put(c, 0);
			}
			
			LinkedHashMap<String, ProteinSequence> proteinSequences = FastaReaderHelper.readFastaProteinSequence(new File("C:/Users/Zaher Lab/Desktop/Kyusik/protein sequences.faa"));
			
			double total = 0;
			for (Entry<String, ProteinSequence> entry : proteinSequences.entrySet()) {
				String sequence = entry.getValue().getSequenceAsString();
				for (int i = 0; i < sequence.length(); i++) {
					char c = sequence.charAt(i);
					int count = aa.get(c);
					aa.put(c, count + 1);
					total++;
				}
			}

			for (Entry<Character, Integer> entry : aa.entrySet()) {
				writer.append(entry.getKey());
				writer.append(",");
				writer.append(entry.getValue().toString());
				writer.append(",");
				double frequency = (double) entry.getValue() / total;
				writer.append(String.valueOf(frequency));
				writer.append("\n");				
			}
			
			writer.flush();
			writer.close();
			System.out.println("complete");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
