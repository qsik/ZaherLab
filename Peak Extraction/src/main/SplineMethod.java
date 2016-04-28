package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class SplineMethod {
	public final static boolean SMOOTH = false;
	public final static int SMOOTHING = 10;
	
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/Data")));
			List<Double> y = new ArrayList<Double>();
			List<Double> maxima = new ArrayList<Double>();
			List<Double> data = new ArrayList<Double>();
			String dataPoint;
			while ((dataPoint = reader.readLine()) != null) {
				y.add(Double.parseDouble(dataPoint.split("\t")[1]));
			}
			if (SMOOTH) {
				for (int i = 0; i < y.size(); i++) {
					double d = 0;
					int c = 0;
					for (int x = i - SMOOTHING; x <= i + SMOOTHING; x++) {
						if (x >= 0 && x < y.size()) {
							d += y.get(x);
							c++;
						}
					}
					d = d / c;
					data.add(d);
				}
			}
			else {
				data.addAll(y);
			}
			double[] sX = new double[y.size()];
			double[] sY = new double[y.size()];
			for (int i = 0; i < y.size(); i++) {
				sX[i] = i;
				sY[i] = data.get(i).doubleValue();
			}
			PolynomialSplineFunction spline = new AkimaSplineInterpolator().interpolate(sX, sY);
			double prev = Double.MAX_VALUE;
			boolean valley = false;
			for (double x = 0; x < sX.length; x++) {
				double der = spline.polynomialSplineDerivative().value(x);
				System.out.println(spline.value(x));
				if (prev == Double.MAX_VALUE) {
					prev = der;
				}
				else {
					if (Math.signum(der) == 0 || (Math.signum(der) < 0 && Math.signum(prev) > 0)) {
						maxima.add(x);
					}
					prev = der;
				}
			}
//			double average = 0;
//			for (int i = 1; i < maxima.size(); i++) {
//				average += maxima.get(i) - maxima.get(i - 1);
//			}
//			average = average / maxima.size();
//			System.out.println(average);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
