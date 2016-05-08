package com.geaden.hackernewsreader.backend.boilerpipe;

import com.geaden.hackernewsreader.backend.domain.Content;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;


/**
 * Service that uses <a href="https://github.com/kohlschutter/boilerpipe">boilerpipe</a>
 * to extract content of the article.
 *
 * @author Gennady Denisov
 */
public class BoilerpipeContentExtractionService {

    public Content content(String url) {
        try {
            final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
            final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();

            String title = doc.getTitle();
            String content = ArticleExtractor.INSTANCE.getText(doc);

            final BoilerpipeExtractor extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
            final ImageExtractor ie = ImageExtractor.INSTANCE;

            List<Image> images = ie.process(new URL(url), extractor);

            Collections.sort(images);
            String image = null;
            if (!images.isEmpty()) {
                image = images.get(0).getSrc();
            }

            return new Content(title, content, image);
        } catch (Exception e) {
            return null;
        }
    }
}
