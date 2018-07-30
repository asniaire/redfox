package com.asniaire.redfox.crawler;

import com.asniaire.redfox.crawler.api.ImageCrawler;
import com.asniaire.redfox.crawler.exceptions.UrlNotAccessibleException;
import com.asniaire.redfox.processor.api.ImageProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsoupImageCrawler extends ImageCrawler {

    private static final String IMG_SRC_JPG = "img[src$=.jpg]";
    private static final String IMG_SRC_PNG = "img[src$=.png]";
    private static final String SRC_ATTR = "src";
    private static final String LINK_HREF = "a[href]";
    private static final String HREF_ATTR = "abs:href";

    private Document document;

    public JsoupImageCrawler(
            ImageProcessor imageProcessor,
            String url,
            Integer searchingLevels,
            boolean isFullParsing) {
        super(imageProcessor, url, searchingLevels, isFullParsing);
    }

    public JsoupImageCrawler(ImageProcessor imageProcessor, String url) {
        super(imageProcessor, url);
    }

    @Override
    protected void openUrl(String url) throws UrlNotAccessibleException {
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException ex) {
            throw new UrlNotAccessibleException(url);
        }
    }

    @Override
    protected List<String> getImages() {
        final Elements jpgs = document.select(IMG_SRC_JPG);
        final Elements pngs = document.select(IMG_SRC_PNG);
        final Stream<String> images = Stream.concat(
                pngs.stream().map(extractAttribute(SRC_ATTR)),
                jpgs.stream().map(extractAttribute(SRC_ATTR)));
        return images.collect(Collectors.toList());
    }

    @Override
    protected List<String> getLinks() {
        Elements links = document.select(LINK_HREF);
        return links.stream()
                .map(extractAttribute(HREF_ATTR))
                .collect(Collectors.toList());
    }

    private Function<Element, String> extractAttribute(String hrefAttr) {
        return element -> element.attr(hrefAttr);
    }

}
