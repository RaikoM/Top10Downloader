package com.example.raiko.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by raiko on 28.02.17.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textValue = "";


        try {
            // Starting the xml parser and initializing required functions
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            // Creates a factory for pull parser objects, i.e constantly creates new pull parser objects via factory
            XmlPullParser xpp = factory.newPullParser();


            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Setting the parser to get a tag name <> (can return null)
                String tagName = xpp.getName();
                switch (eventType) {
                    // waiting for parser to reach a tag, which it will at some point (maybe not at start).. If != Entry tag then keep parsing
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        // If parser is inside Entry (one of top 10 apps info) then setting inEntry to true and creating a new instance of FeedEntry
                        if ("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        // Reads the text value inside current event, if no text is present returns empty String
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for " + tagName);

                        // Look for specific tags inside entry and send the text inside those tags to new instance of FeedEntry created before (currentRecord)
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)){
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releasedate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)){
                                currentRecord.setImgURL(textValue);
                            }
                        }
                        break;
                    default:
                        // Nothing else to do
                }
                // Goes on to the next event type until another event happens.. .Next() sees START_TAG (<), TEXT, END_TAG (/>), END_DOCUMENT
                eventType = xpp.next();
            }
            for (FeedEntry app : applications) {
                Log.d(TAG, "**************************");
                Log.d(TAG, app.toString());
            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
