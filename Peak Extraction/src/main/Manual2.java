package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Manual2 {
	public final static boolean SMOOTH = false;
	public final static int SMOOTHING = 3;
	public final static int WINDOW = 5;
	public final static int BIN_SIZE = 512;

	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/Data")));
			List<Integer> rawData = new ArrayList<Integer>();
			List<Integer> inputData = new ArrayList<Integer>();
			String dataPoint;
			while ((dataPoint = reader.readLine()) != null) {
				int y = Integer.parseInt(dataPoint.split("\t")[1]);
				rawData.add(y);
			}
			if (SMOOTH) {
				for (int i = 0; i < rawData.size(); i++) {
					int d = 0;
					int c = 0;
					for (int x = i - SMOOTHING; x <= i + SMOOTHING; x++) {
						if (x >= 0 && x < rawData.size()) {
							d += rawData.get(x);
							c++;
						}
					}
					d = d / c;
					inputData.add(d);
				}
			}
			else {
				inputData.addAll(rawData);
			}
			double[] data = new double[inputData.size()];
			double[] fft = new double[BIN_SIZE];
			double[] sY = data.clone();
			double[] sX = data.clone();
			for (int i = 0; i < data.length; i++) {
				data[i] = inputData.get(i);
				sX[i] = i;
			}
			double popDev = new StandardDeviation(false).evaluate(data);
			double mean = new Mean().evaluate(data);
			for (int a = 0; a < data.length; a++) {
				List<Double> temp = new ArrayList<Double>();
				for (int b = a - WINDOW; b < a + WINDOW; b++) {
					if (b >= 0 && b < data.length) {
						temp.add(data[b]);
					}
				}
				double[] local = new double[temp.size()];
				for (int c = 0; c < temp.size(); c++) {
					local[c] = temp.get(c);
				}
				fft[a] = data[a];
				double sampleDev = new StandardDeviation().evaluate(local);
				double localDiff = Math.abs(data[a] - new StandardDeviation().evaluate(local)) / sampleDev;
				double globalDiff = Math.abs(data[a] - mean) / popDev;
				if (localDiff * 0.25 + globalDiff * 0.25 >= 1) {
					sY[a] = data[a];
				}
				else {
					sY[a] = 0;
				}
			}
			for (int i = data.length; i < BIN_SIZE; i++) {
				fft[i] = 0;
			}
			Complex[] output = new FastFourierTransformer(DftNormalization.STANDARD).transform(fft, TransformType.FORWARD);
			PolynomialSplineFunction spline = new AkimaSplineInterpolator().interpolate(sX, sY);
			for (Complex c : output) {
				System.out.println(c.abs());
			}
			List<Double> maxima = new ArrayList<Double>();
			double prev = Double.MAX_VALUE;
			for (double x = 0; x < sX.length; x++) {
				//				System.out.println(x + "\t" + spline.value(x));
				double der = spline.polynomialSplineDerivative().value(x);
				if (prev == Double.MAX_VALUE) {
					prev = der;
				}
				else {
					if (spline.value(x) != 0) {
						if (Math.signum(der) == 0 || (Math.signum(der) < 0 && Math.signum(prev) > 0)) {
							maxima.add(x);
						}
						prev = der;
					}
				}
			}
			for (int i = 1; i < maxima.size(); i++) {
				//				System.out.println(maxima.get(i) - maxima.get(i - 1));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}