//選擇路線清單的RecyclerView相關
package com.example.bmicalculator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routeList;
    private Context context;

    public RouteAdapter(List<Route> routeList, Context context) {
        this.routeList = routeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.routeImage.setImageResource(route.getImageResId());
        holder.routeName.setText(route.getName());
        holder.routeDetails.setText("難度: " + route.getDifficulty() + "\n爬升: " + route.getElevation() + " 公尺" + "\n距離: " + route.getDistance() + " 公里" +  "\n坡度: " + route.getSlope());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OutdoorActivity.class);
            // 如果需要可以在這裡傳遞一些資料給 OutdoorActivity
            intent.putExtra("routeName", route.getName()); // ex.傳遞路線名稱
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        ImageView routeImage;
        TextView routeName;
        TextView routeDetails;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            routeImage = itemView.findViewById(R.id.routeImage);
            routeName = itemView.findViewById(R.id.routeName);
            routeDetails = itemView.findViewById(R.id.routeDetails);
        }
    }
}
