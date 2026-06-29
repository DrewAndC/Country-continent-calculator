package nz.ac.auckland.se281;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BreadthFirstSearch {
  private Map<String, Country> graph;

  public BreadthFirstSearch(Map<String, Country> graph) {
    this.graph = graph;
  }

  // Finds the shortest path between two countries using BFS.
  public List<String> findShortestPath(String start, String end) {
    Set<String> visited = new LinkedHashSet<>(); // track visited countries
    Map<String, String> parentMap =
        new LinkedHashMap<>(); // maps each country to its parent in the path
    Queue<String> queue = new LinkedList<>(); // queue to hold countries to be explored

    visited.add(start);
    queue.add(start);

    // explores the graph until the queue is empty or the end country is found
    while (!queue.isEmpty()) {
      String current = queue.poll(); // get the first country in the queue

      Country currentCountry = graph.get(current);
      // loop through the neighbours of the current country in order to reach the end country
      for (String neighbour : currentCountry.getNeighbours()) {
        // Check if the neighbour country has not been visited yet
        if (!visited.contains(neighbour)) {
          visited.add(neighbour);
          parentMap.put(neighbour, current);
          queue.add(neighbour);

          if (neighbour.equals(end)) {
            return constructPath(
                parentMap, start, end); // return the path if the end country is found
          }
        }
      }
    }
    return null;
  }

  // Constructs the path from start to end using the parent map.
  private List<String> constructPath(Map<String, String> parentMap, String start, String end) {
    List<String> path = new LinkedList<>();
    String current = end;

    // while loop to backtrack from the end country to the start country
    while (current != null) {
      path.add(0, current);
      current = parentMap.get(current);
    }

    return path;
  }
}
