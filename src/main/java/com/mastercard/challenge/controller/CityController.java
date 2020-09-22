package com.mastercard.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mastercard.challenge.service.CityConnectionService;

import java.util.Map;

@RestController
public class CityController {

	@Autowired
	CityConnectionService cityConnectionService;

	/**
	 * Example:
	 * {@literal http://localhost:8080/connected?origin=Boston@&destination=Newark}
	 * 
	 * @param requestParams Expected to contain origin and destination.
	 * @return if two cities connected, returns "yes", else returns "no"
	 */
	@GetMapping("/connected")
	public String findCitiesConnected(@RequestParam Map<String, String> requestParams) {
		if (cityConnectionService.isConnected(requestParams.get("origin"), requestParams.get("destination")))
			return "yes";
		return "no";
	}
}
