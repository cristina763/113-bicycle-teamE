package com.example.bmicalculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class UserGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        List<Integer> pages = new ArrayList<>();
        pages.add(R.layout.page_one);
        pages.add(R.layout.page_two);
        pages.add(R.layout.page_three);
        pages.add(R.layout.page_four);
        pages.add(R.layout.page_five);

        UserGuideAdapter adapter = new UserGuideAdapter(pages);
        viewPager.setAdapter(adapter);
    }
}
