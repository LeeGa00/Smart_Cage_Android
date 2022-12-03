package com.example.reptile;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.reptile.databinding.FragmentListBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    FragmentListBinding viewBinding;
    private String TAG = this.getClass().getSimpleName();
    //    private String TAG = CageManagementActivity.class.getSimpleName();

    //리스트뷰 변수
    String data;
    //ListView listView;
    //ListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewBinding = FragmentListBinding.inflate(getLayoutInflater());



        return inflater.inflate(R.layout.fragment_list, container, false);
    }


    String getJsonTempData() {

        String response="";
        String mid = "";
        StringBuffer result = new StringBuffer();

        String queryUrl= "http://182.221.64.162:7579/Mobius/test-grp1";
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

            String[] mid_list = mid.toString().split(","); //쉼표를 기준으로 문자열 나누기

            for (int i = 0; i < mid_list.length; i++){
                if (i == 0)
                    mid_list[i] = mid_list[i].substring(10, mid_list[i].length() - 1);
                else if (i == mid_list.length - 1)
                    mid_list[i] = mid_list[i].substring(9, mid_list[i].length() - 2);
                else
                    mid_list[i] = mid_list[i].substring(9, mid_list[i].length() - 1);

                //adapter.addItem(new CageItemList(mid_list[i]));

                //result.append(mid_list[i]);
                //result.append("\n");

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }


    /* 리스트뷰 어댑터 */
    /*
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<CageItemList> items = new ArrayList<CageItemList>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(CageItemList item) {
            items.add(item);
        }

        public void clearItem() {
            items.clear();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final CageItemList itemList = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cage_listview_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView tv_cage = (TextView) convertView.findViewById(R.id.tv_cage);


            tv_cage.setText(itemList.getCage());

            Log.d(TAG, "getView() - [ "+position+" ] "+itemList.getCage());

            return convertView;  //뷰 객체 반환
        }
    } */

}