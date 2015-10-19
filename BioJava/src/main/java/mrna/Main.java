package mrna;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

public class Main {
	public static void main(String[] args) throws Exception{
		LinkedHashMap<String, DNASequence> genes = FastaReaderHelper.readFastaDNASequence(new File("C:/Users/Kyusik Kim/Downloads/m6A Sequencing/chr1.fa"), true);
		for (Entry<String, DNASequence> entry : genes.entrySet()) {
			System.out.println(entry.getValue().getOriginalHeader() + "=" + entry.getValue().getSequenceAsString());
		}
	}
}
