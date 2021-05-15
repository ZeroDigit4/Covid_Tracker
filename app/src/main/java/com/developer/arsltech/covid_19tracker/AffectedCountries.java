package com.developer.arsltech.covid_19tracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AffectedCountries extends AppCompatActivity {

    EditText edtSearch;
    ListView listView;
    LottieAnimationView lottieAnimationView;

    public static List<CountryModel> countryModelsList = new ArrayList<>();
    CountryModel countryModel;
    MyCustomAdapter myCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affected_countries);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
        }

        edtSearch = findViewById(R.id.edtSearch);
        listView = findViewById(R.id.listView);
        lottieAnimationView = findViewById(R.id.animation);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Affected Countries");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fetchData();

        listView.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(getApplicationContext(),DetailActivity.class).putExtra("position",position)));


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                myCustomAdapter.getFilter().filter(s);
                myCustomAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {

        String url  = "https://corona.lmao.ninja/v3/covid-19/countries";

        lottieAnimationView.playAnimation();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String countryName = jsonObject.getString("country");
                            String cases = jsonObject.getString("cases");
                            String todayCases = jsonObject.getString("todayCases");
                            String deaths = jsonObject.getString("deaths");
                            String todayDeaths = jsonObject.getString("todayDeaths");
                            String recovered = jsonObject.getString("recovered");
                            String active = jsonObject.getString("active");
                            String critical = jsonObject.getString("critical");

                            JSONObject object = jsonObject.getJSONObject("countryInfo");
                            String flagUrl = object.getString("flag");

                            countryModel = new CountryModel(flagUrl,countryName,cases,todayCases,deaths,todayDeaths,recovered,active,critical);
                            countryModelsList.add(countryModel);


                        }

                            myCustomAdapter = new MyCustomAdapter(AffectedCountries.this,countryModelsList);
                            listView.setAdapter(myCustomAdapter);
                        lottieAnimationView.cancelAnimation();
                        lottieAnimationView.setVisibility(View.GONE);






                    } catch (JSONException e) {
                        e.printStackTrace();
                        lottieAnimationView.cancelAnimation();
                        lottieAnimationView.setVisibility(View.GONE);
                    }


                }, error -> {
                    lottieAnimationView.cancelAnimation();
                    lottieAnimationView.setVisibility(View.GONE);
                    Toast.makeText(AffectedCountries.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

}
