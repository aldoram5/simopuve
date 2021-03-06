package com.simopuve.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.simopuve.R;

import com.simopuve.RequestManager;
import com.simopuve.SIMOPUVEApplication;
import com.simopuve.model.PDVHeader;
import com.simopuve.model.PDVRow;
import com.simopuve.model.PDVSurvey;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
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

    private static final long LOCATION_REFRESH_TIME = 12000;//mins
    private static final float LOCATION_REFRESH_DISTANCE = 2;//meters

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

    private EditText peopleAMEditText;
    private EditText peoplePMEditText;
    private EditText peopleWithBagsEditText;
    private EditText peopleDeclinedEditText;

    private boolean notFromRealmFlag = false;

    //Websocket Monitor
    private WebSocketClient mWebSocketClient;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            sendMessage(location);
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    };
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdvrow_list);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        connectWebSocket();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getIntent().getStringExtra("pointOfSale"));
        survey = new PDVSurvey();
        Realm.init(SIMOPUVEApplication.getAppContext());
        realm = Realm.getDefaultInstance();

        PDVHeader first = realm.where(PDVHeader.class).equalTo("pointOfSaleName",getIntent().getStringExtra("pointOfSale")).findFirst();
        if(first != null){
            survey.setHeader(first);
        }else{
            notFromRealmFlag = true;
        }
        position = getIntent().getIntExtra("position", 0);
        RealmResults<PDVRow> all = realm.where(PDVRow.class).equalTo("rowNumber",position).findAll();
        if(!all.isEmpty()){
            RealmList rows = survey.getRows();
            rows.addAll(all);
            survey.setRows(rows);

        }else{

            Toast.makeText(this, "Esta encuesta se encuentra vacía, usa el menu en la parte superior derecha para agregar registros", Toast.LENGTH_LONG).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                new AlertDialog.Builder(PDVRowListActivity.this)
                        .setTitle("Enviar encuesta")
                        .setMessage("¿Esta seguro que desea enviar la encuesta actual?")
                        .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                // do the acknowledged action, beware, this is run on UI thread

                                PDVHeader header = notFromRealmFlag ? survey.getHeader() : realm.copyFromRealm(survey.getHeader());
                                RealmList<PDVRow> list = new RealmList<>();
                                for (int i = 0; i < survey.getRows().size(); i++) {
                                    list.add((PDVRow) realm.copyFromRealm(survey.getRows().get(i)));
                                }
                                PDVSurvey survey = new PDVSurvey();
                                survey.setHeader(header);
                                survey.setRows(list);
                                RequestManager.getInstance().uploadPDV(survey,getSharedPreferences("SIMOPUVE", MODE_PRIVATE).getString("completeName","Sin Nombre"), new RequestManager.JSONObjectCallbackListener() {
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
                            }
                        })
                        .create()
                        .show();
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
            peopleAMEditText = (EditText) findViewById(R.id.people_am);
            peoplePMEditText = (EditText) findViewById(R.id.people_pm);
            peopleDeclinedEditText = (EditText) findViewById(R.id.people_declined);
            peopleWithBagsEditText = (EditText) findViewById(R.id.people_bags);
            peopleAMEditText.setText(String.valueOf(survey.getHeader().getNumberOfPeopleAM()));
            peoplePMEditText.setText(String.valueOf(survey.getHeader().getNumberOfPeoplePM()));
            peopleDeclinedEditText.setText(String.valueOf(survey.getHeader().getNumberOfPeopleDidNotAnswer()));
            peopleWithBagsEditText.setText(String.valueOf(survey.getHeader().getPeopleWithBags()));
            peopleAMEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    realm.beginTransaction();
                    if(peopleAMEditText.getText().toString().isEmpty()){
                        survey.getHeader().setNumberOfPeopleAM(0);
                    }else{
                        survey.getHeader().setNumberOfPeopleAM(Integer.parseInt(peopleAMEditText.getText().toString()));
                    }
                    realm.commitTransaction();
                }
            });
            peoplePMEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    realm.beginTransaction();
                    if(peoplePMEditText.getText().toString().isEmpty()){
                        survey.getHeader().setNumberOfPeoplePM(0);
                    }else{
                        survey.getHeader().setNumberOfPeoplePM(Integer.parseInt(peoplePMEditText.getText().toString()));
                    }
                    realm.commitTransaction();
                }
            });

            peopleDeclinedEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    realm.beginTransaction();
                    if(peopleDeclinedEditText.getText().toString().isEmpty()){
                        survey.getHeader().setNumberOfPeopleDidNotAnswer(0);
                    }else{
                        survey.getHeader().setNumberOfPeopleDidNotAnswer(Integer.parseInt(peopleDeclinedEditText.getText().toString()));
                    }
                    realm.commitTransaction();
                }
            });
            peopleWithBagsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    realm.beginTransaction();
                    if(peopleWithBagsEditText.getText().toString().isEmpty()){
                        survey.getHeader().setPeopleWithBags(0);
                    }else{
                        survey.getHeader().setPeopleWithBags(Integer.parseInt(peopleWithBagsEditText.getText().toString()));
                    }
                    realm.commitTransaction();
                }
            });
        }
    }

    public void addPersonAM(View view){
        realm.beginTransaction();
        survey.getHeader().setNumberOfPeopleAM(survey.getHeader().getNumberOfPeopleAM()+1);
        realm.commitTransaction();
        String peopleAM = peopleAMEditText.getText().toString();
        peopleAMEditText.setText(String.valueOf(Integer.parseInt(peopleAM)+1));
    }
    public void addPersonPM(View view){
        realm.beginTransaction();
        survey.getHeader().setNumberOfPeoplePM(survey.getHeader().getNumberOfPeoplePM()+1);
        realm.commitTransaction();
        String peoplePM = peoplePMEditText.getText().toString();
        peoplePMEditText.setText(String.valueOf(Integer.parseInt(peoplePM)+1));
    }
    public void addPersonWithBag(View view){
        realm.beginTransaction();
        survey.getHeader().setPeopleWithBags(survey.getHeader().getPeopleWithBags()+1);
        realm.commitTransaction();
        String peopleWithBags = peopleWithBagsEditText.getText().toString();
        peopleWithBagsEditText.setText(String.valueOf(Integer.parseInt(peopleWithBags)+1));
    }
    public void addPersonDeclined(View view){
        realm.beginTransaction();
        survey.getHeader().setNumberOfPeopleDidNotAnswer(survey.getHeader().getNumberOfPeopleDidNotAnswer()+1);
        realm.commitTransaction();
        String peopleDeclined = peopleDeclinedEditText.getText().toString();
        peopleDeclinedEditText.setText(String.valueOf(Integer.parseInt(peopleDeclined)+1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(realm != null){
        RealmResults<PDVRow> all = realm.where(PDVRow.class).equalTo("rowNumber",position).findAll();
        if(!all.isEmpty()){
            survey.getRows().clear();
            RealmList rows = survey.getRows();
            rows.addAll(all);
            survey.setRows(rows);

        }
        adapter.notifyDataSetChanged();
        }

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
                    PDVRowDetailFragment fragment = PDVRowDetailFragment.newInstance(position);
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

    public void removeFragment(Fragment fragment){
        if(fragment instanceof PDVRowDetailFragment){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            shouldNotifyDatasetChanged();
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new PDVRowViewAdapter(survey.getRows());
        recyclerView.setAdapter(adapter);
    }

    public void shouldNotifyDatasetChanged(){
        survey.getRows().clear();
        RealmResults<PDVRow> all = realm.where(PDVRow.class).equalTo("rowNumber",position).findAll();
        if(!all.isEmpty()){
            RealmList rows = survey.getRows();
            rows.addAll(all);
            survey.setRows(rows);

        }
        adapter.notifyDataSetChanged();
    }

    //ViewAdapter
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
                        PDVRowDetailFragment fragment = PDVRowDetailFragment.newInstance(holder.mItem,position,pos);
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


    //WebSocket monitor

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://drivechile.dynu.net/simopuve/monitor");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {

            }

            @Override
            public void onClose(int i, String s, boolean b) {
            }

            @Override
            public void onError(Exception e) {
                Log.wtf(TAG,"Error: " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(Location location) {
        Log.d(TAG,"Location: " + location.getLongitude() + " " + location.getLatitude());
        if(mWebSocketClient!= null && mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN)
        mWebSocketClient.send(getSharedPreferences("SIMOPUVE", MODE_PRIVATE).getString("completeName","Sin Nombre") + "|" + survey.getHeader().getPointOfSaleName() + "|"
                +location.getLatitude()+"|"+location.getLongitude() + "| " );
    }
}
