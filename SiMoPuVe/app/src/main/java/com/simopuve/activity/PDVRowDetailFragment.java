package com.simopuve.activity;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simopuve.R;
import com.simopuve.activity.dummy.DummyContent;
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
     * The dummy content this fragment is presenting.
     */
    private PDVRow mItem;

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
            mItem = (PDVRow) getArguments().getSerializable("row");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getDeviceModel());
            }
        }else{
            mItem = new PDVRow();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pdvrow_detail, container, false);

        // Show the dummy content as text in a TextView.
        /*
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.pdvrow_detail)).setText(mItem.getDeviceBrand());
        }*/

        return rootView;
    }
}
