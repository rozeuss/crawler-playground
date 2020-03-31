package org.example.crawler;

import lombok.Value;

import java.util.Set;

@Value
class SiteMap
{
    Set<PageMetadata> locations;
}
