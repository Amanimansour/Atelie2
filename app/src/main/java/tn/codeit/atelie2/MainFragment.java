/**
 * @author kolombo
 *
 */
package tn.codeit.atelie2;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import tn.codeit.atelie2.tn.codeit.atelie2.webservices.UserFunctions;


public class MainFragment extends Fragment {
    public SimpleAdapter mSchedule;
    public UserFunctions uf;
    public ListView stationList;
    public TextView textProgress;
    public LinearLayout layoutProgress;
    public JSONObject data , allStations;
    public JSONArray  stationsList;
    public ArrayList<HashMap<String, String>> listItem ;

    public MainFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);
        layoutProgress = (LinearLayout) rootView.findViewById(R.id.layout_progress);
        textProgress = (TextView) rootView.findViewById(R.id.text_progress);
        stationList = (ListView) rootView.findViewById(R.id.my_list);
        AsyncCallWS task = new AsyncCallWS();
        task.execute();

        return rootView;
    }

    public void displaylist(){
        Log.d("display","list");
        //Log.d("list item" , String.valueOf(listItem));
        SimpleAdapter mSchedule = new SimpleAdapter (getActivity(), listItem, R.layout.list_item,
                new String[] { "title", "adresse"}, new int[] { R.id.title, R.id.adresse});
        stationList.setAdapter(mSchedule);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public void getBusStations(){
        Log.d("test","get bus station");
        uf=new UserFunctions();
        data = uf.getAllStations();
        //Log.d("data",String.valueOf(data));
        listItem = new ArrayList<HashMap<String, String>>();
        try {
            allStations = data.getJSONObject("data");
            stationsList =allStations.getJSONArray("nearstations");
            Log.d("result" , String.valueOf(stationsList));
            for (int i=0;i<stationsList.length();i++) {
                JSONObject station ;
                station=stationsList.getJSONObject(i);
                HashMap<String, String> map;
                map = new HashMap<String, String>();
                map.put("title", station.getString("street_name"));
                map.put("adresse", station.getString("city"));
                map.put("id", station.getString("id"));
                map.put("lat", station.getString("lat"));
                map.put("lon", station.getString("lon"));
                listItem.add(map);
                stationList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>) stationList.getItemAtPosition(position);
                        //adb.setMessage("Votre choix : "+map.get("title"));
                        Bundle data = new Bundle();
                        data.putString("id_station", map.get("id"));
                        data.putString("station", map.get("title"));
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager
                                .beginTransaction();
                        DetailFragmment fragment = new DetailFragmment();
                        fragment.setArguments(data);
                        fragmentTransaction.replace(R.id.container, fragment);
                        fragmentTransaction.addToBackStack( "tag" ).commit();
                    }
                });
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // Log.i(TAG, "doInBackground");
            try {
                getBusStations();

            } catch (Exception e) {
                Log.d("error", e.getMessage() + "-RESULT");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("done", "onPostExecute");
            displaylist();
            // HIDE THE SPINNER AFTER LOADING STATIONS
            layoutProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING STATIONS
            layoutProgress.setVisibility(View.VISIBLE);
            textProgress.setText("Chargement des stations en cours...");
            Log.d("on pre execute", "onPreExecute");

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("done", "onProgressUpdate");
        }



    }

}
