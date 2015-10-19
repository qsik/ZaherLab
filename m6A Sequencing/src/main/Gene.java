package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gene {
	public final String geneSymbol;
	public final List<Integer> peaks = new ArrayList<Integer>();
	public final String[] annot;

	public Gene(String geneSymbol, String[] p, String[] annot) {
		this.geneSymbol = geneSymbol;
		for (String peak : p) {
			this.peaks.add(Integer.parseInt(peak));
		}
		this.annot = annot;
	}

	@Override
	public String toString() {
		return "Gene [geneSymbol=" + geneSymbol + ", peaks=" + peaks
				+ ", annot=" + Arrays.toString(annot) + "]";
	}
}
