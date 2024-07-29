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
        routeList.add(new Route("自行選擇路線", "Lv.???", "???", "???", "???%", R.drawable.questionmap,"自行輸入","自行輸入"));
        routeList.add(new Route("三和路上義大世界", "Lv. 1（簡易）", "75", "4.9", "7%（緩坡）", R.drawable.yi_da_world_on_sanhe_road, "高雄市,大社區, 嘉誠路22號","高雄市, 大樹區, 學城路一段10號"));
        routeList.add(new Route("香格里拉休閒農場", "Lv. 2（簡易）", "161", "5.8", "9%（緩坡）", R.drawable.shangrila_leisure_farm, "宜蘭縣, 冬山鄉, 廣興路168號","宜蘭縣, 冬山鄉, 梅山路168號"));
        routeList.add(new Route("楊銅路上乳姑山", "Lv. 2（簡易）", "147", "2.6", "9%（緩坡）", R.drawable.rugu_mountain_on_yangtong_road, "桃園市, 龍潭區, 楊銅路二段712-1號","桃園市, 龍潭區, 中豐路上林段418號"));
        routeList.add(new Route("南深路", "Lv. 2（簡易）", "192", "3.5", "10%（普通坡）", R.drawable.nanshen_road, "新北市, 深坑區, 北深路一段","台北市, 南港區, 南深路52號"));
        routeList.add(new Route("中科松鼠坡(來回)", "Lv. 2（簡易）", "133", "4.5", "9%（緩坡）", R.drawable.zhongke_squirrel_slope, "台中市, 西屯區, 東大路一段997號","台中市, 西屯區, 西平南巷22號"));
        routeList.add(new Route("澄清湖(繞圈)", "Lv. 1（簡易）", "12", "7.1", "7%（緩坡）", R.drawable.clarification_lake, "自行輸入","自行輸入"));
        routeList.add(new Route("虎頭山環保公園", "Lv. 3（簡易）", "114", "3.2", "13%（普通坡）", R.drawable.hutoushan_environmental_protection_park, "桃園市, 桃園區, 中山東路149號","桃園市, 桃園區, 成功路三段"));
        routeList.add(new Route("望高寮", "Lv. 3（簡易）", "184", "3.8", "11%（普通坡）", R.drawable.wanggaoliao, "台中市, 南屯區, 春安路109號","台中市, 南屯區, 中台路601號"));
        // 可直接增加更多路線資訊，照片放main/res/drawable/底下

        RouteAdapter adapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(adapter);
    }
}
