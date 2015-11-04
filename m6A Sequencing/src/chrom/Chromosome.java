package chrom;

import java.io.File;
import java.io.IOException;

import net.sf.jfasta.FASTAElement;
import net.sf.jfasta.FASTAFileReader;
import net.sf.jfasta.impl.FASTAElementIterator;
import net.sf.jfasta.impl.FASTAFileReaderImpl;

public class Chromosome {
	public static File chromFolder = new File("Z:/Kyusik/m6A sequencing/Chromosomes");
	public final String name;
	public final String sequence;
	
	public Chromosome(File file) throws IOException {
		FASTAFileReader reader = new FASTAFileReaderImpl(file);
		FASTAElementIterator iterator = reader.getIterator();
		FASTAElement element = iterator.next();
		name = element.getHeader();
		sequence = element.getSequence().toUpperCase();
		reader.close();
	}
	
	public Chromosome(String file) throws IOException {
		this(new File(chromFolder.getPath() + "/" + file + ".fa"));
	}
	
	
}
