package retest;

public class Transcript {
	public final String geneSymbol;
	public final String displacement;
	public final String motif;
	
	public Transcript(String geneSymbol, String displacement, String motif) {
		this.geneSymbol = geneSymbol;
		this.displacement = displacement;
		this.motif = motif;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displacement == null) ? 0 : displacement.hashCode());
		result = prime * result
				+ ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + ((motif == null) ? 0 : motif.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transcript other = (Transcript) obj;
		if (displacement == null) {
			if (other.displacement != null)
				return false;
		} else if (!displacement.equalsIgnoreCase(other.displacement))
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equalsIgnoreCase(other.geneSymbol))
			return false;
		if (motif == null) {
			if (other.motif != null)
				return false;
		} else if (!motif.equalsIgnoreCase(other.motif))
			return false;
		return true;
	}
	
	
}
