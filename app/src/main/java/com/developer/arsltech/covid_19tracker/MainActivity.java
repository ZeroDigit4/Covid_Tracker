package com.developer.arsltech.covid_19tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths,tvAffectedCountries;
    ScrollView scrollView;
    PieChart pieChart;
    LottieAnimationView lottieAnimationView;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
        }


        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvAffectedCountries = findViewById(R.id.tvAffectedCountries);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);
        lottieAnimationView = findViewById(R.id.animation);
        linearLayout = findViewById(R.id.label);
        linearLayout.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(29,233,182));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipeRefreshLayout.setProgressViewOffset(false, -30,-5);
        fetchData();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pieChart.clearChart();
            linearLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            fetchData();
            swipeRefreshLayout.setRefreshing(false);
        });

    }

    private void fetchData() {

        String url  = "https://corona.lmao.ninja/v3/covid-19/all";
            lottieAnimationView.playAnimation();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        tvCases.setText(jsonObject.getString("cases"));
                        tvRecovered.setText(jsonObject.getString("recovered"));
                        tvCritical.setText(jsonObject.getString("critical"));
                        tvActive.setText(jsonObject.getString("active"));
                        tvTodayCases.setText(jsonObject.getString("todayCases"));
                        tvTotalDeaths.setText(jsonObject.getString("deaths"));
                        tvTodayDeaths.setText(jsonObject.getString("todayDeaths"));
                        tvAffectedCountries.setText(jsonObject.getString("affectedCountries"));

                        pieChart.addPieSlice(new PieModel("Cases",Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
                        pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                        pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                        pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
                        pieChart.startAnimation();
                        linearLayout.setVisibility(View.VISIBLE);
                        lottieAnimationView.cancelAnimation();
                        lottieAnimationView.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);




                    } catch (JSONException e) {
                        e.printStackTrace();
                        lottieAnimationView.cancelAnimation();
                        lottieAnimationView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                    }


                }, error -> {
                    lottieAnimationView.cancelAnimation();
                    lottieAnimationView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }




    public void goTrackCountries(View view) {

        startActivity(new Intent(getApplicationContext(),AffectedCountries.class));

    }
}
