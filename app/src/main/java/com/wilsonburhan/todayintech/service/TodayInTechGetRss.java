package com.wilsonburhan.todayintech.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;

import com.wilsonburhan.todayintech.TodayInTechContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Wilson on 9/20/2014.
 */
public class TodayInTechGetRss {
    private static URL mUrl = null;
    private static final String ATOM = "http://www.w3.org/2005/Atom";
    private static final String PURL = "http://purl.org/rss/1.0/modules/content/";
    private static final String DC = "http://purl.org/dc/elements/1.1/";
    private static final String ITUNES = "http://www.itunes.com/dtds/podcast-1.0.dtd";
    private static final String MEDIA = "http://search.yahoo.com/mrss/";
    private static final String FEED = "feed";
    private static final String RSS = "rss";
    private static String mNamespace;


    public static void get(ContentResolver contentResolver, List<TodayInTechContract.Source> sourceList) {
        try {
            getXML(contentResolver, sourceList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getXML(ContentResolver resolver, List<TodayInTechContract.Source> sourceList) throws IOException, SAXException {
        for (TodayInTechContract.Source source : sourceList) {
            if (source.isActive() == false)
                continue;
            mUrl = new URL(source.getUrl());
            final ValueList items = new ValueList();
            InputStream stream = (InputStream) mUrl.getContent();

            String rootName = getRootNode(stream);

            if (rootName.equals(FEED))
                atomParser(stream, items, resolver);
            else if (rootName.equals(RSS))
                rssParser(stream, items, resolver);
        }
    }

    private static void rssParser(InputStream stream, final ValueList items, ContentResolver resolver) throws IOException, SAXException{
        RootElement root = new RootElement("rss");
        android.sax.Element channel = root.getChild("channel");
        final String[] publisher = {""};
        // End of each entry add the entire row, and create a new row.
        channel.getChild("item").setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                items.currentRow.put(TodayInTechContract.COLUMN_PUBLISHER, publisher[0]);
                items.rows.add(items.currentRow);
                items.currentRow = new ContentValues();
            }
        });

        // Add the article publisher
        channel.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                //TODO Title for RSS
                if(body.equals("Tech"))
                    publisher[0] = "Mashable";
                else
                    publisher[0] =  body;
            }
        });

        // Add the article title
        channel.getChild("item").getChild("title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_TITLE, body);
            }
        });

        // Get the link to the article
        channel.getChild("item").getChild("link").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_ARTICLE_LINK, body);
            }
        });

        // Add the article ID
        channel.getChild("item").getChild("guid").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_ARTICLE_ID, body);
            }

        });

        // Add the Published Date
        channel.getChild("item").getChild("pubDate").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_PUBLISHED_DATE, DateUtils.parseDate(body));
            }
        });

        // Add the Author Name
        channel.getChild("item").getChild("author").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_AUTHOR_NAME, body);
            }
        });

        channel.getChild("item").getChild(DC, "creator").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_AUTHOR_NAME, body);
            }
        });

        if (mNamespace.equals(PURL)) {
            // Add the Content
            channel.getChild("item").getChild(PURL, "encoded").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    org.jsoup.nodes.Document doc = Jsoup.parse(body);
                    Element png = doc.select("img").first();
                    if (png != null) {
                        items.currentRow.put(TodayInTechContract.COLUMN_PICTURE, png.absUrl("src"));
                        items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body.replaceAll("<img.+?>", ""));
                    } else {
                        items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body);
                    }
                }

            });

            channel.getChild("item").getChild("description").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    items.currentRow.put(TodayInTechContract.COLUMN_SUMMARY, body);
                }
            });
        }

        else {
            // Add the Content
            channel.getChild("item").getChild("description").setEndTextElementListener(new EndTextElementListener() {
                @Override
                public void end(String body) {
                    org.jsoup.nodes.Document doc = Jsoup.parse(body);
                    Element png = doc.select("img").first();
                    if (png != null) {
                        items.currentRow.put(TodayInTechContract.COLUMN_PICTURE, png.absUrl("src"));
                        items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body.replaceAll("<img.+?>", ""));
                    } else {
                        items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body);
                    }
                }
            });
        }

        channel.getChild("item").getChild(MEDIA, "thumbnail").setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                items.currentRow.put(TodayInTechContract.COLUMN_PICTURE, attributes.getValue("url"));
            }
        });

       	/*
       	 * Bulk insert just loops through the list one at a time, not that fast,
       	 * could use an InsertHelper to speed it up, and direct DB write vs via the
       	 * content resolver.
       	 */
        Xml.parse(mUrl.openStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        resolver.bulkInsert(TodayInTechContract.RSS_FEED_URI, items.getRows());
    }

    private static void atomParser(InputStream stream, final ValueList items, ContentResolver resolver) throws IOException, SAXException{
        RootElement root = new RootElement(ATOM, "feed");
        final String[] publisher = {""};
        // End of each entry add the entire row, and create a new row.
        root.getChild(ATOM, "entry").setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                items.currentRow.put(TodayInTechContract.COLUMN_PUBLISHER, publisher[0]);
                items.rows.add(items.currentRow);
                items.currentRow = new ContentValues();
            }
        });

        root.getChild(ATOM, "title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                //TODO Title for ATOM
                if (body.equals("The Verge -  Home Posts"))
                    publisher[0] = "The Verge";
                else
                    publisher[0] = body;
            }
        });

        // Add the article title
        root.getChild(ATOM, "entry").getChild(ATOM, "title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_TITLE, body);
            }
        });

        // Get the link to the article
        root.getChild(ATOM, "entry").getChild(ATOM, "link").setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                items.currentRow.put(TodayInTechContract.COLUMN_ARTICLE_LINK, attributes.getValue("href"));
            }
        });

        // Add the article ID
        root.getChild(ATOM, "entry").getChild(ATOM, "id").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_ARTICLE_ID, body);
            }
        });

        // Add the Published Date
        root.getChild(ATOM, "entry").getChild(ATOM, "published").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_PUBLISHED_DATE, DateUtils.parseDate(body));
            }

        });

        // Add the Updated Date
        root.getChild(ATOM, "entry").getChild(ATOM, "updated").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_UPDATED_DATE, DateUtils.parseDate(body));
            }

        });

        // Add the Author Name
        root.getChild(ATOM, "entry").getChild(ATOM, "author").getChild(ATOM, "name").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_AUTHOR_NAME, body);
            }

        });

        // Add the Author Name
        root.getChild(ATOM, "entry").getChild(ATOM, "author").getChild(ATOM, "uri").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                items.currentRow.put(TodayInTechContract.COLUMN_AUTHOR_URI, body);
            }

        });

        // Add the Content
        root.getChild(ATOM, "entry").getChild(ATOM, "content").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                org.jsoup.nodes.Document doc = Jsoup.parse(body);
                Element png = doc.select("img").first();
                if (png != null) {
                    items.currentRow.put(TodayInTechContract.COLUMN_PICTURE, png.absUrl("src"));
                    items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body.replaceAll("<img.+?>", ""));
                }
                else {
                    items.currentRow.put(TodayInTechContract.COLUMN_CONTENT, body);
                }
            }

        });

       	/*
       	 * Bulk insert just loops through the list one at a time, not that fast,
       	 * could use an InsertHelper to speed it up, and direct DB write vs via the
       	 * content resolver.
       	 */
        Xml.parse(mUrl.openStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        resolver.bulkInsert(TodayInTechContract.RSS_FEED_URI, items.getRows());
    }

    private static String getRootNode(InputStream stream) {
        Document doc = null;
        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            fact.setValidating(false);
            fact.setNamespaceAware(true);
            DocumentBuilder builder = fact.newDocumentBuilder();
            doc = builder.parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
        Node node = doc.getDocumentElement();
        mNamespace = node.getAttributes().item(0).getNodeValue();
        return node.getNodeName();
    }
}
