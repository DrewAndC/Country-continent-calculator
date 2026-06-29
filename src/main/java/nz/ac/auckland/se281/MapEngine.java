package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** This class is the main entry point. */
public class MapEngine {

  private Map<String, Country> graph = new LinkedHashMap<>();

  // country = node, neighbours = edges

  public MapEngine() {
    // add other code here if you wan
    loadMap(); // keep this mehtod invocation
  }

  /** invoked one time only when constracting the MapEngine class. */
  private void loadMap() {
    // Read countries and adjacencies from files
    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();

    // Populate the graph with countries, their continents, fuel costs, and neighbours
    for (String country : countries) {
      String[] parts = country.split(",");
      String name = parts[0].trim();
      String continent = parts[1].trim();
      int fuelCost = Integer.parseInt(parts[2].trim());
      graph.put(name, new Country(name, continent, fuelCost));
    }

    // For each adjacency, add the neighbour countries to the corresponding country
    for (String adjacency : adjacencies) {
      String[] parts = adjacency.split(",");
      String countryName = parts[0].trim();
      Country country = graph.get(countryName);
      if (country != null) {
        for (int i = 1; i < parts.length; i++) {
          String neighbourName = parts[i].trim();
          if (graph.containsKey(neighbourName)) {
            country.addNeighbour(neighbourName);
          }
        }
      }
    }
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    while (true) { // keep asking until a valid country is entered
      MessageCli.INSERT_COUNTRY.printMessage(); // Prompt the user to insert a country name
      String countryName =
          Utils.capitalizeFirstLetterOfEachWord(
              Utils.scanner
                  .nextLine()); // Capitalize the first letter of each word of the input from user
      Country country = graph.get(countryName);
      try { // Check if the country exists in the graph
        // If the country is not found, an exception will be thrown
        validateCountry(countryName);
        MessageCli.COUNTRY_INFO.printMessage(
            country.getName(),
            country.getContinent(),
            String.valueOf(country.getFuelCost()),
            country.getNeighbours().toString());
        break; // Exit the loop if a valid country is found and information is printed
      } catch (CountryNotFoundException e) { // Catch the exception if the country is not found
        MessageCli.INVALID_COUNTRY.printMessage(e.getMessage());
      }
    }
  }

  // This method validates if the country exists in the graph.
  private String validateCountry(String countryName) throws CountryNotFoundException {
    if (!graph.containsKey(countryName)) {
      throw new CountryNotFoundException(countryName);
    }
    return countryName;
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {
    String sourceCountry;
    String destinationCountry;
    while (true) { // keep asking until a valid source country is entered
      MessageCli.INSERT_SOURCE.printMessage();
      sourceCountry = Utils.capitalizeFirstLetterOfEachWord(Utils.scanner.nextLine());
      try {
        validateCountry(sourceCountry);
        break;
      } catch (CountryNotFoundException e) {
        MessageCli.INVALID_COUNTRY.printMessage(e.getMessage());
      }
    }
    while (true) { // keep asking until a valid destination country is entered
      MessageCli.INSERT_DESTINATION.printMessage();
      destinationCountry = Utils.capitalizeFirstLetterOfEachWord(Utils.scanner.nextLine());
      try {
        validateCountry(destinationCountry);
        break;
      } catch (CountryNotFoundException e) {
        MessageCli.INVALID_COUNTRY.printMessage(e.getMessage());
      }
    }

    // if source and destination countries are the same, print a message and leave the method
    if (sourceCountry.equals(destinationCountry)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }

    // If the source and destination countries are valid, find the shortest path using BFS
    BreadthFirstSearch bfs = new BreadthFirstSearch(graph);
    List<String> path = bfs.findShortestPath(sourceCountry, destinationCountry);
    if (path != null) {
      MessageCli.ROUTE_INFO.printMessage(path.toString()); // Print the route information
    }

    LinkedHashMap<String, Integer> visitedContinents = new LinkedHashMap<>();
    int totalFuelCost = 0;
    // source country is the first country in the path
    Country source = graph.get(sourceCountry);
    visitedContinents.putIfAbsent(source.getContinent(), 0);
    // loops through the path, starting from the second country to the second last country
    // to calculate the total fuel cost and the fuel cost for each continent
    for (int i = 1; i < path.size() - 1; i++) {
      String countryName = path.get(i);
      Country country = graph.get(countryName);
      String continent = country.getContinent();
      int fuelCost = country.getFuelCost();
      totalFuelCost += fuelCost;

      visitedContinents.put(continent, visitedContinents.getOrDefault(continent, 0) + fuelCost);
    }
    // destination country is the last country in the path
    Country destination = graph.get(destinationCountry);
    visitedContinents.putIfAbsent(destination.getContinent(), 0);

    // loops through the visited continents to create a list of strings for output
    List<String> continentOutput = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : visitedContinents.entrySet()) {
      String continent = entry.getKey();
      int continentTotalFuelCost = entry.getValue();
      continentOutput.add(continent + " (" + continentTotalFuelCost + ")");
    }

    // Find the continent with the highest fuel cost
    String maxFuelContinent = null;
    int maxFuelCost = -1;
    for (Map.Entry<String, Integer> entry : visitedContinents.entrySet()) {
      if (entry.getValue() > maxFuelCost) {
        maxFuelCost = entry.getValue();
        maxFuelContinent = entry.getKey() + " (" + maxFuelCost + ")";
      }
    }

    MessageCli.FUEL_INFO.printMessage(String.valueOf(totalFuelCost)); // Print total fuel cost
    MessageCli.CONTINENT_INFO.printMessage(
        continentOutput.toString()); // Print visited continents and their fuel costs
    MessageCli.FUEL_CONTINENT_INFO.printMessage(
        maxFuelContinent); // Print continent with max fuel cost
  }
}
