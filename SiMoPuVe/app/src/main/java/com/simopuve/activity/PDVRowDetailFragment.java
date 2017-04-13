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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simopuve.R;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVHeader;
import com.simopuve.model.PDVRow;

import io.realm.Realm;

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
    private EditText deviceBrandEditText;
    private EditText deviceModelEditText;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pdvrow_detail, container, false);

        TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setText(getArguments().containsKey("row") || row.getPersonNumber() > 0 ? "Modificar registro":"Nuevo Registro" );
        personNumberEditText  = (EditText) rootView.findViewById(R.id.person_number);
        deviceBrandEditText = (EditText) rootView.findViewById(R.id.device_brand);
        deviceModelEditText = (EditText) rootView.findViewById(R.id.device_model);
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

        personNumberEditText.setText(String.valueOf(row.getPersonNumber()));
        deviceBrandEditText.setText(row.getDeviceBrand());
        deviceModelEditText.setText(row.getDeviceModel());
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

        Button saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFieldsAndSaveRow();
            }
        });
        return rootView;
    }

    void validateFieldsAndSaveRow(){

        boolean cancel = false;
        View focusView = null;

        String personNumber = personNumberEditText.getText().toString();
        String deviceBrand = deviceBrandEditText.getText().toString();
        String deviceModel = deviceModelEditText.getText().toString();
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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
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
            row.setExpressRefillValue(Integer.parseInt(personNumber));
            row.setPersonNumber(Integer.parseInt(personNumber));
            row.setPortabilityChange(carrierChangedFromTo);
            row.setPortabilityChangeReason(carrierChangeReason);
            if (!getArguments().containsKey("row")){
                realm.copyToRealm(row);
            }

            realm.commitTransaction();
            Toast.makeText(getContext(), "Se agrego correctamente el registro", Toast.LENGTH_SHORT).show();
            if(getActivity() instanceof PDVRowListActivity){
                ((PDVRowListActivity)getActivity()).shouldNotifyDatasetChanged();
            }


        }
    }
}
