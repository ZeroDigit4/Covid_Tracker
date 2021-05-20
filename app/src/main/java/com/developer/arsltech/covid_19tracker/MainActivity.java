package com.developer.arsltech.covid_19tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    TextView tvCases, tvRecovered, tvCritical, tvActive, tvTodayCases, tvTotalDeaths, tvTodayDeaths, tvAffectedCountries;
    ScrollView scrollView;
    PieChart pieChart;
    LottieAnimationView progress, lineButton, lineBg, btnBg, chartIcon, success, fail, topStats, botStats;
    GifImageView chartBorder, statsBorder;
    SwipeRefreshLayout swipeRefreshLayout;
    Button track;
    Boolean chartToggleBorder = true, statsToggleBorder = true, chartFreeze = false, statsFreeze = false;
    private long backPressedTime = 0;

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
        progress = findViewById(R.id.progress);
        lineButton = findViewById(R.id.line_button);
        lineBg = findViewById(R.id.line_button_bg);
        btnBg = findViewById(R.id.btn_bg);
        chartIcon = findViewById(R.id.chart_icon);
        success = findViewById(R.id.success);
        fail = findViewById(R.id.fail);
        topStats = findViewById(R.id.top_stats);
        botStats = findViewById(R.id.bot_stats);
        track = findViewById(R.id.btnTrack);
        chartBorder = findViewById(R.id.chart_border);
        statsBorder = findViewById(R.id.stats_border);
        chartIcon.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
        botStats.setVisibility(View.GONE);
        findViewById(R.id.top_stats_view).setVisibility(View.GONE);
        track.setClickable(false);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(29, 233, 182));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipeRefreshLayout.setProgressViewOffset(false, -30, -5);
        fetchData();
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        swipeRefreshLayout.setEnabled(false);
        new Handler().postDelayed(() ->
                swipeRefreshLayout.setEnabled(true), 7000);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            track.setClickable(false);
            findViewById(R.id.top_stats_view).setVisibility(View.GONE);
            pieChart.clearChart();
            pieChart.setVisibility(View.GONE);
            chartIcon.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            fetchData();
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
            new Handler().postDelayed(() ->
                    swipeRefreshLayout.setEnabled(true), 7000);

        });
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (scrollView.getChildAt(0).getBottom()
                            <= (scrollView.getHeight() + scrollView.getScrollY())) {
                        botStats.setVisibility(View.VISIBLE);
                    }
                });
    }
    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            Toast toast = Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            assert toastView != null;
            toastView.setBackgroundResource(R.drawable.toast_bg);
            TextView toastMessage = toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.rgb(29, 233, 182));
            toast.show();

        } else {
            super.onBackPressed();
        }
    }

    private void fetchData() {
        hideTv();
        String url = "https://corona.lmao.ninja/v3/covid-19/all";
        progress.playAnimation();

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
                        pieChart.addPieSlice(new PieModel("Cases", Integer.parseInt(tvCases.getText().toString()), Color.parseColor("#FFA726")));
                        pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(tvRecovered.getText().toString()), Color.parseColor("#66BB6A")));
                        pieChart.addPieSlice(new PieModel("Deaths", Integer.parseInt(tvTotalDeaths.getText().toString()), Color.parseColor("#EF5350")));
                        pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(tvActive.getText().toString()), Color.parseColor("#29B6F6")));
                        pieChart.setVisibility(View.VISIBLE);
                        pieChart.startAnimation();
                        chartIcon.setVisibility(View.VISIBLE);
                        progress.cancelAnimation();
                        progress.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        findViewById(R.id.top_stats_view).setVisibility(View.VISIBLE);
                        animation();
                        new Handler().postDelayed(() -> {
                            track.setClickable(true);
                            toastSuccess();
                        }, 7000);
                        topStats.setClickable(true);
                        botStats.setClickable(true);
                        chartIcon.setClickable(true);

                    } catch (JSONException e) {

                        e.printStackTrace();
                        progress.cancelAnimation();
                        progress.setVisibility(View.GONE);
                        findViewById(R.id.top_stats_view).setVisibility(View.GONE);
                            Toast toast = Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT);
                            View toastView = toast.getView();

                        assert toastView != null;
                        toastView.setBackgroundResource(R.drawable.toast_bg);
                            TextView toastMessage = toast.getView().findViewById(android.R.id.message);
                            toastMessage.setTextColor(Color.rgb(29, 233, 182));
                            toast.show();

                        track.setClickable(false);
                        toastFail();
                    }


                }, error -> {
            progress.cancelAnimation();
            progress.setVisibility(View.GONE);
            findViewById(R.id.top_stats_view).setVisibility(View.GONE);
            Toast toast = Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT);
            View toastView = toast.getView();
            assert toastView != null;
            toastView.setBackgroundResource(R.drawable.toast_bg);
            TextView toastMessage = toast.getView().findViewById(android.R.id.message);
            toastMessage.setTextColor(Color.rgb(29, 233, 182));
            toast.show();
            track.setClickable(false);
            toastFail();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }


    public void goTrackCountries(View view) {
        track.setClickable(false);
        YoYo.with(Techniques.Wobble)
                .duration(500)
                .repeat(1)
                .playOn(findViewById(R.id.btnTrack));
        Handler handler = new Handler();
        lineButton.setVisibility(View.VISIBLE);
        lineButton.playAnimation();
        lineBg.setVisibility(View.VISIBLE);
        lineBg.playAnimation();
        handler.postDelayed(() -> {
            lineBg.cancelAnimation();
            lineBg.setVisibility(View.GONE);
        }, 500);
        handler.postDelayed(() -> {
            lineButton.cancelAnimation();
            lineButton.setVisibility(View.GONE);
            track.setClickable(true);
            startActivity(new Intent(getApplicationContext(), AffectedCountries.class));
        }, 2000);


    }

    public void toastFail() {
        fail.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Swing)
                .duration(1000)
                .repeat(2)
                .playOn(findViewById(R.id.fail));
        fail.playAnimation();
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.RotateOutUpRight)
                        .duration(500)
                        .playOn(findViewById(R.id.fail)), 2200);
        new Handler().postDelayed(() -> {
            fail.cancelAnimation();
            fail.setVisibility(View.GONE);
        }, 2200);

    }

    public void toastSuccess() {
        success.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Swing)
                .duration(1000)
                .repeat(2)
                .playOn(findViewById(R.id.success));
        success.playAnimation();
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.RotateOutUpRight)
                        .duration(500)
                        .playOn(findViewById(R.id.success)), 2200);
        new Handler().postDelayed(() -> {
            success.cancelAnimation();
            success.setVisibility(View.GONE);
        }, 2500);

    }

    public void buttonEffect(View view) {
        btnBg.setClickable(false);
        YoYo.with(Techniques.Wave)
                .duration(500)
                .repeat(1)
                .playOn(findViewById(R.id.btn_bg));
        YoYo.with(Techniques.Wobble)
                .duration(500)
                .repeat(1)
                .playOn(findViewById(R.id.btnTrack));
        Handler handler = new Handler();
        lineButton.setVisibility(View.VISIBLE);
        lineButton.playAnimation();
        handler.postDelayed(() -> {
            lineButton.cancelAnimation();
            lineButton.setVisibility(View.GONE);
            btnBg.setClickable(true);
        }, 2000);
    }

    public void chartBorderToggle(View view) {
        YoYo.with(Techniques.Swing)
                .duration(500)
                .playOn(findViewById(R.id.piechart));
        YoYo.with(Techniques.BounceIn)
                .duration(500)
                .playOn(findViewById(R.id.chart_icon));
        YoYo.with(Techniques.Shake)
                .duration(500)
                .playOn(findViewById(R.id.first));
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(findViewById(R.id.second)), 250);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(findViewById(R.id.third)), 500);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(findViewById(R.id.fourth)), 750);
        if (chartToggleBorder && !chartFreeze) {
            chartIcon.setSpeed(5);
            chartBorder.setVisibility(View.VISIBLE);
            chartFreeze = true;
        } else if (chartFreeze) {
            ((GifDrawable) chartBorder.getDrawable()).stop();
            chartIcon.setSpeed(1);
            chartFreeze = false;
            chartToggleBorder = false;
        } else {
            ((GifDrawable) chartBorder.getDrawable()).start();
            chartIcon.setSpeed(1);
            chartBorder.setVisibility(View.GONE);
            chartToggleBorder = true;
        }
    }

    public void topStatsBorderToggle(View view) {

        if (statsToggleBorder && !statsFreeze) {
            topStats.setSpeed(5);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .repeat(1)
                    .playOn(findViewById(R.id.global));
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.RotateInDownRight)
                            .duration(1000)
                            .playOn(findViewById(R.id.global)), 150);
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.Wobble)
                            .duration(500)
                            .repeat(1)
                            .playOn(findViewById(R.id.stats)), 500);
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.Wobble)
                            .duration(2000)
                            .playOn(findViewById(R.id.stats)), 750);
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.Shake)
                            .duration(200)
                            .playOn(findViewById(R.id.stats)), 2500);
            new Handler().postDelayed(() ->
                    YoYo.with(Techniques.Shake)
                            .duration(200)
                            .playOn(findViewById(R.id.global)), 2750);
            statsBorder.setVisibility(View.VISIBLE);
            statsFreeze = true;
        } else if (statsFreeze) {
            ((GifDrawable) statsBorder.getDrawable()).stop();
            topStats.setSpeed(1);
            statsFreeze = false;
            statsToggleBorder = false;
        } else {
            topStats.setSpeed(1);
            ((GifDrawable) statsBorder.getDrawable()).start();
            statsBorder.setVisibility(View.GONE);
            statsToggleBorder = true;
        }
    }

    public void botStatsBorderToggle(View view) {

        if (statsToggleBorder && !statsFreeze) {
            botStats.setSpeed(5);
            statsBorder.setVisibility(View.VISIBLE);
            statsFreeze = true;
        } else if (statsFreeze) {
            ((GifDrawable) statsBorder.getDrawable()).stop();
            botStats.setSpeed(1);
            statsFreeze = false;
            statsToggleBorder = false;
        } else {
            botStats.setSpeed(1);
            ((GifDrawable) statsBorder.getDrawable()).start();
            statsBorder.setVisibility(View.GONE);
            statsToggleBorder = true;
        }
    }

    public void animation() {
        hideTv();
        first();
        chartIcon.setClickable(false);
        topStats.setClickable(false);
        botStats.setClickable(false);
        YoYo.with(Techniques.FadeIn)
                .duration(500)
                .playOn(findViewById(R.id.line));
        topStats.setSpeed(10);
        YoYo.with(Techniques.RubberBand)
                .duration(500)
                .playOn(findViewById(R.id.cardViewGraph));
        new Handler().postDelayed(() -> YoYo.with(Techniques.Wobble)
                .duration(500)
                .playOn(findViewById(R.id.cardViewStats)), 350);
        new Handler().postDelayed(() ->
        {
            YoYo.with(Techniques.Wobble)
                    .duration(500)
                    .playOn(findViewById(R.id.btnTrack));
            YoYo.with(Techniques.Wave)
                    .duration(500)
                    .playOn(findViewById(R.id.btn_bg));
        }, 700);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.first).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Swing)
                    .duration(500)
                    .playOn(findViewById(R.id.piechart));
            YoYo.with(Techniques.BounceIn)
                    .duration(500)
                    .playOn(findViewById(R.id.chart_icon));
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.first));
        }, 400);
        new Handler().postDelayed(() ->
                {
                    findViewById(R.id.second).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(findViewById(R.id.second));
                }, 650);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.third).setVisibility(View.VISIBLE);
            topStats.setSpeed(15);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.third));
        }, 900);
        new Handler().postDelayed(() ->
                {
                    findViewById(R.id.fourth).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .playOn(findViewById(R.id.fourth));
                }, 1150);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.Shake)
                        .duration(500)
                        .repeat(1)
                        .playOn(findViewById(R.id.global)), 750);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.RotateInDownRight)
                        .duration(1000)
                        .playOn(findViewById(R.id.global)), 900);
        new Handler().postDelayed(() ->
        {
            YoYo.with(Techniques.Swing)
                    .duration(500)
                    .playOn(findViewById(R.id.piechart));
            YoYo.with(Techniques.Wobble)
                    .duration(500)
                    .repeat(1)
                    .playOn(findViewById(R.id.stats));
        }, 1250);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(findViewById(R.id.chart_icon)), 1350);

        new Handler().postDelayed(() ->
        {
            topStats.setSpeed(10);
            YoYo.with(Techniques.Wobble)
                    .duration(2000)
                    .playOn(findViewById(R.id.stats));
        }, 1500);
        new Handler().postDelayed(() ->
        {
            after();
            topStats.setSpeed(5);
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .playOn(findViewById(R.id.stats));
        }, 3250);
        new Handler().postDelayed(() ->
        {
            topStats.setSpeed(1);
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .playOn(findViewById(R.id.global));
        }, 3500);
        new Handler().postDelayed(() ->
        {
            topStats.cancelAnimation();
            YoYo.with(Techniques.Flash)
                    .duration(500)
                    .repeat(1)
                    .playOn(findViewById(R.id.line));
        }, 3600);
        new Handler().postDelayed(() ->
        {
            topStats.playAnimation();
            topStats.setSpeed(1);
        }, 3700);
        new Handler().postDelayed(() ->
                YoYo.with(Techniques.Flash)
                        .duration(200)
                        .playOn(findViewById(R.id.cardViewStats)), 4300);
        new Handler().postDelayed(this::showTv, 4500);

    }

    public void hideTv(){
        findViewById(R.id.first).setVisibility(View.INVISIBLE);
        findViewById(R.id.second).setVisibility(View.INVISIBLE);
        findViewById(R.id.third).setVisibility(View.INVISIBLE);
        findViewById(R.id.fourth).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbCases).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbRecovered).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbActive).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbCritical).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbTodayCases).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbTodayDeaths).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbTotalDeaths).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbAffectedCountries).setVisibility(View.INVISIBLE);
        findViewById(R.id.view1).setVisibility(View.GONE);
        findViewById(R.id.view2).setVisibility(View.GONE);
        findViewById(R.id.view3).setVisibility(View.GONE);
        findViewById(R.id.view4).setVisibility(View.GONE);
        findViewById(R.id.view5).setVisibility(View.GONE);
        findViewById(R.id.view6).setVisibility(View.GONE);
        findViewById(R.id.view7).setVisibility(View.GONE);
        findViewById(R.id.view8).setVisibility(View.GONE);
        tvCases.setVisibility(View.GONE);
        tvRecovered.setVisibility(View.GONE);
        tvCritical.setVisibility(View.GONE);
        tvActive.setVisibility(View.GONE);
        tvTodayCases.setVisibility(View.GONE);
        tvTodayDeaths.setVisibility(View.GONE);
        tvTotalDeaths.setVisibility(View.GONE);
        tvAffectedCountries.setVisibility(View.GONE);
    }
    public void showTv(){
        tvCases.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Shake)
                .duration(500)
                .playOn(findViewById(R.id.tvCases));
        new Handler().postDelayed(() ->
        {
            tvRecovered.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvRecovered));
        }, 250);
        new Handler().postDelayed(() ->
        {
            tvCritical.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvCritical));
        }, 500);
        new Handler().postDelayed(() ->
        {
            tvActive.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvActive));
        }, 750);
        new Handler().postDelayed(() ->
        {
            tvTodayCases.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvTodayCases));
        }, 1000);
        new Handler().postDelayed(() ->
        {
            tvTotalDeaths.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvTotalDeaths));
        }, 1250);
        new Handler().postDelayed(() ->
        {
            tvTodayDeaths.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvTodayDeaths));
        }, 1500);
        new Handler().postDelayed(() ->
        {
            tvAffectedCountries.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(findViewById(R.id.tvAffectedCountries));
        }, 1750);
        new Handler().postDelayed(() ->
            findViewById(R.id.lbCases).setVisibility(View.VISIBLE), 100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbRecovered).setVisibility(View.VISIBLE), 350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbCritical).setVisibility(View.VISIBLE), 600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbActive).setVisibility(View.VISIBLE), 850);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayCases).setVisibility(View.VISIBLE), 1100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTotalDeaths).setVisibility(View.VISIBLE), 1350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayDeaths).setVisibility(View.VISIBLE), 1600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbAffectedCountries).setVisibility(View.VISIBLE), 1850);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view1).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view1));
        }, 150);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view2).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view2));
        }, 400);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view3).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view3));
        }, 650);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view4).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view4));
        }, 900);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view5).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view5));
        }, 1150);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view6).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view6));
        }, 1400);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view7).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view7));
        }, 1650);
        new Handler().postDelayed(() ->
        {
            findViewById(R.id.view8).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Tada)
                    .duration(500)
                    .playOn(findViewById(R.id.view8));
        }, 1900);


    }
    public void first()
    {
        new Handler().postDelayed(() ->
                findViewById(R.id.lbCases).setVisibility(View.VISIBLE), 100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbRecovered).setVisibility(View.VISIBLE), 350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbCritical).setVisibility(View.VISIBLE), 600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbActive).setVisibility(View.VISIBLE), 850);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayCases).setVisibility(View.VISIBLE), 1100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTotalDeaths).setVisibility(View.VISIBLE), 1350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayDeaths).setVisibility(View.VISIBLE), 1600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbAffectedCountries).setVisibility(View.VISIBLE), 1850);
        new Handler().postDelayed(() ->
                findViewById(R.id.view1).setVisibility(View.VISIBLE), 150);
        new Handler().postDelayed(() ->
                findViewById(R.id.view2).setVisibility(View.VISIBLE), 400);
        new Handler().postDelayed(() ->
                findViewById(R.id.view3).setVisibility(View.VISIBLE), 650);
        new Handler().postDelayed(() ->
                findViewById(R.id.view4).setVisibility(View.VISIBLE), 900);
        new Handler().postDelayed(() ->
                findViewById(R.id.view5).setVisibility(View.VISIBLE), 1150);
        new Handler().postDelayed(() ->
                findViewById(R.id.view6).setVisibility(View.VISIBLE), 1400);
        new Handler().postDelayed(() ->
                findViewById(R.id.view7).setVisibility(View.VISIBLE), 1650);
        new Handler().postDelayed(() ->
                findViewById(R.id.view8).setVisibility(View.VISIBLE), 1900);
    }
    public void after()
    {
        new Handler().postDelayed(() ->
                findViewById(R.id.lbCases).setVisibility(View.GONE), 100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbRecovered).setVisibility(View.GONE), 350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbCritical).setVisibility(View.GONE), 600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbActive).setVisibility(View.GONE), 850);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayCases).setVisibility(View.GONE), 1100);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTotalDeaths).setVisibility(View.GONE), 1350);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbTodayDeaths).setVisibility(View.GONE), 1600);
        new Handler().postDelayed(() ->
                findViewById(R.id.lbAffectedCountries).setVisibility(View.GONE), 1850);
        new Handler().postDelayed(() ->
                findViewById(R.id.view1).setVisibility(View.GONE), 150);
        new Handler().postDelayed(() ->
                findViewById(R.id.view2).setVisibility(View.GONE), 400);
        new Handler().postDelayed(() ->
                findViewById(R.id.view3).setVisibility(View.GONE), 650);
        new Handler().postDelayed(() ->
                findViewById(R.id.view4).setVisibility(View.GONE), 900);
        new Handler().postDelayed(() ->
                findViewById(R.id.view5).setVisibility(View.GONE), 1150);
        new Handler().postDelayed(() ->
                findViewById(R.id.view6).setVisibility(View.GONE), 1400);
        new Handler().postDelayed(() ->
                findViewById(R.id.view7).setVisibility(View.GONE), 1650);
        new Handler().postDelayed(() ->
                findViewById(R.id.view8).setVisibility(View.GONE), 1900);
    }

}
