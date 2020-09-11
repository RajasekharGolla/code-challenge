# Mastercard code Challenge

### Summary

This application is deployed as a Spring Boot App that expose the endpoint:

http://localhost:8080/connected?origin=city1&destination=city2

It responds with 'yes' if city1 is connected to city2. Otherwise it responds with 'no'.

### Links 

[Code](src/main/java/com/mastercard/challenge)

[Unit Tests](src/test/java/com/mastercard/challenge/ChallengeApplicationTests.java)

[city.txt](src/main/resources/city.txt)


### Operation

The application loads all information from city.txt into a hashmap containing a hashset.

We are saving all of the connections in this hashmap in both directions. Example city.txt:

    Boston, New York
    Philadelphia, Newark
    Newark, Boston
    Trenton, Albany

Will save as this map and set:

    {
      boston -> (new york, newark),
      new york -> (boston)
      philadelphia -> (newark),
      newark -> (boston, philadelphia),
      trenton -> (albany),
      albany -> (trenton),
    }

Note that everything is saved in lowercase to compare ignoring case.


### Self referenced cities

http://localhost:8080/connected?origin=Boston&destination=Boston

Boston is not a city listed in the map. Yet, it feels intuitive to understand that any city should be connected to itself, regardless if it is in the map or not.

