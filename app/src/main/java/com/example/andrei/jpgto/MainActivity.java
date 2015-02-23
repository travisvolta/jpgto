package com.example.andrei.jpgto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {
    public String tag = "jpgto";
    public String webViewTitle = "";
    String errorRegExMismatching = "input can contain 1-30 alphanumeric+space, non latin letters not allowed";
    String regEx = "[A-Za-z0-9 ]{1,30}"; //allows only alphanumeric and spaces from 1 to 30 chars

    EditText editText;
    Button showMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText = (EditText)findViewById(R.id.editText);
        showMe = (Button)findViewById(R.id.button);

        showMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String subject = editText.getText().toString();
                webViewTitle = subject;
                subject = removeSpaces(subject);
                try {
                    validateInputByRegex(webViewTitle);
                } catch (MyException e) {
                    e.printStackTrace();
                    return;
                }
                String url = ("http://" + subject + ".jpg.to");
                new HttpAsyncTask().execute(url);

            }
        });
    }

    private void validateInputByRegex(String subject) throws MyException{
        Pattern patern = Pattern.compile(regEx);
        Matcher matcher = patern.matcher(subject);
        if(matcher.find()){

        }else {
            throw new MyException();
        }


    }


    private String removeSpaces(String subject) {
        subject = subject.replaceAll("\\s", "");
        return subject;
    }


    protected String sendGet(String url)  {

        InputStream inputStream;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            //make HttpRequest to given url
            HttpResponse httpResponse = httpClient.execute(new HttpGet(url));

            if (httpResponse.getStatusLine().getStatusCode()!=200){
                Toast.makeText(getApplicationContext(), "No internet connection. Check your connection and try again.", Toast.LENGTH_SHORT).show();
                return "";
            }
            //receive response in inputStream
            inputStream = httpResponse.getEntity().getContent();

            //convert inputStream to string
            if (inputStream != null){
                result = convertInputStreamToString(inputStream);
                result = getResultReturnURLOfImage(result);
            }else {
                result = "Didn't work";

        }
            } catch (Exception e){
            Log.i(tag, e.getLocalizedMessage());
        }

        return result;
    }

    private String getResultReturnURLOfImage(String result) {
        Pattern pattern = Pattern.compile("src=\"(.*?)\" "); //regex to cut all except ulr of image
                                // to be transmitted to webview activity via intent
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        else {
            result = "Didn't work";
        }
        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws  IOException{
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferReader.readLine()) != null) {
            result += line;
            result += "\n";
        }
        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String s = "";

            s = sendGet(params[0]);

            return s;

        }

        @Override
        protected void onPostExecute(String result) {

            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra("imageUrl", result);
            intent.putExtra("title", webViewTitle);
            Log.i(tag, result);
            startActivity(intent);

        }

    }

    private class MyException extends Exception {
        public MyException(){
            super();
            Toast.makeText(getApplicationContext(), errorRegExMismatching, Toast.LENGTH_SHORT).show();
        }
    }

}

