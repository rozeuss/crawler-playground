# Crawler playground

Crawler4j is a Java library which provides a simple interface for crawling the web. 

## Prerequisites
   
Project was built with:
   * Java 14
   * Gradle 6.3
   
## Run

Provide input properties `app.properties`.

Build project:
`gradle clean build`

Run project:
`gradle run`

## Result

Crawling's result is `sitemap.json` stored in `crawledStorageFolder + resultsFilename` path. Example sitemap can be found in 
project resources.

## To consider
- usage of **_jsoup_** for some advanced extracting,
- write tests against html,
- dedicated interfaces for static resources/location.



