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

        // Add more routes as needed

        RouteAdapter adapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(adapter);
    }
}
