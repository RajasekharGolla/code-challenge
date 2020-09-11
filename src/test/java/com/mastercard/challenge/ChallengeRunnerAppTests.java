package com.mastercard.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ChallengeRunnerAppTests {

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
		CityConnectionService connections = new CityConnectionService();
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
		CityConnectionService connections = new CityConnectionService();
		connections.addConnection("Boston", "Newark");
		connections.addConnection("Boston", "Newark");
		connections.addConnection("Newark", "Boston");
		connections.addConnection(" NeWark", "Boston\t");
		assertTrue(connections.isConnected("Boston", "NeWark"));
	}



}
