package com.example.ecommerce;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphHistory extends AppCompatActivity {

    com.github.mikephil.charting.charts.LineChart lineChart;
    PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_history);
        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);



        //removing hole
        pieChart.setDrawHoleEnabled(false);
        getOrderByMonth();

    }

    private void getOrderByMonth() {

        final Map<String,Integer> OrderPerMonth = new HashMap<>(); //year-month --> quantity

        DatabaseReference mSoldProducts = FirebaseDatabase.getInstance().getReference().child("sold");
        mSoldProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dSnapshot : snapshot.getChildren()) {
                    String date =  dSnapshot.child("date").getValue().toString();
                    String year = date.substring(0,4);

                    try {
                        OrderPerMonth.put(date.substring(0,7),
                                OrderPerMonth.get(date.substring(0,7))+Integer.parseInt(dSnapshot.child("quantity").getValue().toString()));
                    }catch (Exception e){
                        OrderPerMonth.put(date.substring(0,7),
                                Integer.parseInt(dSnapshot.child("quantity").getValue().toString()));
                    }
                    initPieChart(OrderPerMonth);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }

    private void initPieChart(Map<String, Integer> orderPerMonth) {

        pieChart.clear();
        int dec = 0;
        int nov = 0;
        int oct = 0;
        int sep = 0;
        int aug = 0;
        int jul = 0;
        int jun = 0;
        int mar = 0;
        int apr = 0;
        int feb = 0;
        int jan = 0;
        int may =0 ;
        for (Map.Entry<String, Integer> x: orderPerMonth.entrySet()) {
            if(x.getKey().substring(0,4).equals("2021")){
                String month = x.getKey().substring(5,7);
                if (month.equals("01")) jan = x.getValue();
                if (month.equals("02")) feb = x.getValue();
                if (month.equals("03")) mar = x.getValue();
                if (month.equals("04")) apr = x.getValue();
                if (month.equals("05")) may = x.getValue();
                if (month.equals("06")) jun = x.getValue();
                if (month.equals("07")) jul = x.getValue();
                if (month.equals("08")) aug = x.getValue();
                if (month.equals("09")) sep = x.getValue();
                if (month.equals("10")) oct = x.getValue();
                if (month.equals("11")) nov = x.getValue();
                if (month.equals("12")) dec = x.getValue();
            }
        }
        PieData pieData;
        List<PieEntry> pieEntryList = new ArrayList<>();
        pieEntryList.add(new PieEntry(jan,"Jan"));
        pieEntryList.add(new PieEntry(feb,"Feb"));
        pieEntryList.add(new PieEntry(mar,"Mar"));
        pieEntryList.add(new PieEntry(apr,"Apr"));
        pieEntryList.add(new PieEntry(may,"May"));
        pieEntryList.add(new PieEntry(jun,"Jun"));
        pieEntryList.add(new PieEntry(jul,"Jul"));
        pieEntryList.add(new PieEntry(aug,"Aug"));
        pieEntryList.add(new PieEntry(sep,"Sep"));
        pieEntryList.add(new PieEntry(oct,"Oct"));
        pieEntryList.add(new PieEntry(nov,"Nov"));
        pieEntryList.add(new PieEntry(dec,"Dec"));

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Month");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieData = new PieData(pieDataSet);
        pieChart.animateY(3000);
        pieChart.setCenterTextColor(Color.RED);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}