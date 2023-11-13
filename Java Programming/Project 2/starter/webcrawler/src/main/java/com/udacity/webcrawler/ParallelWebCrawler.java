package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
    private final Clock clock;
    private final Duration timeout;
    private final int popularWordCount;
    private final ForkJoinPool pool;
    @Inject
    private PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    private final int maxDepth;

    @Inject
    ParallelWebCrawler(
            Clock clock,
            @Timeout Duration timeout,
            @PopularWordCount int popularWordCount,
            @TargetParallelism int threadCount,
            @IgnoredUrls List<Pattern> ignoredUrls,
            @MaxDepth int maxDepth) {
        this.clock = clock;
        this.timeout = timeout;
        this.popularWordCount = popularWordCount;
        this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
        this.ignoredUrls = ignoredUrls;
        this.maxDepth = maxDepth;
    }

    @Override
    public CrawlResult crawl(List<String> startingUrls) {
        Instant deadline = clock.instant().plus(timeout);
        Map<String, Integer> counts = new HashMap<>();
        Set<String> visitedUrls = new HashSet<>();

        Collections.synchronizedCollection(startingUrls).forEach(url -> {
            pool.invoke(new CrawlInternal(url, counts, visitedUrls, deadline, maxDepth));
        });
        if (counts.isEmpty()) {
            return new CrawlResult.Builder()
                    .setWordCounts(counts)
                    .setUrlsVisited(visitedUrls.size())
                    .build();
        }

        return new CrawlResult.Builder()
                .setWordCounts(WordCounts.sort(counts, popularWordCount))
                .setUrlsVisited(visitedUrls.size())
                .build();

    }

    @Override
    public int getMaxParallelism() {
        return Runtime.getRuntime().availableProcessors();
    }

    final class CrawlInternal extends RecursiveAction {
        private String url;
        private Map<String, Integer> counts;
        private Set<String> visitedUrls;
        private Instant deadline;
        private int maxDepth;

        public CrawlInternal(String url, Map<String, Integer> counts, Set<String> visitedUrls, Instant deadline, @MaxDepth int maxDepth) {
            this.url = url;
            this.counts = counts;
            this.visitedUrls = visitedUrls;
            this.deadline = deadline;
            this.maxDepth = maxDepth;
        }

        @Override
        protected void compute() {
            if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
                return;
            }

            if (ignoredUrls.stream().anyMatch(ignoredUrl -> ignoredUrl.matcher(url).matches())) return;

            synchronized (this) {
                if (visitedUrls.contains(url) || !visitedUrls.add(url)) {
                    return;
                }
            }

            PageParser.Result parseResult = parserFactory.get(url).parse();

            Collections.synchronizedCollection(parseResult.getWordCounts().entrySet())
                    .forEach(e -> {
                        String key = e.getKey();
                        Integer val = e.getValue();

                        if (counts.containsKey(key)) {
                            counts.put(key, val + counts.get(key));
                        } else {
                            counts.put(key, val);
                        }
                    });

            List<CrawlInternal> crawlInternalSubTaskList = parseResult.getLinks().stream()
                    .map(link -> new CrawlInternal(link, counts, visitedUrls, deadline, maxDepth - 1)).toList();

            invokeAll(crawlInternalSubTaskList);
        }
    }

}
