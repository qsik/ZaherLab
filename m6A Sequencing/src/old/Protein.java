package old;

public class Protein {
	public final String ref;
	public final double start;
	public final double end;
	public final double position;
	public final double codonPos;
	public final String codon;
	public final String geneSymbol;
	
	public Protein(String ref, double start, double end, double position,
			double codonPos, String codon, String geneSymbol) {
		this.ref = ref;
		this.start = start;
		this.end = end;
		this.position = position;
		this.codonPos = codonPos;
		this.codon = codon;
		this.geneSymbol = geneSymbol;
	}
	
	@Override
	public String toString() {
		return "Protein [ref=" + ref + ", start=" + start + ", end=" + end
				+ ", position=" + position + ", codonPos=" + codonPos
				+ ", codon=" + codon + ", geneSymbol=" + geneSymbol + "]";
	}
}
