# VRP Challenge solution in Java 

* This code attempts to solve the Vehicle Routing Problem with multiple drivers using:
  * Nearest neighbor as the basic heuristic.
  * Some local optimizations:
    * Prioritizing "clusters" of the routes that are potentially the most problematic, as measured by their distance to depot, and from pickup to dropoff.
    * A basic implementation of simulated annealing, using exponentially decaying temperature to avoid local minimums.
* It was developed/tested using Java 21, but anything above Java 8 should work well.
* This is a pure Java solution.  It has zero external dependencies.
* Since the requirements indicated a 30-second hard limit on execution time, the program keeps iterating for 25 seconds, at which point it stops and prints out the best/lowest-cost solution it found.

# How to build and run
* There is a pre-packaged jar included under the `/bin` directory in this repo.
* In order to build the jar yourself and run it, you can use Maven to package it into a jar with `mvn package`, and it should create a file called `challenge-1.0-SNAPSHOT-jar-with-dependencies.jar` under `target/`.
* Once you have the jar, you can run the program by executing the following command:
  * `java -jar challenge-1.0-SNAPSHOT-jar-with-dependencies.jar path/to/problemfile.txt`  


# Validation using evaluateShared.py script
* This code was validated with the provided `evaluateShared.py` script, and to iterate when adding optimizations.
* The best/lowest-cost result after applying all the optimizations, and running for 25 seconds, is copied below:
```
mean cost: 51750.56198202616
mean run time: 25078.42116355896ms
```
 
# References used:
  * https://en.wikipedia.org/wiki/Vehicle_routing_problem
  * https://en.wikipedia.org/wiki/Travelling_salesman_problem
  * https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm
  * https://en.wikipedia.org/wiki/Christofides_algorithm
  * https://stackabuse.com/graphs-in-java-minimum-spanning-trees-prims-algorithm/
  * https://stackoverflow.com/questions/562904/clustering-algorithm-for-paper-boys/
  * https://stackoverflow.com/questions/6239148/travelling-salesman-with-multiple-salesmen
  * https://arxiv.org/pdf/2303.04147
  * https://www.cs.ubc.ca/~hutter/previous-earg/EmpAlgReadingGroup/TSP-JohMcg97.pdf
  * https://www.sciencedirect.com/science/article/abs/pii/S0305048304001550
  * https://www.sciencedirect.com/science/article/pii/S2405896316311028
  * https://www.youtube.com/watch?v=GiDsjIBOVoA
