package com.birutekno.umrah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.birutekno.umrah.adapter.JadwalAiwaAdapter;
import com.birutekno.umrah.helper.AIWAResponse;
import com.birutekno.umrah.helper.UtilsApi;
import com.birutekno.umrah.model.DataJadwal;
import com.birutekno.umrah.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class JadwalActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.spinnerFilter)
    Spinner spinnerFilter;

    List<String> listPeriode = new ArrayList<String>();
    private ArrayList<DataJadwal> pojo;
    private JadwalAiwaAdapter adapterB;

    private ProgressDialog pDialog;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, JadwalActivity.class);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_jadwal;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setupToolbar(mToolbar, true);
        setTitle("Jadwal");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                try {
                    loadJSON(selectedItem);
                }catch (Exception ex){
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    loadJSON(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initViews(){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        initSpinnerPeriode();
    }

    private void loadJSON(final String periode){
        pDialog = new ProgressDialog(JadwalActivity.this);
        pDialog.setMessage("Harap tunggu...");
        pDialog.setCancelable(false);
        pDialog.show();
        Call<AIWAResponse> call = UtilsApi.getAPIService().getJSON(periode);
        call.enqueue(new Callback<AIWAResponse>() {
            @Override
            public void onResponse(Call<AIWAResponse> call, Response<AIWAResponse> response) {
                pDialog.dismiss();
                if(response.isSuccessful()){
                    AIWAResponse jsonResponse = response.body();
                    pojo = new ArrayList<>(Arrays.asList(jsonResponse.getData()));
                    adapterB = new JadwalAiwaAdapter(pojo, getBaseContext());
                    recyclerView.setAdapter(adapterB);
                }else {
                    Log.d("ERROR CODE" , String.valueOf(response.code()));
                    Log.d("ERROR BODY" , response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<AIWAResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
                pDialog.dismiss();
                loadJSON(periode);
            }
        });
    }

    public void initSpinnerPeriode() {
        listPeriode.add("1440");
        listPeriode.add("1439");

        ArrayAdapter<String> adapterC= new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_item, listPeriode);
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapterC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        title.setVisibility(View.GONE);
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                title.setVisibility(View.VISIBLE);
                adapterB.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(JadwalActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("viewpager_position", 1);
        startActivity(intent);

//        Log.d("CDA", "onBackPressed Called");
//        AlertDialog.Builder ask = new AlertDialog.Builder(JadwalActivity.this);
//        ask.setTitle("Apakah Anda Akan Keluar?");
//        ask.setMessage("Tekan tombol Ya jika anda benar benar ingin keluar dari menu ini");
//        ask.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(JadwalActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("viewpager_position", 1);
//                startActivity(intent);
//            }
//        });
//        ask.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        ask.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
