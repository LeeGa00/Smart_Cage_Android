package com.example.reptile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reptile.databinding.FragmentHomeBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    FragmentHomeBinding viewBinding;
    private ArrayList<TypeData> typeList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewBinding = FragmentHomeBinding.inflate(getLayoutInflater());

        initializeListData();

        VerTypeRVAdapter vRVAdapter = new VerTypeRVAdapter(getContext(), typeList);

        viewBinding.verListRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        viewBinding.verListRv.setAdapter(vRVAdapter);

        viewBinding.btnMoveRoro.setOnClickListener(view -> {
            Intent intent = new Intent (getContext(), RoroActivity.class);
            startActivity(intent);
        });
        return viewBinding.getRoot();
    }

    public void initializeListData(){
        String[] typeNameList = {"test-grp1", "test-grp2"};
        int drawInt = R.drawable.btnlizardxml;  // cage 기본 이미지
        for (String typeName: typeNameList) {
            ArrayList<ReptileData> cageDataList = new ArrayList<>();
            ArrayList<String> cageNameList = this.getCageDataByJson(typeName);
            for(String cageName: cageNameList){
                cageDataList.add(new ReptileData(cageName, drawInt));
            }
            typeList.add(new TypeData(typeName, cageDataList));
        }
    }

    ArrayList getCageDataByJson(String grpName) {

        ArrayList<String> typeList = new ArrayList<>();
        String response="";
        String mid = "";

        String queryUrl= "http://182.221.64.162:7579/Mobius/"+ grpName;
        Log.d("connectTest", "getCageDataByJson: " + queryUrl);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            URL url= new URL(queryUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-M2M-RI", "dashboard_testing");
            connection.setRequestProperty("X-M2M-Origin", "dashboard_testing");
            connection.setRequestProperty("Content-Type", "application/vnd.onem2m-res+json; ty=9");

            int responseCode = connection.getResponseCode();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine);
            }

            bufferedReader.close();

            response = stringBuffer.toString();
            System.out.println(response);

            JSONObject jsonObject = new JSONObject(response).getJSONObject("m2m:grp");
            mid = jsonObject.optString("mid");

            String[] mid_list = mid.split(","); //쉼표를 기준으로 문자열 나누기

            Log.d("cageLength", "getCageDataByJson: " + mid_list.length);
            for (int i = 0; i < mid_list.length; i++){
                if (i == 0)
                    mid_list[i] = mid_list[i].substring(10, mid_list[i].length() - 1);
                else if (i == mid_list.length - 1)
                    mid_list[i] = mid_list[i].substring(9, mid_list[i].length() - 2);
                else
                    mid_list[i] = mid_list[i].substring(9, mid_list[i].length() - 1);

                typeList.add(mid_list[i]);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return typeList;
    }

}