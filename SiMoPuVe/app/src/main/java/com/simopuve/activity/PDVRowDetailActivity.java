package com.simopuve.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.simopuve.R;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVRow;

import io.realm.Realm;

/**
 * An activity representing a single PDVRow detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PDVRowListActivity}.
 */
public class PDVRowDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdvrow_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle("Nuevo registro");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            PDVRowDetailFragment fragment;
            if(getIntent().getIntExtra("rowNumber",-1) != -1){

                toolbar.setTitle("Modificar registro");
                Realm.init(SIMOPUVEApplication.getAppContext());
                Realm.getDefaultInstance().where(PDVRow.class).findAll().get(getIntent().getIntExtra("rowNumber",0));
                fragment = PDVRowDetailFragment.newInstance(Realm.getDefaultInstance().where(PDVRow.class).findAll().get(getIntent().getIntExtra("rowNumber",0)));
            }else{
            fragment = new PDVRowDetailFragment();
            }
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pdvrow_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, PDVRowListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
