package com.mastercard.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CityConnectionService {

	Logger logger = LoggerFactory.getLogger(CityConnectionService.class);

	/**
	 * Saving all connections in this hash map in both directions.
	 *
	 *
	 * Note: saved in lowercase to compare ignoring case.
	 */

	private Map<String, Set<String>> cityConnectionMap = new HashMap<>();

	/**
	 * Loads file data and store in map while creating service.
	 */
	public CityConnectionService() {
		loadFile("city.txt");
	}

	/**
	 * Reads each line from file into loadConnection function.
	 * 
	 * @param filename example file data: "New York, California\nBoston,
	 *                 Philadelphia"
	 */
	private void loadFile(String filename) {
		try {
			InputStream resource = new ClassPathResource(filename).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
			reader.lines().forEach(line -> {
				String[] cities = line.split(",");
				if (cities.length == 2) {
					addConnection(cities[0], cities[1]);
				} else {
					logger.error("Failed to load connection: {}", line);
				}
			});
		} catch (IOException e) {
			logger.error("Failed to read file {}", filename);
		}
	}

	/**
	 * Saves connections in hashmap both directions.
	 * 
	 * @param city1 either city.
	 * @param city2 either city.
	 */
	public void addConnection(String city1, String city2) {
		if (city1 == null || city2 == null) {
			return;
		}
		city1 = city1.toLowerCase().trim();
		city2 = city2.toLowerCase().trim();
		if (city1.equals(city2)) {
			return;
		}
		addConnectionToMap(city1, city2);
		addConnectionToMap(city2, city1);
	}

	/**
	 * Saves a connection from city1 to city2 in a hashmap.
	 * 
	 * @param city1 origin city
	 * @param city2 destination city
	 */
	private void addConnectionToMap(String city1, String city2) {
		Set<String> existingConnection = cityConnectionMap.get(city1);
		if (existingConnection == null) {
			existingConnection = new HashSet<>();
			existingConnection.add(city2);
			cityConnectionMap.put(city1, existingConnection);
		} else {
			existingConnection.add(city2);
		}
		
	}

	/**
	 * Reading the cityConnectionMap to find connections
	 * 
	 * @param currentCity where are we coming from?
	 * @param destination where are we trying to get to?
	 * @return boolean - connection found or not
	 */
	public boolean isConnected(String currentCity, String destination) {
		return this.isConnected(currentCity, destination, null);
	}
	
	 public boolean isConnected(String currentCity, String destination, Set<String> avoidCities) {
	        if(currentCity == null || destination == null) {
	            logger.error("isConnected was called with null cities.");
	            return false;
	        }

	        //Make it case insensitive
	        currentCity = currentCity.toLowerCase().trim();
	        destination = destination.toLowerCase().trim();

	        //Any city should connect to itself. Even if it's not in the map.
	        if(currentCity.equals(destination)) {
	            return true;
	        }

	        Set<String> cities = cityConnectionMap.get(currentCity);

	        //There are no connections here.
	        if(cities == null) {
	            return false;
	        }

	        if(avoidCities == null) {
	            avoidCities = new HashSet<>();
	        } else {
	            //Make a copy of the previous set, and write to our copy instead of overwriting parents.
	            avoidCities = new HashSet<>(avoidCities);
	        }
	        //Since we are traversing this city now, we will avoid trying to traverse this it in the future.
	        avoidCities.add(currentCity);

	        //First check if in the immediate layer the city exists.
	        if(cities.contains(destination)) {    //We found it! Bubble back up!
	            return true;
	        }

	        //Go to the next layer only if we didn't process this city before
	        for(String city : cities) {
	            if(!avoidCities.contains(city)
	                && isConnected(city, destination, avoidCities)) {    //We found it! Keep bubbling it up!
	                    return true;
	            }
	        }
	        //By this point, we have tried all the possible connections, and couldn't find anything.
	        return false;
	    }

}
