package search;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Crawls given websites from a list of urls and saves the results as a List of
 * WebsiteData. {@link }
 *
 * @author Simon Sattelberger
 */
public class Crawler {
    private String[] seedUrls;
    private List<String> foundWebsites = new ArrayList<>();
    private List<WebsiteData> crawledSites = new ArrayList<>();
    private final int LIMIT = 1024;
    private int numberOfLinks;
    private static final Logger logger = Logger.getLogger(Crawler.class.getName());
    public Crawler(String[] pSeedUrls) {

        this.seedUrls = pSeedUrls;
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
