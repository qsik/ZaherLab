package genomeanalysis;

public class DnaReader {
	//FastaReaderHelper.readFastaDNASequence for DNA sequences
//	public static void main(String[] args) throws Exception{
//		/*
//		 * Method 1: With the FastaReaderHelper
//		 */
//		//Try with the FastaReaderHelper
//		LinkedHashMap<String, DNASequence> genome = FastaReaderHelper.readFastaDNASequence(new File("C:/Users/Zaher Lab/Desktop/Kyusik/ecolik12.fasta"), true);
//		LinkedHashMap<String, DNASequence> genes = FastaReaderHelper.readFastaDNASequence(new File("C:/Users/Zaher Lab/Desktop/Kyusik/ecolik12 gene.txt"), true);
//
//		//		//FastaReaderHelper.readFastaDNASequence for DNA sequences
//		//		for (Entry<String, DNASequence> entry : codingSequences.entrySet()) {
//		//			System.out.println(entry.getValue().getOriginalHeader() + "=" + entry.getValue().getSequenceAsString());
//		//		}
//
//		DNASequence seq = genome.values().iterator().next();
//		String sequence = seq.getSequenceAsString();
//		String reverseSequence = seq.getComplement().getSequenceAsString();
//		sequence = sequence.replaceAll("\\s+", "");
//
//		StringSelection selection = new StringSelection(reverseSequence);
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		clipboard.setContents(selection, selection);
//		
//		System.out.println("Copied");
//
//		int count = 0;
//		Matcher matcher = null;
//		PrintWriter pw = new PrintWriter("C:/Users/Zaher Lab/Desktop/Kyusik/ecolik12 sequences.txt");
//		for (DNASequence gene : genes.values()) {
//			if (sequence.indexOf(gene.getSequenceAsString()) != -1) {
//				String upstreamSequence = sequence.substring(sequence.indexOf(gene.getSequenceAsString()) - 100, sequence.indexOf(gene.getSequenceAsString()) - 35);
//				Pattern forward = Pattern.compile("AAT[A-Z]{6}GCAA[A-Z]{10}");
//				matcher = forward.matcher(upstreamSequence);
//				while (matcher.find()) {
//					pw.println(matcher.group());
//					count++;
//				}
//
//
//			}
//			else {
//				String upstreamSequence = reverseSequence.substring(reverseSequence.indexOf(gene.getSequenceAsString()) + gene.getLength() + 35, reverseSequence.indexOf(gene.getSequenceAsString()) + gene.getLength() + 100);
//				Pattern reverse = Pattern.compile("[A-Z]{10}AACG[A-Z]{6}TAA");
//				matcher = reverse.matcher(upstreamSequence);
//				while (matcher.find()) {
//					DNASequence match = new DNASequence(matcher.group());
//					pw.println(match.getComplement().getSequenceAsString());
//					count++;
//				}
//			}
//		}
//		pw.close();
//		System.out.println(count);
//	}
}
