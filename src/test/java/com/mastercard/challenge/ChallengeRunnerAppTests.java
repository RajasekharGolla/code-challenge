package com.mastercard.challenge;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import com.mastercard.challenge.controller.CityController;
import com.mastercard.challenge.service.CityConnectionService;
import com.mastercard.challenge.service.DefaultCityConnectionService;
import com.mastercard.challenge.util.CityFileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ChallengeRunnerAppTests {

	Logger logger = LoggerFactory.getLogger(ChallengeRunnerAppTests.class);

	@Autowired
	private CityController controller;

	@Autowired
	CityConnectionService cityConnectionService;

	@Test
	void testCitiesConnetecd() {
		Map<String, String> map = new HashMap<>();
		map.put("origin", "Newark");
		map.put("destination", "Boston");
		assertEquals("yes", controller.findCitiesConnected(map));
	}

	@Test
	void testCitiesNotConnetecd() {
		Map<String, String> map = new HashMap<>();
		map.put("origin", "Newark");
		map.put("destination", "Albany");
		assertEquals("no", controller.findCitiesConnected(map));
	}

	/**
	 * Given challenge reading from file and validation.
	 */
	@Test
	void testValidate() {
		assertTrue(cityConnectionService.isConnected("Boston", "Newark"));
		assertTrue(cityConnectionService.isConnected("Boston", "Philadelphia"));
		assertFalse(cityConnectionService.isConnected("Philadelphia", "Albany"));
		assertFalse(cityConnectionService.isConnected(null, null));
	}

	/**
	 * there is no exception if no city connection found.
	 */
	@Test
	void testNoConnectedCities() {
		assertFalse(cityConnectionService.isConnected("Boston", "Albany"));
	}
	
	/**
	 * there is no exception if no city connection found.
	 */
	@Test
	void testUnknownCities() {
		assertFalse(cityConnectionService.isConnected("some city", "some other city"));
	}

	/**
	 * Any city should connect to itself. Even if it's not in the map.
	 */
	@Test
	void testSelfReference() {
		assertTrue(cityConnectionService.isConnected("Newark", "Newark"));
		assertTrue(cityConnectionService.isConnected("Boston", "Boston"));
	}

	/**
	 * test the connections are bidirectional
	 */
	@Test
	void testBidirectionalCheck() {
		DefaultCityConnectionService connections = new DefaultCityConnectionService(null);
		connections.addConnection("Philadelphia", "Newark");
		connections.addConnection("Newark", "Boston");
		assertTrue(connections.isConnected("Boston", "Philadelphia"));
	}

	/**
	 * Test adding the same connection multiple times doesn't break isConnected
	 * function
	 */
	@Test
	void testDuplicate() {
		DefaultCityConnectionService connections = new DefaultCityConnectionService(null);
		connections.addConnection("Boston", "Newark");
		connections.addConnection("Boston", "Newark");
		connections.addConnection("Newark", "Boston");
		connections.addConnection(" NeWark", "Boston\t");
		assertTrue(connections.isConnected("Boston", "NeWark"));
	}

	@Test
	void testInitCityConnectionWithInvalidFile() {
		CityFileManager fileManager = Mockito.spy(CityFileManager.class);
		DefaultCityConnectionService connectionService = new DefaultCityConnectionService(fileManager);
		Mockito.doReturn("someinvalidfilename").when(fileManager).getFileName();
		String message = connectionService.initCityConnections();
		assertTrue(message.startsWith("Error"));
	}

	@Test
	void testMockNoCityEntriesInFile() throws IOException {
		CityFileManager fileManager = Mockito.spy(CityFileManager.class);
		DefaultCityConnectionService connectionService = new DefaultCityConnectionService(fileManager);

		Mockito.doReturn(new ArrayList<>()).when(fileManager).getCities();
		String message = connectionService.initCityConnections();
		assertTrue(message.startsWith("No cities"));

	}
	
	/**
	 * Checking a file with missing connection pair.
	 * @throws IOException
	 */
	@Test
	void testMissingCityEntriesInFile() throws IOException {
		String fileName = "missing-city.txt";
		InputStream resource = new ClassPathResource(fileName).getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
		long lineCount = 0;	
		while (reader.readLine() != null) lineCount++;
		
		CityFileManager fileManager = Mockito.spy(CityFileManager.class);
		DefaultCityConnectionService connectionService = new DefaultCityConnectionService(fileManager);
		Mockito.doReturn(fileName).when(fileManager).getFileName();
		String message = connectionService.initCityConnections();
		assertTrue(lineCount != Integer.valueOf(message.substring(0, message.indexOf(" cities")))/2);

	}

}
