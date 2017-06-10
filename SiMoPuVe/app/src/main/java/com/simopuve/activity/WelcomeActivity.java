package com.simopuve.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.simopuve.R;
import com.simopuve.RequestManager;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVHeader;
import com.simopuve.model.PDVRow;
import com.simopuve.model.PDVSurvey;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class WelcomeActivity extends AppCompatActivity {
    final String TAG = WelcomeActivity.class.getSimpleName();

    private RealmList<PDVHeader> headers;
    private PDVHeaderViewAdapter adapter;
    private Realm realm;
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
                Intent intent = new Intent(WelcomeActivity.this,HeaderCreatorActivity.class);
                startActivity(intent);
            }
        });
        headers = new RealmList<PDVHeader>();
        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();
        RealmResults<PDVHeader> headersResult = realm.where(PDVHeader.class).findAll();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        Calendar cal2 = Calendar.getInstance();
        if(!headersResult.isEmpty()){
            RealmList rows = headers;
            rows.addAll(headersResult);
            headers = rows;
            int i = 0;
            for (PDVHeader row : headersResult) {
                cal2.setTime(row.getSurveyDate());
                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)){

                }else{
                    headers.remove(row);
                    RealmResults<PDVRow> all = realm.where(PDVRow.class).equalTo("rowNumber",i).findAll();
                    realm.beginTransaction();
                    all.deleteAllFromRealm();
                    row.deleteFromRealm();
                    realm.commitTransaction();
                }
                i++;
            }

        }else{

            Toast.makeText(this, "No hay encuestas activas en este momento, crea una tocando el boton flotante", Toast.LENGTH_LONG).show();
        }
        View recyclerView = findViewById(R.id.pdvrow_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();
        RealmResults<PDVHeader> headersResult = realm.where(PDVHeader.class).findAll();
        if(!headersResult.isEmpty()){
            headers.clear();
            RealmList rows = headers;
            rows.addAll(headersResult);
            headers = rows;
        }
        adapter.notifyDataSetChanged();

    }

    public void shouldNotifyDatasetChanged(){
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new PDVHeaderViewAdapter(headers);
        recyclerView.setAdapter(adapter);
    }

    public class PDVHeaderViewAdapter extends RecyclerView.Adapter<WelcomeActivity.PDVHeaderViewAdapter.ViewHolder> {

        private List<PDVHeader> mValues;

        public PDVHeaderViewAdapter(List<PDVHeader> items) {
            mValues = items;
        }

        public void setmValues(List<PDVHeader> mValues) {
            this.mValues = mValues;
        }


        @Override
        public WelcomeActivity.PDVHeaderViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pdvrow_list_content, parent, false);
            return new WelcomeActivity.PDVHeaderViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final WelcomeActivity.PDVHeaderViewAdapter.ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText("Punto de venta: " +mValues.get(position).getPointOfSaleName());
            //holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, PDVRowListActivity.class);
                    intent.putExtra("pointOfSale", holder.mItem.getPointOfSaleName());
                    intent.putExtra("position", position);
                    context.startActivity(intent);

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
            public PDVHeader mItem;

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
