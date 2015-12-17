package at.ac.tuwien.sepm.musicplayer.service.retriever;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * Created by Lena Lenz.
 */
public class HtmlParser {

    private static final Logger logger = Logger.getLogger(HtmlParser.class);

    private HtmlParser() {}

    public static String parseHtmlToText(String html) {
        //return html.replaceAll("\\<.*?>","");
        String parsedHtml = Jsoup.parse(html).text();
        parsedHtml.replaceAll("^\\s+", "");
        parsedHtml.replaceAll("\\s+$", "");
        return parsedHtml;
    }

    public static String parseHtmlToLyrics(String html) {
        String parsedHtml = html;
        parsedHtml = parsedHtml.replace("<i>","");
        parsedHtml = parsedHtml.replace("</i>","");
        parsedHtml = parsedHtml.replace("<br>", "\n");
        parsedHtml = parsedHtml.replace("&amp;", "&");
        return parsedHtml;
    }

}
