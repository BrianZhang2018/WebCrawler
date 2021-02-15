package src.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/**
 * Created by brianzhang on 2/14/21.
 */
public class WebCrawler {

    private HashSet<String> links;
    private List<List<String>> articles;

    public WebCrawler() {
        links = new HashSet<>();
        articles = new ArrayList<>();
    }

    //Find all URLs that start with "https://www.dealmoon.cn/up-to-" and add them to the HashSet
    public void getPageLinks(String URL) {
        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements otherLinks = document.select("a[href^=\"https://www.dealmoon.cn/up-to-\"]");

                for (Element page : otherLinks) {
                    if (links.add(URL)) {
                        //Remove the comment from the line below if you want to see it running on your editor
                        System.out.println(URL);
                    }
                    if(links.size()> 100) return;
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    //Connect to each link saved in the article and find all the articles in the page
    public void getArticles() {
        links.forEach(x -> {
            Document document;
            try {
                document = Jsoup.connect(x).get();
                Elements articleLinks = document.select("a[href^=\"https://www.dealmoon.cn/exec/\"]");
                for (Element article : articleLinks) {
                    //Only retrieve the titles of the articles that contain Java 8
                    System.out.println(article.text());
                    if (article.text().matches("^.*?(空军一号|air force|NIKE|Nike).*$")) {
                        //Remove the comment from the line below if you want to see it running on your editor,
                        //or wait for the File at the end of the execution
                        //System.out.println(article.attr("abs:href"));

                        ArrayList<String> temporary = new ArrayList<>();
                        temporary.add(article.text()); //The title of the article
                        temporary.add(article.attr("abs:href")); //The URL of the article
                        articles.add(temporary);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void writeToFile(String filename) {
        FileWriter writer;
        try {
            writer = new FileWriter(filename);
            articles.forEach(a -> {
                try {
                    String temp = "- Title: " + a.get(0) + " (link: " + a.get(1) + ")\n";
                    //display to console
                    System.out.println(temp);
                    //save to file
                    writer.write(temp);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            });
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        WebCrawler bwc = new WebCrawler();
        bwc.getPageLinks("https://www.dealmoon.cn/sports-outdoors");
        System.out.println(bwc.links.size());
        bwc.getArticles();
        bwc.writeToFile("Java 8 Articles");
    }
}
