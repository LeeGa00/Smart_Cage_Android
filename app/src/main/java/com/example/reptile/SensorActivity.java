package com.example.reptile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.reptile.databinding.ActivitySensorBinding;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SensorActivity extends AppCompatActivity {

    ActivitySensorBinding viewBinding;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivitySensorBinding.inflate(getLayoutInflater());

        String repName = getIntent().getStringExtra("cageName");
        viewBinding.pageTitle.setText(repName+"'s 센서");

        String[] items = {"온도/습도/조도", "온도", "습도", "조도"};

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        viewBinding.spinnerSensor.setAdapter(dataAdapter);

        viewBinding.spinnerSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // viewBinding.spinnerTest.setText(items[position]);
                // 통계 이미지 만들어서 변경하도록 해야함
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //viewBinding.spinnerTest.setText(items[0]);
            }

        });

        viewBinding.btnBack.setOnClickListener(view -> {
            finish(); // CageActivity 를 finish() 하지 않았기 때문에 이전 화면으로 돌아감
        });

        setContentView(viewBinding.getRoot());

        EditText editTem = findViewById(R.id.edit_text_temp);
        String TemStr = editTem.getText().toString(); // 수정할 온도
        int Tem = Integer.parseInt(TemStr);
        // + 수정할 조도, 습도 추가 가능

        btn_save = (Button) findViewById(R.id.savebutton);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cin생성
                String JsonMsg = makeJsonMsg(Tem);
                SendJsonToServer(JsonMsg, repName, "temperature");
                // SendJsonToServer(JsonMsg, repName, "humidity");
                // SendJsonToServer(JsonMsg, repName, "brightness");
            }
        });
    }

    private static String makeJsonMsg(int Tem){
        String retMsg = "";

        JSONStringer jsonStringer = new JSONStringer();

        try{
            retMsg = jsonStringer.object()
                    .key("m2m:cin").object()
                    .key("con").value(Tem)
                    .endObject()
                    .endObject().toString();
            System.out.println(retMsg);

            Log.d(TAG,"send string = " + retMsg);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return retMsg;
    }

    static public String SendJsonToServer(String JsonMsg, String ae_name, String sensor){
        OutputStream os = null;
        InputStream is = null;

        int responseCode;

        HttpURLConnection connection = null;

        String response="";
        String queryUrl = "http://182.221.64.162:7579/Mobius/"+ae_name+"/"+sensor + "/"+ "control";
        System.out.println("cin전송 " + queryUrl);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            URL url = new URL(queryUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-M2M-RI", "dks");
            connection.setRequestProperty("X-M2M-Origin", "dks");
            connection.setRequestProperty("Content-Type", "application/vnd.onem2m-res+json; ty=9");

            // OutputStream으로 Post데이터 넘겨주는 옵션
            connection.setDoOutput(true);

            // InputStream으로 서버 응답 받는 옵션
            connection.setDoInput(true);

            os = connection.getOutputStream();
            os.write(JsonMsg.getBytes()); //json넘겨주는 부분
            os.flush();

            responseCode = connection.getResponseCode();

            System.out.println(responseCode);


            // inputStream으로 응답
            // 요 부분 잘 안됨....
            if(responseCode==201){
                System.out.println("HTTP_OK");
                is = connection.getInputStream();
                // inputStream to String
                if(is != null)
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer stringBuffer = new StringBuffer();
                    String inputLine;

                    while ((inputLine = bufferedReader.readLine()) != null)  {
                        stringBuffer.append(inputLine);
                    }
                    bufferedReader.close();

                    response = stringBuffer.toString();
                }
                else
                    response = "Error";
                System.out.println(response);
            }
            else{
                response = "Connection_Error";
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
}