package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.List;

public class Country {
  private String name;
  private String continent;
  private int fuelCost;
  private List<String> neighbours;

  // Constructor to initialize the country with its name, continent, fuel cost, and neighbours
  public Country(String name, String continent, int fuelCost) {
    this.name = name;
    this.continent = continent;
    this.fuelCost = fuelCost;
    this.neighbours = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public String getContinent() {
    return continent;
  }

  public int getFuelCost() {
    return fuelCost;
  }

  public List<String> getNeighbours() {
    return neighbours;
  }

  public void addNeighbour(String neighbour) {
    // Add a neighbour to the list if it is not already in the list
    if (!neighbours.contains(neighbour)) {
      neighbours.add(neighbour);
    }
  }
}
