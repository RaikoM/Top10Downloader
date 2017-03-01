package com.example.raiko.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "Invalidated";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApps = (ListView) findViewById(R.id.xmlListView);

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setCheckable(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
            case R.id.mnuRefresh:
                feedCachedUrl = "Invalidated";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }



    private void  downloadUrl(String feedUrl){
        // Checks if feedUrl has changed, if not means user is on same page, no extra download of data needed
       if (!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
           Log.d(TAG, "downloadUrl: starting ASync task");
           DownloadData downloadData = new DownloadData();
           downloadData.execute(feedUrl);
           feedCachedUrl = feedUrl;
           Log.d(TAG, "downloadUrl: done");
       } else {
           Log.d(TAG, "downloadUrl: URL Not changed");
       }

    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        private static final String TAG = "DownloadData";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            // Downloads the contents of the xml and stores it in a string while in background
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: error download");
            }
            return rssFeed;
        }

        @Override
        // Takes in the result from doInBackground method
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // Log.d(TAG, "onPostExecute: parameter is " + s);
            // Parses the previously downloaded xml data
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            // Creates adapter, parameters needed context (comes later), resource is created list_item layout (looks for it in layout folder, hence R.layout not R.id), getApplications is a list of items to display
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(MainActivity.this, R.layout.list_item, parseApplications.getApplications());
            // Links the list view to the adapter
//            listApps.setAdapter(arrayAdapter);

            FeedAdapter<FeedEntry> feedAdapter = new FeedAdapter<>(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }


        // @param takes in the url string from doInBackground method
        private String downloadXML (String urlPath) {
            StringBuilder xmlPath = new StringBuilder();

            try {
                // Creating a ref to the URL path
                URL url = new URL(urlPath);

                // Opening the http connection to url, server sends back response codes
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Storing the response code
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);


//                // Getting the data from http connection as InputStream bytes
//                InputStream inputStream = connection.getInputStream();
//                // Translate inputStream data bytes into chars
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                // Read the chars from inputStreamReader, can only read chars and not inputStream byte data directly
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                /**
                 * Does the same thing as 3 lines above
                 * 1) Sets up bufferedreader to read the data from a stream
                 * 2) Initialises new InputStreamReader to convert inputStream bytes to chars for buffered reader
                 * 3) Starts the inputStream from connection URL
                 */

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                // Initialize buffer char array to 500 chars in length.
                char[] inputBuffer = new char[500];

                // loops until there is data from InputStream
                while (true) {
                    charsRead = bufferedReader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    // Holds count for number of characters read from stream
                    if (charsRead > 0) {
                        xmlPath.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }

                // Closes the buffer reader, and other data stream methods (InputStream, StreamReader, etc)
                bufferedReader.close();

                // Converts stringbuilder char sequence to string
                return xmlPath.toString();

            } catch (MalformedURLException e) {
                // Catch the http url exception (subclass of IOexception)
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                // Catch IO exceptions from inputstream and readers
                Log.e(TAG, "downloadXML: IOException reading data " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception? Need Permission? " + e.getMessage());
//                e.printStackTrace();
            }

            return null;
        }
    }
}
