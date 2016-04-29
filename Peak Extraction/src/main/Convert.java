package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Convert {
	public static void main(String[] args) {
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/Collated")));
			reader.readLine();
			String line;
			for (int i = 0; i < 2048; i++) {
				data.put(i, 0);
			}
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("\t");
				for (int i = 0; i < split.length; i = i + 2) {
					try {
						int start = Integer.parseInt(split[i]);
						int count = Integer.parseInt(split[i + 1]);
						int c = 0;
						if (data.containsKey(start)) {
							c = data.get(start);
						}
						data.put(start, c + count);
					} catch (Exception ex) {

					}
				}

			}
			for (Entry<Integer, Integer> entry : data.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
