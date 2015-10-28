package ucsc;

public class Transcript {
	public final String chrom;
	public final double chromStart;
	public final double chromEnd;
	public final String id;
	public final String refseq;
	
	public Transcript(String chrom, double chromStart, double chromEnd, String id, String refseq) {
		this.chrom = chrom;
		this.chromStart = chromStart;
		this.chromEnd = chromEnd;
		this.id = id;
		this.refseq = refseq;
	}
	
	public boolean matchTranscript(String chrom, double chromStart, double chromEnd, String id) {
		return this.chrom.equalsIgnoreCase(chrom) 
				&& this.chromStart == chromStart
				&& this.chromEnd == chromEnd 
				&& this.id.equals(id);
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Transcript other = (Transcript) obj;
//		if (chrom == null) {
//			if (other.chrom != null)
//				return false;
//		} else if (!chrom.equals(other.chrom))
//			return false;
//		if (Double.doubleToLongBits(chromEnd) != Double.doubleToLongBits(other.chromEnd))
//			return false;
//		if (Double.doubleToLongBits(chromStart) != Double.doubleToLongBits(other.chromStart))
//			return false;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
}
