package com.example.raiko.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: starting ASync task");
        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
        Log.d(TAG, "onCreate: done");
    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        private static final String TAG = "DownloadData";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
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
            Log.d(TAG, "onPostExecute: parameter is " + s);
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
                    bufferedReader.close();


                    // Converts stringbuilder char sequence to string
                    return xmlPath.toString();
                }
            } catch (MalformedURLException e) {
                // Catch the http url exception (subclass of IOexception)
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                // Catch IO exceptions from inputstream and readers
                Log.e(TAG, "downloadXML: IOException reading data " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception? Need Permission? " + e.getMessage());
            }
            return null;
        }
    }
}
