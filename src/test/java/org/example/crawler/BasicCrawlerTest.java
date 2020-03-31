package org.example.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
class BasicCrawlerTest
{
    static BasicCrawler basicCrawler;
    static Page domainPage;
    
    static final List<String> EXTERNAL_SITES =
        Arrays.asList(
            "https://twitter.com/wiprodigital",
            "https://s.w.org/",
            "https://www.googletagmanager.com/ns.html?id=GTM-P736BTD",
            "https://www.linkedin.com/company/wipro-digital",
            "https://www.facebook.com/WiproDigital/",
            "https://px.ads.linkedin.com/collect/?pid=696835&fmt=gif"
                     );
    
    static final List<String> STATIC_CONTENT =
        Arrays.asList(
            "https://s17776.pcdn.co/wp-includes/wlwmanifest.xml",
            "https://wiprodigital.com/1_files/logo.png",
            "https://wiprodigital.com/1_files/style.css",
            "https://s17776.pcdn.co/wp-content/uploads/2016/08/Fav_icon_144x144.png",
            "https://wiprodigital.com/1_files/style.min.css",
            "https://wiprodigital.com/1_files/logo-dk-2X.png"
                     );
    
    @BeforeAll
    static void setUp()
    {
        var appProperties = new AppProperties();
        String url = Optional.of(appProperties.getSeeds())
                             .map(v -> v.get(0))
                             .orElseThrow(() -> new IllegalArgumentException("Seeds are not provided."));
        setupCrawledDomainPage(url);
        basicCrawler = new BasicCrawler(appProperties);
    }
    
    @SneakyThrows
    private static void setupCrawledDomainPage(String url)
    {
        WebURL crawledDomainUrl = new WebURL();
        crawledDomainUrl.setURL(url);
        domainPage = new Page(crawledDomainUrl);
        domainPage.setContentData(IOUtils.toByteArray(
            BasicCrawlerTest.class.getClassLoader()
                                  .getResourceAsStream("org.example.crawler/domain-page.html")));
        Parser parser = new Parser(new CrawlConfig());
        parser.parse(domainPage, url);
    }
    
    @Test
    void shouldVisitPagesInsideDomain()
    {
        assertTrue(basicCrawler.shouldVisit(null, domainPage.getWebURL()));
        final String nestedUrl = domainPage.getWebURL().getURL() + "/who-we-are/";
        final WebURL nestedWebUrl = new WebURL();
        nestedWebUrl.setURL(nestedUrl);
        assertTrue(basicCrawler.shouldVisit(null, nestedWebUrl));
    }
    
    @Test
    void shouldNotVisitOtherDomains()
    {
        String facebookUrl = "https://www.facebook.com/";
        final WebURL facebookWebUrl = new WebURL();
        facebookWebUrl.setURL(facebookUrl);
        assertFalse(basicCrawler.shouldVisit(null, facebookWebUrl));
        
        String twitterUrl = "https://twitter.com/";
        final WebURL twitterWebUrl = new WebURL();
        twitterWebUrl.setURL(twitterUrl);
        assertFalse(basicCrawler.shouldVisit(null, twitterWebUrl));
    }
    
    @Test
    void visit()
    {
        basicCrawler.visit(domainPage);
        var myLocalData = (ConcurrentHashMap.KeySetView<PageMetadata, Boolean>) basicCrawler.getMyLocalData();
        PageMetadata pageMetadata = myLocalData.iterator().next();
        assertAll(
            () -> assertNotNull(pageMetadata),
            () -> assertEquals(pageMetadata.getLocation(), domainPage.getWebURL().getURL()),
            () -> assertEquals(60, pageMetadata.getInternalSites().size()),
            () -> assertIterableEquals(EXTERNAL_SITES, pageMetadata.getExternalSites()),
            () -> assertIterableEquals(STATIC_CONTENT, pageMetadata.getStaticContent())
                 );
    }
    
    @Test
    void shouldReturnNotNullLocalData()
    {
        assertNotNull(basicCrawler.getMyLocalData());
    }
}
