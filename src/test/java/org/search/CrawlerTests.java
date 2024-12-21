package org.search;

import org.junit.jupiter.api.Test;
import search.Crawler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;


/**
 * Unit tests for the crawler.
 */
class CrawlerTests {

    private final String[] seedUrls = {"http://creamy-liederkranz24.cheesy1",
            "http://buttery-redleicester.cheesy1",
            "http://rich-jarlsberg.cheesy1",
            "http://shropshireblue24.cheesy2",
            "http://burrata.cheesy2",
            "http://stilton24.cheesy2",
            "http://mozzarella-and-edam.cheesy3",
            "http://asiago-and-brie.cheesy3",
            "http://quark-and-slovakianbryndza.cheesy3",
            "http://gruyere24.cheesy4",
            "http://tangy-gruyere.cheesy4",
            "http://edam24.cheesy4",
            "http://cheddar24.cheesy6"};

    @Test
    void crawlAllWebsitesInProvidedNetwork() throws IOException {
        int numPagesCrawled = 0;

        // Add your code here to get the number of crawled pages
        Crawler crawler = new Crawler(seedUrls);

        crawler.start();
        numPagesCrawled = crawler.getNumberOfSites();
        // Get the crawled pages

        // Verify that the number of crawled pages is correct, i.e. the same as stated
        // in the JSON file
        assertEquals(260, numPagesCrawled);
    }


    @Test
    void findCorrectNumberOfLinks() throws IOException {
        // Iterate over all test JSON files

        int numLinks = 0;

        // Add your code here to get the number of links
        Crawler crawler = new Crawler(seedUrls);

        crawler.start();
        numLinks = crawler.getNumberOfLinks();

        // Get the number of links

        // Verify that the number of links is correct, i.e. the same as stated in the
        // JSON file
        assertEquals(3824, numLinks);
    }
}
