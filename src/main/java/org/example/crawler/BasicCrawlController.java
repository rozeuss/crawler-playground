package org.example.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicCrawlController
{
    private final AppProperties propertiesHolder = new AppProperties();
    
    @SneakyThrows
    public void crawl()
    {
        CrawlController controller = instantiateCrawlController(getCrawlConfig());
        int numberOfCrawlers = 8;
        controller.start(() -> new BasicCrawler(propertiesHolder), numberOfCrawlers);
        storeSiteMap(controller);
    }
    
    private CrawlConfig getCrawlConfig()
    {
        var config = new CrawlConfig();
        config.setCrawlStorageFolder(propertiesHolder.getCrawlStorageFolder());
        config.setPolitenessDelay(propertiesHolder.getPolitenessDelay());
        config.setMaxDepthOfCrawling(propertiesHolder.getMaxDepthOfCrawling());
        config.setMaxPagesToFetch(propertiesHolder.getMaxPagesToFetch());
        return config;
    }
    
    @SneakyThrows
    private CrawlController instantiateCrawlController(CrawlConfig config)
    {
        var pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        propertiesHolder.getSeeds().forEach(controller::addSeed);
        return controller;
    }
    
    private void storeSiteMap(CrawlController controller) throws IOException
    {
        SiteMap siteMap = new SiteMap(getLocations(controller));
        FileUtils.writeStringToFile(getResultsFile(), new ObjectMapper().writeValueAsString(siteMap),
                                    StandardCharsets.UTF_8, true
                                   );
    }
    
    private static Set<PageMetadata> getLocations(CrawlController controller)
    {
        return controller
            .getCrawlersLocalData()
            .stream()
            .map(castUnchecked())
            .flatMap(Collection::stream)
            .map(PageMetadata.class::cast)
            .collect(Collectors.toSet());
    }
    
    @SuppressWarnings("unchecked")
    private static Function<Object, ConcurrentHashMap.KeySetView<PageMetadata, Boolean>> castUnchecked()
    {
        return v -> (ConcurrentHashMap.KeySetView<PageMetadata, Boolean>) v;
    }
    
    private File getResultsFile()
    {
        var file = new File(propertiesHolder.getCrawlStorageFolder() + propertiesHolder.getResultsFilename());
        FileUtils.deleteQuietly(file);
        return file;
    }
}
