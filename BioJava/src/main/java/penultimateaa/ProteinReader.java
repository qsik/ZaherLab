package penultimateaa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class ProteinReader {
	public static void main(String[] args) throws Exception{
		char[] alphabetize = AminoAcid.ALPHABET.toCharArray();
		Arrays.sort(alphabetize);
		String alphabet = new String(alphabetize);
		//		Set<AminoAcid> aas = new HashSet<AminoAcid>();
		/*
		 * Method 1: With the FastaReaderHelper
		 */
		//Try with the FastaReaderHelper
		LinkedHashMap<String, ProteinSequence> proteinSequences = FastaReaderHelper.readFastaProteinSequence(new File("C:/Users/Zaher Lab/Desktop/Kyusik/protein sequences.faa"));
		//FastaReaderHelper.readFastaDNASequence for DNA sequences

		try {
			FileWriter writer = new FileWriter("C:/Users/Zaher Lab/Desktop/Kyusik/analysis.csv");
			writer.append(",");
			for (int i = 0; i < alphabet.length(); i++) {
				writer.append(alphabet.charAt(i));
				if (i == alphabet.length() - 1) {
					writer.append("\n");
				}
				else {
					writer.append(",");
				}
			}

			for (int i = 0; i < alphabet.length(); i++) {
				char aa = alphabet.charAt(i);
				AminoAcid aminoAcid = new AminoAcid(aa);
				for (Entry<String, ProteinSequence> entry : proteinSequences.entrySet()) {
					String sequence = entry.getValue().getSequenceAsString();
					if (sequence.endsWith(String.valueOf(aa))) {
						aminoAcid.addCount(sequence.charAt(sequence.length() - 2));
					}
				}
				aminoAcid.print();
				aminoAcid.writeData(writer);
			}
			
			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
	}

	//		/*
	//		 * Method 2: With the FastaReader Object 
	//		 */		
	//		//Try reading with the FastaReader
	//		FileInputStream inStream = new FileInputStream( args[0] );
	//		FastaReader<ProteinSequence,AminoAcidCompound> fastaReader = 
	//			new FastaReader<ProteinSequence,AminoAcidCompound>(
	//					inStream, 
	//					new GenericFastaHeaderParser<ProteinSequence,AminoAcidCompound>(), 
	//					new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
	//		LinkedHashMap<String, ProteinSequence> b = fastaReader.process();
	//		for (  Entry<String, ProteinSequence> entry : b.entrySet() ) {
	//			System.out.println( entry.getValue().getOriginalHeader() + "=" + entry.getValue().getSequenceAsString() );
	//		}
}
