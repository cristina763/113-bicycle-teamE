package com.example.bmicalculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChooseMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosemap);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Route> routeList = new ArrayList<>();
        routeList.add(new Route("劍南路", "Lv. 1（簡易）", 123, 2.2, "10%（普通坡）", R.drawable.jiannanlu));
        routeList.add(new Route("大古山", "Lv. 1（簡易）", 79, 2.2, "11%（普通坡）", R.drawable.dagushan));
        routeList.add(new Route("澄清湖", "Lv. 1（簡易）", 12, 7.1, "7%（緩坡）", R.drawable.chenchinhu));
        // 可直接增加更多路線資訊，照片放main/res/drawable/底下

        RouteAdapter adapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(adapter);
    }
}
