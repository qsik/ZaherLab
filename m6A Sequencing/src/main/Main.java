package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Main {
	public final static String TAB = "\t";
	
	public void test() {

		  /*/code{transcriptWidths} computes the lengths of the transcripts
		  (called the "widths" in this context) based on the boundaries
		  of their exons
		transcriptWidths(exonStarts=list(), exonEnds=list())
		}

		\arguments{
		  \item{tlocs}{
		    A list of integer vectors of the same length as \code{exonStarts}
		    and \code{exonEnds}. Each element in \code{tlocs} must contain
		    transcript-based locations.
		  }
		  \item{exonStarts, exonEnds}{
		    The starts and ends of the exons, respectively.

		    Each argument can be a list of integer vectors,
		    an \link[IRanges]{IntegerList} object,
		    or a character vector where each element is a
		    comma-separated list of integers.
		    In addition, the lists represented by \code{exonStarts}
		    and \code{exonEnds} must have the same shape i.e.
		    have the same lengths and have elements of the same lengths.
		    The length of \code{exonStarts} and \code{exonEnds}
		    is the number of transcripts.
		  }
		  \item{strand}{
		    A character vector of the same length as \code{exonStarts} and
		    \code{exonEnds} specifying the strand (\code{"+"} or \code{"-"})
		    from which the transcript is coming.
		  }
		  \item{decreasing.rank.on.minus.strand}{
		    \code{TRUE} or \code{FALSE}.
		    Describes the order of exons in transcripts located on the minus strand:
		    are they ordered by increasing (default) or decreasing rank?
		  }
		  \item{error.if.out.of.bounds}{
		    \code{TRUE} or \code{FALSE}.
		    Controls how out of bound \code{tlocs} are handled: an error is thrown
		    (default) or \code{NA} is returned.
		  }
		}

		\value{
		  For \code{transcriptLocs2refLocs}: A list of integer vectors of the same
		  shape as \code{tlocs}.

		  For \code{transcriptWidths}: An integer vector with one element per
		  transcript.
		}

		\author{Hervé Pagès}

		\seealso{
		  \itemize{
		    \item \code{\link{extractTranscriptSeqs}} for extracting transcript
		          (or CDS) sequences from chromosomes.

		    \item \code{\link{coverageByTranscript}} for computing coverage by
		          transcript (or CDS) of a set of ranges.
		  }
		}

		\examples{
		## ---------------------------------------------------------------------
		## GOING FROM TRANSCRIPT-BASED TO REFERENCE-BASED LOCATIONS
		## ---------------------------------------------------------------------
		library(BSgenome.Hsapiens.UCSC.hg19)  # load the genome
		genome <- BSgenome.Hsapiens.UCSC.hg19
		txdb_file <- system.file("extdata", "hg19_knownGene_sample.sqlite",
		                         package="GenomicFeatures")
		txdb <- loadDb(txdb_file)
		transcripts <- exonsBy(txdb, by="tx", use.names=TRUE)
		tx_seqs <- extractTranscriptSeqs(genome, transcripts)

		## Get the reference-based locations of the first 4 (5' end)
		## and last 4 (3' end) nucleotides in each transcript:
		tlocs <- lapply(width(tx_seqs), function(w) c(1:4, (w-3):w))
		tx_strand <- sapply(strand(transcripts), runValue)
		## Note that, because of how we made them, 'tlocs', 'start(exbytx)',
		## 'end(exbytx)' and 'tx_strand' have the same length, and, for any
		## valid positional index, elements at this position are corresponding
		## to each other. This is how transcriptLocs2refLocs() expects them
		## to be!
		rlocs <- transcriptLocs2refLocs(tlocs,
		             start(transcripts), end(transcripts),
		             tx_strand, decreasing.rank.on.minus.strand=TRUE)

		## ---------------------------------------------------------------------
		## EXTRACTING WORM TRANSCRIPTS ZC101.3 AND F37B1.1
		## ---------------------------------------------------------------------
		## Transcript ZC101.3 (is on + strand):
		##   Exons starts/ends relative to transcript:
		rstarts1 <- c(1, 488, 654, 996, 1365, 1712, 2163, 2453)
		rends1 <- c(137, 578, 889, 1277, 1662, 1870, 2410, 2561)
		##   Exons starts/ends relative to chromosome:
		starts1 <- 14678410 + rstarts1
		ends1 <- 14678410 + rends1

		## Transcript F37B1.1 (is on - strand):
		##   Exons starts/ends relative to transcript:
		rstarts2 <- c(1, 325)
		rends2 <- c(139, 815)
		##   Exons starts/ends relative to chromosome:
		starts2 <- 13611188 - rends2
		ends2 <- 13611188 - rstarts2

		exon_starts <- list(as.integer(starts1), as.integer(starts2))
		exon_ends <- list(as.integer(ends1), as.integer(ends2))
		transcripts <- IRangesList(start=exon_starts, end=exon_ends)

		library(BSgenome.Celegans.UCSC.ce2)
		## Both transcripts are on chrII:
		chrII <- Celegans$chrII
		tx_seqs <- extractTranscriptSeqs(chrII, transcripts, strand=c("+","-"))

		## Same as 'width(tx_seqs)':
		transcriptWidths(exonStarts=exon_starts, exonEnds=exon_ends)

		transcriptLocs2refLocs(list(c(1:6, 135:140, 1555:1560),
		                            c(1:6, 137:142, 625:630)),
		                       exonStarts=exon_starts,
		                       exonEnds=exon_ends,
		                       strand=c("+","-"))

		## A sanity check:
		ref_locs <- transcriptLocs2refLocs(list(1:1560, 1:630),
		                                   exonStarts=exon_starts,
		                                   exonEnds=exon_ends,
		                                   strand=c("+","-"))
		stopifnot(chrII[ref_locs[[1]]] == tx_seqs[[1]])
		stopifnot(complement(chrII)[ref_locs[[2]]] == tx_seqs[[2]])
		}

		\keyword{manip}
		/*/
	}

	public static void main(String[] args) {
		File sites = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/sites.txt");
		File transcripts = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/transcripts.txt");
		File output = new File("C:/Users/Zaher Lab/Desktop/Kyusik/CIMS/output.txt");

		Set<Transcript> transcriptSet = new HashSet<Transcript>();
		Set<Site> siteSet = new HashSet<Site>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(transcripts)));
			String data;
			reader.readLine();
			while ((data = reader.readLine()) != null) {
				String[] tData = data.split("\t");
				Transcript transcript = new Transcript(tData[0], tData[1], tData[2], Integer.parseInt(tData[3]),
						Integer.parseInt(tData[4]), 
						Arrays.stream(tData[5].substring(1, tData[5].length() - 2).split(",")).mapToInt(Integer::parseInt).toArray(), 
						Arrays.stream(tData[6].substring(1, tData[6].length() - 2).split(",")).mapToInt(Integer::parseInt).toArray());
				transcriptSet.add(transcript);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sites)));
			String data;
			reader.readLine();
			while ((data = reader.readLine()) != null) {
				String[] siteData = data.split("\t");
				Site site = new Site(siteData[0], siteData[1], Integer.parseInt(siteData[2]),
						Integer.parseInt(siteData[3]), siteData[4], siteData[5], siteData[6],
						Integer.parseInt(siteData[7]));
				siteSet.add(site);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for (Site site : siteSet) {
				for (Transcript transcript : transcriptSet) {
					if (site.chrom.equalsIgnoreCase(transcript.chrom)) {
						if (site.start >= transcript.start) {
							if (site.end <= transcript.end) {
								for (int i = 0; i < transcript.exonStarts.length; i++) {
									if (site.start >= transcript.exonStarts[i] && site.end <= transcript.exonEnds[i]) {
										String out = site.motif + TAB + site.chrom + TAB + site.start +
												TAB + site.end + TAB + site.strand + TAB + site.annot1 + 
												TAB + site.annot2 +	TAB + site.distance + TAB + 
												transcript.ref + TAB + transcript.chrom + TAB + 
												transcript.strand + TAB + transcript.start + TAB + transcript.end + 
												TAB + transcript.exonStarts[i] + TAB + transcript.exonEnds[i];
										System.out.println(out);
										writer.write(out);
										writer.newLine();
									}
								}
							}
						}
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Site {
	public final String motif;
	public final String chrom;
	public final int start;
	public final int end;
	public final String strand;
	public final String annot1;
	public final String annot2;
	public final int distance;
	public Site(String motif, String chrom, int start, int end,
			String strand, String annot1, String annot2, int distance) {
		this.motif = motif;
		this.chrom = chrom;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.annot1 = annot1;
		this.annot2 = annot2;
		this.distance = distance;
	}
}

class Transcript {
	public final String ref;
	public final String chrom;
	public final String strand;
	public final int start;
	public final int end;
	public final int[] exonStarts;
	public final int[] exonEnds;

	public Transcript(String ref, String chrom, String strand, int start, int end,
			int[] exonStarts, int[] exonEnds) {
		this.ref = ref;
		this.chrom = chrom;
		this.strand = strand;
		this.start = start;
		this.end = end;
		this.exonStarts = exonStarts;
		this.exonEnds = exonEnds;
	}
}