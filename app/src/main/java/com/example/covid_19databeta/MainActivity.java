package com.example.covid_19databeta;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    int deaths = 0;
    String lastUpadate = "";
    int inspections = 0;
    int positives = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onlickShowNumbers(View view){
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Data from the file: " + "\n" + "Last Update: " +  lastUpadate + "\n" + "Number of deaths: " + deaths + "\n" + "Number of inspections: " + inspections + "\n" + "Number of positives: " + positives);
    }

    public void onlickAnalyze(View view){
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        double percentOfPositive = ((double)positives/inspections) * 100;
        textView3.setText("Percentage of Positive: " + (float)Math.round(percentOfPositive * 100) / 100 + "%");
    }


    public void onclickFunction(View view){
        jsonParseRequest();
    }

    public void onclickLoad(View view){

        //Reference : https://abhiandroid.com/database/internal-storage

        try{
            FileInputStream fileInputStream  = openFileInput("data.txt");
            int read = -1;
            StringBuffer buffer = new StringBuffer();
            while((read = fileInputStream.read()) != -1){
                buffer.append((char)read);
            }
            //Thank god they created English part
            //The Japanese part is stored with weird characters
            Log.i("Code: ", buffer.toString());

            String data = buffer.toString();
            JSONObject jsonObject = new JSONObject(data);
            deaths = jsonObject.getInt("ndeaths");
            lastUpadate = jsonObject.getString("lastUpdate");
            inspections = jsonObject.getInt("ninspections");
            positives = jsonObject.getInt("npatients");
            //Log.i("File Json parse", Integer.toString(deaths))
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jsonParseRequest(){
        final TextView textView = (TextView) findViewById(R.id.textView);
        String url = "https://www.stopcovid19.jp/data/covid19japan.json";
        RequestQueue queue = Volley.newRequestQueue(this);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Input the jsonObject to String called data
                    String data = response.toString();

                    //Create jsonObject and input the string data inside, so now the string data is converted to jsonObject?
                    JSONObject jsonObject = new JSONObject(data);
                    //Use json data to extract the data using the key such as "ndeaths"
                    int ndeaths = jsonObject.getInt("ndeaths");
                    int ninspections = jsonObject.getInt("ninspections");
                    int npatients = jsonObject.getInt("npatients");
                    String lastUpdate = jsonObject.getString("lastUpdate");

                    //***********BIG ISSUE***** here, the textView has to be converted to String ot be able to display!!
                    textView.setText("Latest Covid-19 Data by (厚生労働省)" + "\n" + "Last Update: " + lastUpdate + "\n" + "Number of deaths: " + Integer.toString(ndeaths) + "\n" + "Number of Inspection: " + Integer.toString(ninspections) + "\n" + "Number of Positives: " + Integer.toString(npatients));

                    //Figure out a way to write the data to a file, or the journey will not begin
                    String File_Name = "data.txt";
                    FileOutputStream fileobject = openFileOutput(File_Name, Context.MODE_PRIVATE);
                    //Realize that it is stored in bytes !!
                    byte[] byteArray = data.getBytes();
                    fileobject.write(byteArray);
                    fileobject.close();

                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);
    }

    

}

//Json data sorted by dates
//number of positives by dates https://data.corona.go.jp/converted-json/covid19japan-npatients.json
//Person that requires to be hospitalized https://data.corona.go.jp/converted-json/covid19japan-ncures.json
//Death https://data.corona.go.jp/converted-json/covid19japan-ndeaths.json