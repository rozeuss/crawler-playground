package org.example.crawler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AppProperties
{
    @Getter
    private final Properties properties;
    
    public AppProperties()
    {
        InputStream resourceAsStream = AppProperties.class.getClassLoader()
                                                          .getResourceAsStream("org.example.crawler/app.properties");
        this.properties = new Properties();
        try
        {
            if(resourceAsStream != null)
            {
                properties.load(resourceAsStream);
            }
        }
        catch(IOException ex)
        {
            log.error("Cannot load properties.", ex);
        }
    }
    
    public String getCrawlStorageFolder()
    {
        return properties.getProperty("crawlStorageFolder");
    }
    
    public String getResultsFilename()
    {
        return properties.getProperty("resultsFilename");
    }
    
    public int getPolitenessDelay()
    {
        return Integer.parseInt(properties.getProperty("politenessDelay"));
    }
    
    public int getMaxDepthOfCrawling()
    {
        return Integer.parseInt(properties.getProperty("maxDepthOfCrawling"));
    }
    
    public int getMaxPagesToFetch()
    {
        return Integer.parseInt(properties.getProperty("maxPagesToFetch"));
    }
    
    public String getCdnUrl()
    {
        return properties.getProperty("cdnUrl");
    }
    
    public List<String> getSeeds()
    {
        return new ArrayList<>(new DefaultListDelimiterHandler(',').split(properties.getProperty("seeds"), true));
    }
}
