package com.newthinktank.jsonparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

	static String yahooStockInfo = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22MSFT%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";

	static String stockSymbol = "";
	static String stockDaysLow = "";
	static String stockDaysHigh = "";
	static String stockChange = "";
	static String stockYearLow = "";
	static String stockYearHigh = "";
	static String stockLastTradePriceOnly = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new MyAsyncTask().execute();

	}

	private class MyAsyncTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... arg0) {
			// get background information...this is an Async task

			// get http client which will stream both upload and download
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());

			// we define we need to use POST method to grab data from provided
			// url that is Yahoo
			HttpPost httppost = new HttpPost(yahooStockInfo);

			// define the type of webservice we are using..here its JSON
			httppost.setHeader("content-type", "application/json");

			// Input stream to read data from the URL
			InputStream inputStream = null;
			// this will hold all the data we will get from the URL
			String result = null;

			try {
				// Asking a response from our webservice
				HttpResponse response = httpclient.execute(httppost);
				// create http entity, this will have all the content,headers
				// etc from WS
				HttpEntity entity = response.getEntity();
				//
				inputStream = entity.getContent();
				// Reads all the data from the input stream until the buffer is
				// full
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);

				// String builder stores all our data for us
				StringBuilder theStringBuilder = new StringBuilder();

				String line = null;

				// Read all the data from the buffer until no data is left to be
				// read
				while ((line = reader.readLine()) != null) {

					theStringBuilder.append(line + "\n");
				}
				// after reading all the data we convert it into a STRING
				result = theStringBuilder.toString();

			}

			catch (Exception e) {

				e.printStackTrace();
			}
			// closes the inputstream for us
			finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
			// holds bunch of key value pairs from JSON Source
			JSONObject jsonObject;

			try {
				// Delete cbfunc( and ); from the result
				result = result.substring(7);
				// chops off the last part of json string
				result = result.substring(0, result.length() - 2);
				// print all the result in logcat panel, Done to DEBUG android
				// applications
				// Log.v("JSONParser RESULT", result);
				// }

				// Get the root JSONObject
				jsonObject = new JSONObject(result);

				// Get the JSON object named query
				JSONObject queryJSONObject = jsonObject.getJSONObject("query");

				// Get the JSON object named results inside of the query object
				JSONObject resultsJSONObject = queryJSONObject
						.getJSONObject("results");

				// Get the JSON object named quote inside of the results object
				JSONObject quoteJSONObject = resultsJSONObject
						.getJSONObject("quote");

				// Get the JSON Strings in the quote object
				stockSymbol = quoteJSONObject.getString("symbol");
				stockDaysLow = quoteJSONObject.getString("DaysLow");
				stockDaysHigh = quoteJSONObject.getString("DaysHigh");
				stockChange = quoteJSONObject.getString("Change");
				stockYearLow = quoteJSONObject.getString("YearLow");
				stockYearHigh = quoteJSONObject.getString("YearHigh");
				stockLastTradePriceOnly = quoteJSONObject
						.getString("LastTradePriceOnly");

				// EXTRA STUFF THAT HAS NOTHING TO DO WITH THE PROGRAM

				Log.v("SYMBOL ", stockSymbol);
				Log.v("Days Low ", stockDaysLow);
				Log.v("Days High ", stockDaysHigh);
				Log.v("Change ", stockChange);

			}

			catch (JSONException e) {
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(String result) {
			// Method called after background information is done
			// This gain access so we can change the textviews

			TextView line1 = (TextView) findViewById(R.id.Line1);
			TextView line2 = (TextView) findViewById(R.id.line2);
			TextView line3 = (TextView) findViewById(R.id.line3);
			TextView line4 = (TextView) findViewById(R.id.line4);
			TextView line5 = (TextView) findViewById(R.id.line5);
			TextView line6 = (TextView) findViewById(R.id.line6);

			// Change the values for all the TextViews
			line1.setText("Stock: " + stockSymbol + " : " + stockChange);
			line2.setText("Days Low: " + stockDaysLow);
			line3.setText("Stock: " + stockDaysHigh);
			line4.setText("Year's Low: " + stockYearLow );
			line5.setText("Year's High: " + stockYearHigh);
			line6.setText("Last Trade Price: " + stockLastTradePriceOnly);
		}
	}
}
