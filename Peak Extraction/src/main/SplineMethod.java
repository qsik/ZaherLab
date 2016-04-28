package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class SplineMethod {
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/Data")));
			List<Double> x = new ArrayList<Double>();
			List<Double> y = new ArrayList<Double>();
			List<Double> maxima = new ArrayList<Double>();
			String dataPoint;
			while ((dataPoint = reader.readLine()) != null) {
				x.add(Double.parseDouble(dataPoint.split("\t")[0]));
				y.add(Double.parseDouble(dataPoint.split("\t")[1]));
			}
			double[] sX = new double[x.size()];
			double[] sY = new double[y.size()];
			for (int i = 0; i < x.size(); i++) {
				sX[i] = x.get(i).doubleValue();
				sY[i] = y.get(i).doubleValue();
			}
			PolynomialSplineFunction spline = new SplineInterpolator().interpolate(sX, sY);
			for (double knot : spline.getKnots()) {
				double der = spline.polynomialSplineDerivative().value(knot);
				if (der == 0) {
					System.out.println("SOMETHING FOUND!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
