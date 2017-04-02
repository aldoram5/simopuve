package com.simopuve.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.simopuve.R;
import com.simopuve.RequestManager;
import com.simopuve.model.PDVHeader;
import com.simopuve.model.PDVRow;
import com.simopuve.model.PDVSurvey;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    final String TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDVHeader header = new PDVHeader("Lugar X", "Dirección prueba", "comuna X", 1, 1,2,null,"Pepe Peréz",0);
                PDVRow row = new PDVRow(1,1,"","","","","",true,false,false,"","NO APLICA",0,"","");
                PDVRow row2 = new PDVRow(2,1,"","","","","",true,false,false,"","NO APLICA",0,"","");
                List<PDVRow> list = new ArrayList<PDVRow>();
                list.add(row);
                list.add(row2);
                PDVSurvey survey = new PDVSurvey();
                survey.setHeader(header);
                survey.setRows(list);
                RequestManager.getInstance().uploadPDV(survey, new RequestManager.JSONObjectCallbackListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG,response.toString());
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        Log.d(TAG,error.toString());
                    }
                });
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
