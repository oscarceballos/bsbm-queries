# BSBM-QUERIES library

A Java library to generated the SPARQL queries templates of [Berlin SPARQL Benchmark (BSBM)](http://wifo5-03.informatik.uni-mannheim.de/bizer/berlinsparqlbenchmark/) in a valid query based on specific dataset.

## System requirements

* Oracle JDK 1.8.0_144
* Apache Maven 3.5.0 or higher

## Compile the BSBM-QUERIES

Deploy with maven usign the configuration in pom.xml

```
mvn clean install compile package
```

## Generate a valid query based on specific dataset

Run bsbm-query Java library with query template and dataset as parameters to generate a SPARQL query valid

```
java -jar <query-template> <dataset>
```
