package org.search;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Crawls given websites from a list of urls and saves the results as a List of
 * WebsiteData. {@link org.Logic.WebsiteData}
 *
 * @author Simon Sattelberger
 */
public class Crawler {
    private String[] seedUrls;
    private List<String> foundWebsites = new ArrayList<>();
    private List<WebsiteData> crawledSites = new ArrayList<>();
    private final int LIMIT = 1024;
    private int numberOfLinks;

    public Crawler(String[] pSeedUrls) {

        this.seedUrls = pSeedUrls;
    }

    public static void main(String[] args) throws IOException {

        String[] pSeedUrls = {"http://mozzarella-and-edam.cheesy3",
                "http://asiago-and-brie.cheesy3",
                "http://quark-and-slovakianbryndza.cheesy3"};
        Crawler main = new Crawler(pSeedUrls);
        main.start();

    }

    /**
     * Starts the crawler
     *
     * @return List of all crawled Websites saved as WebsiteData Object
     * {@link WebsiteData}
     * @throws IOException
     */
    public List<WebsiteData> start() throws IOException {
        LinkedList<String> queue = new LinkedList<>();

        int counter = 0;
        // adds the seedUrls to the queue
        for (String seedUrl : seedUrls) {
            queue.add(seedUrl);
            foundWebsites.add(seedUrl);
        }

        // takes out first Element of the queue and extracts its links
        while (!queue.isEmpty() && counter < LIMIT) {
            String url = queue.poll();
            Document document = Jsoup.connect(url).proxy("localhost", 80).get();
            Elements links = document.select("a");

            for (Element link : links) {
                // adds found links to the queue if they haven't been visited
                if (!foundWebsites.contains(link.attr("href"))) {
                    foundWebsites.add(link.attr("href"));
                    queue.add(link.attr("href"));
                }
            }

            // parsing the document and creating WebsiteData Object

            String title = document.selectFirst("title").text();
            String header = document.selectFirst("header").text();
            Element content = document.selectFirst("main");
            List<String> outgoingLinks = document.select("a").eachAttr("href");

            content.select("a").remove();
            WebsiteData website = new WebsiteData(url, title, header, content.text(), outgoingLinks);
            crawledSites.add(website);
            numberOfLinks = numberOfLinks + links.size();
            counter++;
        }
        return crawledSites;
    }

    /**
     * @return number of all crawled links
     */
    public int getNumberOfLinks() {
        return numberOfLinks;
    }

    /**
     * @return number of found sites
     */
    public int getNumberOfSites() {
        return foundWebsites.size();
    }

}
