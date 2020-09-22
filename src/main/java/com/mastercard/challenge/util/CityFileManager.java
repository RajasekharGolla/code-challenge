package com.mastercard.challenge.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class CityFileManager {

	@Value("${mastercard.city.file.name}")
	private String fileName;

	Logger logger = LoggerFactory.getLogger(CityFileManager.class);

	public List<Pair<String, String>> getCities() throws IOException {
		String file = getFileName();
		List<Pair<String, String>> connections = new ArrayList<>();

		InputStream resource = new ClassPathResource(file).getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
		reader.lines().forEach(line -> {
			String[] cities = line.split(",");
			if (cities.length == 2) {
				connections.add(Pair.of(cities[0], cities[1]));
			} else {
				logger.error("Failed to load connection: {}", line);
			}
		});

		return connections;
	}

	public String getFileName() {
		return fileName;
	}

}
