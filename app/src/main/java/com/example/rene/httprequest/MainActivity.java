package com.example.rene.httprequest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

    EditText etResponse;
    TextView tvIsConnected;
    Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // get reference to the views
        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        button1=(Button)findViewById(R.id.button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Register.class);
                startActivity(intent);
            }
        });



        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }



        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("http://api.unicoapp.com:80/qa/restaurants");
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(url);
            getRequest.setHeader("Content-Type", "application/json");
            getRequest.setHeader("Authorization","FfsPDLI8D1o058Bdmorp8jvp9AMe962E");
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(getRequest);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            etResponse.setText(result);
        }
    }

}