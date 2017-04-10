package com.simopuve.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.simopuve.R;

import com.simopuve.RequestManager;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVHeader;
import com.simopuve.model.PDVRow;
import com.simopuve.model.PDVSurvey;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * An activity representing a list of PDVRows. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PDVRowDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PDVRowListActivity extends AppCompatActivity {

    private String TAG = PDVRowListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Realm realm;
    private PDVRowViewAdapter adapter;
    private PDVSurvey survey;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdvrow_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Log.d(TAG,getIntent().getStringExtra("pointOfSale"));
        toolbar.setTitle(getIntent().getStringExtra("pointOfSale"));
        survey = new PDVSurvey();
        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();

        PDVHeader first = realm.where(PDVHeader.class).equalTo("pointOfSaleName",getIntent().getStringExtra("pointOfSale")).findFirst();
        if(first != null){
            survey.setHeader(first);
        }
        position = getIntent().getIntExtra("position", 0);
        Log.d(TAG,"Position of header: " + position);
        RealmResults<PDVRow> all = realm.where(PDVRow.class).equalTo("rowNumber",position).findAll();
        if(!all.isEmpty()){
            RealmList rows = survey.getRows();
            rows.addAll(all);
            survey.setRows(rows);
            Log.d(TAG,"Rows: " + survey.getRows().size());

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PDVHeader header = realm.copyFromRealm(survey.getHeader());
                RealmList<PDVRow> list = new RealmList<>();
                list.add((PDVRow) realm.copyFromRealm(survey.getRows().first()));
                for (int i = 0; i < survey.getRows().size(); i++) {
                    list.add((PDVRow) realm.copyFromRealm(survey.getRows().get(i)));
                }
                //list.add(row2);
                PDVSurvey survey = new PDVSurvey();
                survey.setHeader(header);
                survey.setRows(list);
                RequestManager.getInstance().uploadPDV(survey, new RequestManager.JSONObjectCallbackListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG,response.toString());
                        Snackbar.make(view, "Exito al mandar la encuesta", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        Log.d(TAG,error.toString());
                        Snackbar.make(view, "Error al mandar la encuesta", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.pdvrow_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.pdvrow_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.activity_main_pdv_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_menu_item:
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    //arguments.putString(PDVRowDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    PDVRowDetailFragment fragment = PDVRowDetailFragment.newInstance(position);
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.pdvrow_detail_container, fragment)
                            .commit();
                } else {
                    Context context = this;
                    Intent intent = new Intent(context, PDVRowDetailActivity.class);
                    intent.putExtra("position",position);
                    context.startActivity(intent);
                }
                return true;
            case R.id.edit_header_menu_item:
                Context context = this;

                Intent intent = new Intent(context,HeaderCreatorActivity.class);
                intent.putExtra("id",survey.getHeader().getPointOfSaleName());
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new PDVRowViewAdapter(survey.getRows());
        recyclerView.setAdapter(adapter);
    }

    public void shouldNotifyDatasetChanged(){
        adapter.notifyDataSetChanged();
    }

    public class PDVRowViewAdapter
            extends RecyclerView.Adapter<PDVRowViewAdapter.ViewHolder> {

        private List<PDVRow> mValues;

        public PDVRowViewAdapter(List<PDVRow> items) {
            mValues = items;
        }

        public void setmValues(List<PDVRow> mValues) {
            this.mValues = mValues;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pdvrow_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int pos) {
            holder.mItem = mValues.get(pos);
            holder.mContentView.setText("Persona número: " +mValues.get(pos).getPersonNumber());
            //holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        //arguments.putString(PDVRowDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        PDVRowDetailFragment fragment = PDVRowDetailFragment.newInstance(holder.mItem,position,pos);
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.pdvrow_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PDVRowDetailActivity.class);
                        intent.putExtra("rowNumber", pos);
                        intent.putExtra("position", position);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public PDVRow mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
