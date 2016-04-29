package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Manual {
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/FFT")));
			List<Double> raw = new ArrayList<Double>();
			String dataPoint;
			while ((dataPoint = reader.readLine()) != null) {
				double y = Double.parseDouble(dataPoint);
				raw.add(y);
			}
			double[] in = new double[raw.size()];
			for (int i = 0; i < raw.size(); i++) {
				in[i] = raw.get(i);
			}
			Complex[] c = new FastFourierTransformer(DftNormalization.STANDARD).transform(in, TransformType.FORWARD);
			for (Complex complex : c) {
				System.out.println(complex.getReal() + " \t" + complex.getImaginary());
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}