package penultimateaa;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AminoAcid {
	public static final String ALPHABET = "DTSEPGACVMILYFHKRWQNUO";
	public final char AA;
	private final Map<Character, Integer> prevAA = new HashMap<Character, Integer>();
	
	public AminoAcid(char AA) {
		this.AA = AA;
		for (int i = 0; i < ALPHABET.length(); i++) {
			prevAA.put(ALPHABET.charAt(i), 0);
		}
	}
	
	public void addCount(char aa) {
		int count = prevAA.get(aa);
		prevAA.put(aa, count + 1);
	}
	
	public void print() {
		System.out.println("Amino Acid = " + AA);
		for (Entry<Character, Integer> entry : prevAA.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
	public void writeData(FileWriter fileWriter) throws IOException {
		fileWriter.append(AA);
		fileWriter.append(",");
		int i = 0;
		for (Integer count : prevAA.values()) {
			fileWriter.append(count.toString());
			if (i == 21) {
				fileWriter.append("\n");
			}
			else {
				fileWriter.append(",");
			}
			i++;
		}
	}
}
