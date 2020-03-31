package org.example.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BasicCrawler extends WebCrawler
{
    static final Pattern STATIC_CONTENT_EXTENSIONS = Pattern.compile(".*(\\.(js|css|bmp|gif|jpe?g|png|tiff?))$");
    
    Set<PageMetadata> localData = ConcurrentHashMap.newKeySet();
    String cdnUrl;
    String crawledDomain;
    
    public BasicCrawler(AppProperties propertiesHolder)
    {
        this.cdnUrl = propertiesHolder.getCdnUrl();
        this.crawledDomain = Optional.of(propertiesHolder.getSeeds())
                                     .map(v -> v.get(0))
                                     .orElseThrow(() -> new IllegalArgumentException("Seeds are not provided."));
    }
    
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String href = url.getURL().toLowerCase();
        return href.startsWith(crawledDomain);
    }
    
    @Override
    public void visit(Page page)
    {
        var pageMetadataBuilder = PageMetadata.builder().location(page.getWebURL().getURL());
        
        Set<WebURL> outgoingUrls = page.getParseData().getOutgoingUrls();
        Map<Boolean, Set<WebURL>> partitionedByStaticContent = outgoingUrls
            .stream()
            .collect(Collectors.partitioningBy(isStaticContent(), Collectors.toSet()));
        Set<String> staticContent = partitionedByStaticContent
            .get(true)
            .stream()
            .map(WebURL::getURL)
            .collect(Collectors.toSet());
        pageMetadataBuilder.staticContent(staticContent);
        
        Map<Boolean, Set<String>> sitesPerDomain = partitionedByStaticContent
            .get(false)
            .stream()
            .map(WebURL::getURL)
            .collect(Collectors.groupingBy(url -> url.startsWith(crawledDomain), Collectors.toSet()));
        pageMetadataBuilder.internalSites(sitesPerDomain.get(true));
        pageMetadataBuilder.externalSites(sitesPerDomain.get(false));
        
        localData.add(pageMetadataBuilder.build());
    }
    
    @Override
    public Object getMyLocalData()
    {
        return localData;
    }
    
    private Predicate<WebURL> isStaticContent()
    {
        return url -> url.getURL().startsWith(cdnUrl)
                      || STATIC_CONTENT_EXTENSIONS.matcher(url.getURL()).matches();
    }
}
