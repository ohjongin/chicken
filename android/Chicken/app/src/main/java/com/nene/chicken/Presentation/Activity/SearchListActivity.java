package com.nene.chicken.Presentation.Activity;

import android.app.Activity;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nene.chicken.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchListActivity extends Activity {

    private String query;
    private SearchListAdapter searchListAdapter;
    private ListView searchListView;
    private ArrayList<SearchResultInfo> searchResultInfoList = new ArrayList<SearchResultInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        Intent intent = getIntent();
        query = intent.getStringExtra("query");

        setLayout();

    }

    private void setLayout(){
        searchListView = (ListView) findViewById(R.id.search_listview);
        searchListAdapter = new SearchListAdapter(this, R.layout.list_item_search, searchResultInfoList);
        searchListView.setAdapter(searchListAdapter);
        searchListView.setOnItemClickListener(searchItemClickListener);
        searchListView.setDividerHeight(0);

        searchPosition();
    }

    private AdapterView.OnItemClickListener searchItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            int mapx = searchResultInfoList.get(position).getMapx();
            int mapy = searchResultInfoList.get(position).getMapy();

            Toast.makeText(SearchListActivity.this,"this x : " + mapx, Toast.LENGTH_SHORT);

//            Intent intent = new Intent(SettingFAQActivity.this, SettingFAQ01Activity.class);
//            intent.putExtra("title",title);
//            intent.putExtra("contents",contents);
//
//            startActivity(intent);

        }
    };

    private void searchPosition(){
        searchResultInfoList.clear();

        try {
            query = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "query=" + query + "&display=10&start=1&sort=random";

        Communicator.getHttp(2, url, new Handler() {
            public void handleMessage(Message msg) {

                searchResultInfoList.clear();
                String jsonString = msg.getData().getString("jsonString");
                Log.d("jsonString",jsonString);

                try {
                    JSONObject dataObject = new JSONObject(jsonString);

                    String itemList = dataObject.getString("items");
                    JSONArray itemArray = new JSONArray(itemList);
                    JSONObject tempObject;

                    for (int i = 0; i < itemArray.length(); i++) {
                        String tempString = itemArray.getString(i);
                        tempObject = new JSONObject(tempString);

                        String title = tempObject.getString("title");
                        String roadAddress = tempObject.getString("roadAddress");
                        int mapx = tempObject.getInt("mapx");
                        int mapy = tempObject.getInt("mapy");

                        searchResultInfoList.add(new SearchResultInfo(title, roadAddress, mapx, mapy));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                searchListAdapter.notifyDataSetChanged();
            }
        });
    }

}
