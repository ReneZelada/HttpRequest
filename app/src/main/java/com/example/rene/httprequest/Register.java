package com.example.rene.httprequest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rene on 16/05/2015.
 */
public class Register extends Activity {
    EditText eTEmail;
    EditText eTPassword;
    EditText eTusername;
    EditText eTPasswordconfirm;

    Button crear;
    String password;
    String email;
    String username;
    String comfirm;
    String url = "http://ec2-52-4-196-240.compute-1.amazonaws.com:3000/users";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eTEmail = (EditText) findViewById(R.id.Email);
        eTPassword = (EditText) findViewById(R.id.Password);
        eTusername =(EditText)findViewById(R.id.Username);
        eTPasswordconfirm=(EditText)findViewById(R.id.Cpassword);
        crear=(Button)findViewById(R.id.Crear);


               username = eTusername.getText().toString();
                email = eTEmail.getText().toString();
                password = eTPassword.getText().toString();
                comfirm=eTPasswordconfirm.getText().toString();

                if(password.equals(comfirm))
                {
                    Networking n = new Networking();
                    n.execute(url, Networking.NETWORK_STATE_REGISTER);
               }

            }

//AsyncTask good for long running tasks
  /*      class Networking extends AsyncTask {

            public static final int NETWORK_STATE_REGISTER = 1;

            @Override
            protected Object doInBackground(Object[] params) {

                getJson((String) params[0], (Integer) params[1]);
                return null;
            }
        }}
*/
    private void getJson(String url, int state) {
        //Do a HTTP POST, more secure than GET
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        boolean valid = false;

        switch (state) {
            case Networking.NETWORK_STATE_REGISTER:
                //Building key value pairs to be accessed on web
                postParameters.add(new BasicNameValuePair("username", username));
                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("password", password));
                postParameters.add(new BasicNameValuePair("password_confirmation", comfirm));

                valid = true;


                break;
            default:
                // Toast.makeText(c, "Unknown state", Toast.LENGTH_SHORT).show();

        }

        if (valid == true) {
            //Reads everything that comes from server
            BufferedReader bufferedReader = null;
            StringBuffer stringBuffer = new StringBuffer("");
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
                request.setEntity(entity);

                //Send off to server
                HttpResponse response = httpClient.execute(request);

                //Reads response and gets content
                bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String line = "";
                String LineSeparator = System.getProperty("line.separator");
                //Read back server output
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + LineSeparator);
                }

                bufferedReader.close();
            } catch (Exception e) {
                //Toast.makeText(c, "Error during networking", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            decodeResultIntoJson(stringBuffer.toString());

            //Toast.makeText(c, "Valid details", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(c, "Invalid details", Toast.LENGTH_SHORT).show();

        }
    }

    private void decodeResultIntoJson(String response) {
    /* Example from server
    {
       "success":1,
       "message":"You have been successfully registered"
    }
     */
        if (response.contains("error")) {
            try {
                JSONObject jo = new JSONObject(response);
                String error = jo.getString("error");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONObject jo = new JSONObject(response);

            String success = jo.getString("success");
            String message = jo.getString("message");
            // Toast.makeText(c, "Register successful", Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class Networking extends AsyncTask{
        public static final int NETWORK_STATE_REGISTER = 1;
        @Override
        protected Object doInBackground(Object[] params) {
            getJson((String) params[0], (Integer) params[1]);

            return null;
        }
    }
}

