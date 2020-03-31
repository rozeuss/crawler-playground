package org.example.crawler;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
class PageMetadata
{
    String location;
    Set<String> internalSites;
    Set<String> externalSites;
    Set<String> staticContent;
}
