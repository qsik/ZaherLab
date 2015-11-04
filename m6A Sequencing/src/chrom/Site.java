package chrom;

public class Site {
	public final String chromosome;
	public final double position;
	public final String strand;
	public final String refseq;

	public Site(String chromosome, double position, String strand, String refseq) {
		this.chromosome = chromosome;
		this.strand = strand;
		this.position = position;
		this.refseq = refseq;
	}
}
