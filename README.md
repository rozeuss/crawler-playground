# Crawler playground

[Crawler4j](https://github.com/yasserg/crawler4j) is a Java library which provides a simple interface for crawling the web. 
It's pretty configurable, although there are many opened issues and hanging pull-requests.

## Prerequisites
   
Project was built with:
   * Java 14
   * Gradle 6.3
   
## Run

Provide input properties `app.properties`.

Build project:
`gradle clean build`

Test project:
`gradle clean test`

Run project:
`gradle run`

## Result

Crawling's result is `sitemap.json` stored in `crawledStorageFolder + resultsFilename` path. Example sitemap can be found in 
project resources.

## To consider
- usage of **_jsoup_** for some advanced extracting,
- dedicated interfaces for static resources/location.



