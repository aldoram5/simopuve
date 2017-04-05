package com.simopuve.activity;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.simopuve.R;
import com.simopuve.model.PDVRow;

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
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private PDVRow row;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PDVRowDetailFragment() {
    }


    public static PDVRowDetailFragment newInstance() {

        Bundle args = new Bundle();

        PDVRowDetailFragment fragment = new PDVRowDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PDVRowDetailFragment newInstance(PDVRow row) {

        Bundle args = new Bundle();

        PDVRowDetailFragment fragment = new PDVRowDetailFragment();
        args.putSerializable("row",row);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("row")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            row = (PDVRow) getArguments().getSerializable("row");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(row.getDeviceModel());
            }
        }else{
            row = new PDVRow();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pdvrow_detail, container, false);

        TextView title = (TextView)rootView.findViewById(R.id.title);
        title.setText(getArguments().containsKey("row") ? "Modificar registro":"Nuevo Registro" );
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
        purchasedCardCheckBox.setSelected(row.isBoughtCard());
        purchasedAccessoryCheckBox.setSelected(row.isBoughtAccessory());
        purchasedChipCheckBox.setSelected(row.isBoughtChip());
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
            //TODO save the row
        }
    }
}
