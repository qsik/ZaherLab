package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Manual {
	public final static int SMOOTHING = 10;
	public final static int WINDOW = 45;
	
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/Data")));
			List<Integer> count = new ArrayList<Integer>();
			List<Integer> smoothed = new ArrayList<Integer>();
			List<Integer> distance = new ArrayList<Integer>();
			String dataPoint;
			while ((dataPoint = reader.readLine()) != null) {
				int y = Integer.parseInt(dataPoint.split("\t")[1]);
				count.add(y);
			}
			for (int i = 0; i < count.size(); i++) {
				int d = 0;
				int c = 0;
				for (int x = i - SMOOTHING; x <= i + SMOOTHING; x++) {
					if (x >= 0 && x < count.size()) {
						d += count.get(x);
						c++;
					}
				}
				d = d / c;
				smoothed.add(d);
			}
			for (int i = 0; i < smoothed.size(); i++) {
				int min = Integer.MAX_VALUE;
				int b = 0;
				for (int j = i; j <= i + WINDOW && j < smoothed.size(); j++) {
					if (smoothed.get(j) < min) {
						min = smoothed.get(j);
						b = j;
					}
				}
				int maxOne = 0;
				int a = 0;
				for (int j = i; j <= b; j++) {
					if (smoothed.get(j) > maxOne) {
						maxOne = smoothed.get(j);
						a = j;
					}
				}
				int maxTwo = 0;
				int c = 0;
				for (int j = b; j <= i + WINDOW && j < smoothed.size(); j++) {
					if (smoothed.get(j) > maxTwo) {
						maxTwo = smoothed.get(j);
						c = j;
					}
				}
				distance.add(Math.abs(c - a));
			}
			double average = 0;
			for (int d : distance) {
				average += d;
			}
			average = average / distance.size();
			System.out.println(average);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Extrema {
	public final int maxOne;
	public final int maxTwo;
	public final int distance;
	
	public Extrema(int maxOne, int maxTwo) {
		this.maxOne = maxOne;
		this.maxTwo = maxTwo;
		distance = maxTwo - maxOne;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Extrema other = (Extrema) obj;
		if (distance != other.distance)
			return false;
		if (maxOne != other.maxOne)
			return false;
		if (maxTwo != other.maxTwo)
			return false;
		return true;
	}	
}