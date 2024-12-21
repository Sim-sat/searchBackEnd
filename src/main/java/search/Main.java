package search;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class Main {
    private final static String[] seedUrls = {"http://creamy-liederkranz24.cheesy1",
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

    public static Map<String, WebsiteData> forwardIndexMap;
    public static Map<String, Map<String, Double>> reverseIndexMap;
    public static Crawler crawler;
    ForwardIndex forwardIndex;

    void onStart(@Observes StartupEvent ev) throws IOException {

        final Logger LOGGER = Logger.getLogger("ListenerBean");
        crawler = new Crawler(seedUrls);
        forwardIndex = new ForwardIndex();
        forwardIndex.addEntries(crawler.start());
        forwardIndexMap = forwardIndex.getForwardIndex();
        LOGGER.info(String.valueOf(forwardIndexMap.size()));
        reverseIndexMap = ReverseIndex.getReverseIndex(forwardIndexMap);
        forwardIndex.calculateVector(reverseIndexMap);
        forwardIndex.calculatePageRankDamped();

        System.out.println("You can now start searching. ");
        System.out.println("Pages index: " + crawler.getNumberOfSites());
    }

}
