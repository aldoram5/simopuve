package com.simopuve.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.simopuve.R;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVHeader;

import java.util.Date;

import io.realm.Realm;

public class HeaderCreatorActivity extends AppCompatActivity {
    private PDVHeader header;

    private EditText pointOfSaleEditText;
    private EditText addressEditText;
    private EditText locationEditText;
    private EditText surveyDateEditText;
    private EditText peopleAMEditText;
    private EditText peoplePMEditText;
    private EditText peopleWithBagsEditText;
    private EditText peopleDeclinedEditText;
    private String id;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_creator);
        id = getIntent().getStringExtra("id");

        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();
        if(id != null && !id.isEmpty()){

            getSupportActionBar().setTitle("Editar Información");
            Realm.init(SIMOPUVEApplication.getAppContext());
            header = Realm.getDefaultInstance().where(PDVHeader.class).equalTo("pointOfSaleName",id).findFirst();
        }else{
            header = new PDVHeader();
        }
        pointOfSaleEditText = (EditText) findViewById(R.id.sale_point);
        addressEditText = (EditText) findViewById(R.id.address);
        locationEditText = (EditText) findViewById(R.id.location);
        surveyDateEditText = (EditText) findViewById(R.id.survey_date);
        peopleAMEditText = (EditText) findViewById(R.id.people_am);
        peoplePMEditText = (EditText) findViewById(R.id.people_pm);
        peopleDeclinedEditText = (EditText) findViewById(R.id.people_declined);
        peopleWithBagsEditText = (EditText) findViewById(R.id.people_bags);

        if(id != null && !id.isEmpty()){
            pointOfSaleEditText.setEnabled(false);
        }

        pointOfSaleEditText.setText(header.getPointOfSaleName());
        addressEditText.setText(header.getAddress());
        locationEditText.setText(header.getComuna());
        peopleAMEditText.setText(String.valueOf(header.getNumberOfPeopleAM()));
        peoplePMEditText.setText(String.valueOf(header.getNumberOfPeoplePM()));
        peopleDeclinedEditText.setText(String.valueOf(header.getNumberOfPeopleDidNotAnswer()));
        peopleWithBagsEditText.setText(String.valueOf(header.getPeopleWithBags()));

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFieldsAndSaveHeader();
            }
        });
    }

    public void addPersonAM(View view){
        String peopleAM = peopleAMEditText.getText().toString();
        peopleAMEditText.setText(String.valueOf(Integer.parseInt(peopleAM)+1));
    }
    public void addPersonPM(View view){
        String peoplePM = peoplePMEditText.getText().toString();
        peoplePMEditText.setText(String.valueOf(Integer.parseInt(peoplePM)+1));
    }
    public void addPersonWithBag(View view){
        String peopleWithBags = peopleWithBagsEditText.getText().toString();
        peopleWithBagsEditText.setText(String.valueOf(Integer.parseInt(peopleWithBags)+1));
    }
    public void addPersonDeclined(View view){
        String peopleDeclined = peopleDeclinedEditText.getText().toString();
        peopleDeclinedEditText.setText(String.valueOf(Integer.parseInt(peopleDeclined)+1));
    }

    void validateFieldsAndSaveHeader(){

        boolean cancel = false;
        View focusView = null;

        String pointOfSale = pointOfSaleEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String peopleAM = peopleAMEditText.getText().toString();
        String peoplePM = peoplePMEditText.getText().toString();
        String peopleDeclined = peopleDeclinedEditText.getText().toString();
        String peopleWithBags = peopleWithBagsEditText.getText().toString();
        if (TextUtils.isEmpty(pointOfSale)) {
            pointOfSaleEditText.setError(getString(R.string.error_field_required));
            focusView = pointOfSaleEditText;
            cancel = true;
        }else if (TextUtils.isEmpty(address)) {
            addressEditText.setError(getString(R.string.error_field_required));
            focusView = addressEditText;
            cancel = true;
        }else if (TextUtils.isEmpty(location)) {
            locationEditText.setError(getString(R.string.error_field_required));
            focusView = locationEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            realm.beginTransaction();
            header.setAddress(address);
            header.setComuna(location);
            header.setCompleteName(getSharedPreferences("SIMOPUVE", MODE_PRIVATE).getString("completeName","Sin Nombre"));
            header.setNumberOfPeopleAM(Integer.parseInt(peopleAM));
            header.setNumberOfPeoplePM(Integer.parseInt(peoplePM));
            header.setSurveyDate(new Date());
            header.setNumberOfPeopleDidNotAnswer(Integer.parseInt(peopleDeclined));
            header.setPeopleWithBags(Integer.parseInt(peopleWithBags));
            if (id == null || id.isEmpty()){
                header.setPointOfSaleName(pointOfSale);
                realm.copyToRealm(header);
            }

            realm.commitTransaction();
            Toast.makeText(this, "Se agrego correctamente el registro", Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}
