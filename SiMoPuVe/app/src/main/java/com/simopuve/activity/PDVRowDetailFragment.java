package com.simopuve.activity;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.simopuve.R;
import com.simopuve.RequestManager;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVRow;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A fragment representing a single PDVRow detail screen.
 * This fragment is either contained in a {@link PDVRowListActivity}
 * in two-pane mode (on tablets) or a {@link PDVRowDetailActivity}
 * on handsets.
 */
public class PDVRowDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public final String TAG = PDVRowDetailFragment.class.getSimpleName();

    /**
     * The content this fragment is presenting.
     */
    private PDVRow row = null;

    private EditText personNumberEditText;
    private Spinner deviceBrandEditText;
    private Spinner deviceModelEditText;
    private EditText deviceModeEditText;
    private EditText contractTypeEditText;
    private EditText additionalFeaturesEditText;
    private CheckBox purchasedCardCheckBox;
    private CheckBox purchasedAccessoryCheckBox;
    private CheckBox purchasedChipCheckBox;
    private Spinner deviceRatingSpinner;
    private Spinner planRatingSpinner;
    private EditText reloadValueEditText;
    private EditText carrierChangeReasonEditText;
    private EditText carrierChangedFromToEditText;
    private ArrayAdapter<String> brands;
    private ArrayAdapter<String> models;
    private JSONArray brandsAndDevices = null;
    private Button saveButton;
    private Button deleteButton;

    private Realm realm;

    private int position;
    private int rowNumber;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PDVRowDetailFragment() {
    }


    public static PDVRowDetailFragment newInstance(int position) {

        Bundle args = new Bundle();

        PDVRowDetailFragment fragment = new PDVRowDetailFragment();
        args.putInt("position",position);
        fragment.setArguments(args);
        return fragment;
    }

    public static PDVRowDetailFragment newInstance(PDVRow row, int position,int rowNumber) {

        Bundle args = new Bundle();

        PDVRowDetailFragment fragment = new PDVRowDetailFragment();
        fragment.row = row;
        args.putInt("position",position);
        args.putInt("rowNumber",rowNumber);
        //args.putSerializable("row",row);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();
        position = getArguments().getInt("position");

        if (getArguments().containsKey("rowNumber")) {
            //row = (PDVRow) getArguments().getSerializable("row");
            position = getArguments().getInt("position");
            rowNumber = getArguments().getInt("rowNumber");
            row = realm.where(PDVRow.class).equalTo("rowNumber",position).findAll().get(rowNumber);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(row.getDeviceModel());
            }
        }else if (row == null){
            row = new PDVRow();
            RealmResults<PDVRow> rows = realm.where(PDVRow.class).equalTo("rowNumber", position).findAll();
            row.setPersonNumber((int) (rows.size() > 0 ? rows.last().getPersonNumber() + 1 : 1));
            this.rowNumber = row.getPersonNumber();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pdvrow_detail, container, false);

        TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setText(getArguments().containsKey("rowNumber") ? "Modificar registro":"Nuevo Registro" );
        personNumberEditText  = (EditText) rootView.findViewById(R.id.person_number);
        deviceBrandEditText = (Spinner) rootView.findViewById(R.id.brand_spinner);
        deviceModelEditText = (Spinner) rootView.findViewById(R.id.model_spinner);
        deviceModeEditText = (EditText) rootView.findViewById(R.id.device_mode);
        contractTypeEditText = (EditText) rootView.findViewById(R.id.contract_type);
        additionalFeaturesEditText = (EditText) rootView.findViewById(R.id.additional_features);
        reloadValueEditText = (EditText) rootView.findViewById(R.id.reload_value);
        carrierChangeReasonEditText = (EditText) rootView.findViewById(R.id.company_change_reason);
        carrierChangedFromToEditText = (EditText) rootView.findViewById(R.id.company_change_from_to);
        purchasedAccessoryCheckBox = (CheckBox) rootView.findViewById(R.id.bought_accessory_checkbox);
        purchasedChipCheckBox = (CheckBox) rootView.findViewById(R.id.bought_chip_checkbox);
        purchasedCardCheckBox = (CheckBox) rootView.findViewById(R.id.bought_card_checkbox);
        deviceRatingSpinner = (Spinner) rootView.findViewById(R.id.rate_device_spinner);
        planRatingSpinner = (Spinner) rootView.findViewById(R.id.rate_plan_spinner);

        brands = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        models = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line,new ArrayList<String>());
        deviceBrandEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(brandsAndDevices != null && i < brandsAndDevices.length()){
                    try {
                        JSONArray modelsArray = brandsAndDevices.getJSONObject(i).getJSONArray("models");
                        int length = modelsArray.length();
                        if (length > 0) {
                            models.clear();
                            for (int j = 0; j < length; j++) { ;
                                models.add(modelsArray.getString(j));
                            }
                            models.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        deviceBrandEditText.setAdapter(brands);
        /*deviceBrandEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(brandsAndDevices != null && i < brandsAndDevices.length()){
                    try {
                        JSONArray modelsArray = brandsAndDevices.getJSONObject(i).getJSONArray("models");
                        int length = modelsArray.length();
                        if (length > 0) {
                            models.clear();
                            for (int j = 0; i < length; i++) { ;
                                models.add(modelsArray.getString(i));
                            }
                            models.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        deviceModelEditText.setAdapter(models);

        personNumberEditText.setText(String.valueOf(row.getPersonNumber()));
        deviceModeEditText.setText(row.getDeviceMode());
        contractTypeEditText.setText(row.getContractType());
        additionalFeaturesEditText.setText(row.getAdditionalCharacteristics());
        reloadValueEditText.setText(String.valueOf(row.getExpressRefillValue()));
        carrierChangedFromToEditText.setText(row.getPortabilityChange());
        carrierChangeReasonEditText.setText(row.getPortabilityChangeReason());
        purchasedCardCheckBox.setChecked(row.isBoughtCard());
        purchasedAccessoryCheckBox.setChecked(row.isBoughtAccessory());
        purchasedChipCheckBox.setChecked(row.isBoughtChip());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.rating_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceRatingSpinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
                R.array.rating_options, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planRatingSpinner.setAdapter(adapter2);
        if(!row.getDeviceRating().isEmpty()){
            int spinnerPosition = adapter.getPosition(row.getDeviceRating());
            deviceRatingSpinner.setSelection(spinnerPosition);
        }if(!row.getPlanRating().isEmpty()){
            int spinnerPosition = adapter.getPosition(row.getPlanRating());
            planRatingSpinner.setSelection(spinnerPosition);
        }
        deviceBrandEditText.setAdapter(brands);
        deviceModelEditText.setAdapter(models);
        RequestManager.getInstance().getDevicesAndBrands(new RequestManager.JSONArrayCallbackListener() {
            @Override
            public void onSuccess(JSONArray response) {
                brandsAndDevices = response;
                brands.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        brands.add(response.getJSONObject(i).getString("brandName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                brands.notifyDataSetChanged();
                if(!row.getDeviceBrand().contentEquals("")){
                    int spinnerPosition = brands.getPosition(row.getDeviceBrand());
                    if(spinnerPosition == -1 )spinnerPosition = brands.getPosition("OTROS");
                    deviceBrandEditText.setSelection(spinnerPosition);
                    if(!row.getDeviceModel().contentEquals("")){
                        JSONArray modelsArray = null;
                        try {
                            modelsArray = brandsAndDevices.getJSONObject(spinnerPosition).getJSONArray("models");
                            int length = modelsArray.length();
                            if (length > 0) {
                                models.clear();
                                for (int j = 0; j < length; j++) { ;
                                    models.add(modelsArray.getString(j));
                                }
                                models.notifyDataSetChanged();
                                spinnerPosition = models.getPosition(row.getDeviceModel());
                                if(spinnerPosition == -1 )spinnerPosition = models.getPosition("OTROS");
                                deviceModelEditText.setSelection(spinnerPosition);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(PDVRowDetailFragment.this.getContext(),"Error al traer la lista de marcas y modelos",Toast.LENGTH_LONG);
            }
        });

        saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setEnabled(false);
                deleteButton.setEnabled(false);
                validateFieldsAndSaveRow();
            }
        });
        deleteButton = (Button) rootView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteButton.setEnabled(false);
                saveButton.setEnabled(false);
                deleteOrCloseFragment();
            }
        });
        return rootView;
    }

    void deleteOrCloseFragment(){
        if (getArguments().containsKey("rowNumber")){
            realm.beginTransaction();
            PDVRow temp = realm.where(PDVRow.class).equalTo("personNumber", row.getPersonNumber()).findFirst();
            if(temp != null){
                temp.deleteFromRealm();
                int i = 1;
                for (PDVRow pdvRow : realm.where(PDVRow.class).equalTo("rowNumber", position).findAll()) {
                    pdvRow.setPersonNumber(i);
                    i++;
                }
            }
            realm.commitTransaction();
        }
        if(getActivity() instanceof PDVRowListActivity){
            PDVRowListActivity listActivity = (PDVRowListActivity) getActivity();
            listActivity.removeFragment(this);
        }else {
            getActivity().finish();
        }
    }

    void validateFieldsAndSaveRow(){

        boolean cancel = false;
        View focusView = null;

        String personNumber = personNumberEditText.getText().toString();
        String deviceBrand = deviceBrandEditText.getSelectedItem().toString();
        String deviceModel = deviceModelEditText.getSelectedItem().toString();
        String deviceMode = deviceModeEditText.getText().toString();
        String contractType = contractTypeEditText.getText().toString();
        String additionalFeatures = additionalFeaturesEditText.getText().toString();
        String realoadValue = reloadValueEditText.getText().toString();
        String carrierChangedFromTo = carrierChangedFromToEditText.getText().toString();
        String carrierChangeReason = carrierChangeReasonEditText.getText().toString();
        boolean purchasedCard =  purchasedCardCheckBox.isChecked();
        boolean purchasedAccesory = purchasedAccessoryCheckBox.isChecked();
        boolean purchasedChip  = purchasedChipCheckBox.isChecked();
        String planRating = getResources().getStringArray(R.array.rating_options)[planRatingSpinner.getSelectedItemPosition()];
        String deviceRating = getResources().getStringArray(R.array.rating_options)[deviceRatingSpinner.getSelectedItemPosition()];
        //TODO Validate here the row info
        if (TextUtils.isEmpty(personNumber)) {
            personNumberEditText.setError(getString(R.string.error_field_required));
            focusView = personNumberEditText;
            cancel = true;
        }else if (!TextUtils.isDigitsOnly(personNumber)) {
            personNumberEditText.setError(getString(R.string.error_field_number_required));
            focusView = personNumberEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt to save the data and give focus to the view that needs it
            focusView.requestFocus();
        } else {
            realm.beginTransaction();
            row.setAdditionalCharacteristics(additionalFeatures);
            row.setRowNumber(position);
            row.setBoughtAccessory(purchasedAccesory);
            row.setBoughtCard(purchasedCard);
            row.setBoughtChip(purchasedChip);
            row.setPlanRating(planRating);
            row.setContractType(contractType);
            row.setDeviceBrand(deviceBrand);
            row.setDeviceMode(deviceMode);
            row.setDeviceModel(deviceModel);
            row.setDeviceRating(deviceRating);
            row.setExpressRefillValue(Integer.parseInt(realoadValue));
            row.setPersonNumber(Integer.parseInt(personNumber));
            row.setPortabilityChange(carrierChangedFromTo);
            row.setPortabilityChangeReason(carrierChangeReason);
            if (!getArguments().containsKey("row")){
                realm.copyToRealm(row);
                getArguments().putString("row","row");
                getArguments().putInt("rowNumber",rowNumber);
            }

            realm.commitTransaction();
            Toast.makeText(getContext(), "Se agrego correctamente el registro", Toast.LENGTH_SHORT).show();
            if(getActivity() instanceof PDVRowListActivity){
                ((PDVRowListActivity)getActivity()).shouldNotifyDatasetChanged();
            }
            saveButton.setEnabled(true);
            deleteButton.setEnabled(true);


        }

    }
}
