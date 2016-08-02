package com.example.android.books;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String BOOKS_KEY = "books";
    private static final String PROTOCOL = "https";
    private static final String HOST = "www.googleapis.com";
    private static final String PATH = "/books/v1/volumes";
    private static final String PARAM = "q=";
    private static String bookUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void searchButton(View view) throws URISyntaxException {
        TextView searchTextView = (TextView) findViewById(R.id.subject_to_search);
        String query = searchTextView.getText().toString();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isNetworkAvailable()) {
            bookUrl = getStringUrl(query);
            Log.v(LOG_TAG, "This is query " + bookUrl);
            Log.v(LOG_TAG, "There is a valid internet connection.");


            new BookAsyncTask().execute();
        } else {
            Log.v(LOG_TAG, "There is no valid internet connection.");
            Toast.makeText(this, "There is no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getStringUrl(String query) throws URISyntaxException {

        URI uri = new URI(PROTOCOL, HOST, PATH, PARAM + encodeValue(query), null);
        String request = uri.toString();
        return request;
    }

    private String encodeValue(String data) {
        String encodedValue = "";
        try {
            encodedValue = URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedValue;
    }

    public void navigateToListBook(ArrayList<Book> books) {
        Intent intent = new Intent(this, ListBookActivity.class);
        intent.putExtra(BOOKS_KEY, books);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            URL url = createUrl(bookUrl);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.v(LOG_TAG, "makeHttpRequest  error " + e);
            }
            ArrayList<Book> bookInfo = extractItemsFromJson(jsonResponse);
            return bookInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookInfo) {
            if (bookInfo == null) {
                Log.v(LOG_TAG, "onPostExecute is null");
                return;
            }
            navigateToListBook(bookInfo);
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL.", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null) {
                return jsonResponse;
            }
            HttpsURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving books JSON results", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;

        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        private ArrayList<Book> extractItemsFromJson(String bookJSON) {
            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }

            ArrayList<Book> listOfBooks = new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                int totalItems = baseJsonResponse.getInt("totalItems");
                if (totalItems == 0) {
                    return listOfBooks;
                }
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                if (itemsArray.length() > 0) {
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject currentBook = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                        String title = volumeInfo.getString("title");
                        String authors = "";
                        try {
                            JSONArray authorsList = volumeInfo.getJSONArray("authors");
                            StringBuilder authorsBuilder = new StringBuilder();
                            for (int j = 0; j < authorsList.length(); j++) {
                                authorsBuilder.append(authorsList.get(j)).append("\n");
                            }
                            authors = authorsBuilder.toString();
                        } catch (JSONException e) {
                            authors = "No data";
                        }
                        String publisher = "";
                        try {
                            publisher = volumeInfo.getString("publisher");
                        } catch (JSONException e) {
                            publisher = "No data";
                        }


                        listOfBooks.add(new Book(title, authors, publisher));
                    }
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing books JSON results", e);
            }
            return listOfBooks;
        }

    }

}

