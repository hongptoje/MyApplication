package com.example.hong.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by hong on 2018-03-27.
 */

public class TransitListAdapter extends RecyclerView.Adapter<TransitListAdapter.TransitListViewHolder>{

    private ArrayList<TransitListItem> transitList = new ArrayList<>();
    private Context context;

    public TransitListAdapter(ArrayList<TransitListItem> transitList, Context context) {
        this.transitList = transitList;
        this.context = context;
    }

    @Override
    public TransitListAdapter.TransitListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_transit_item, parent, false);
        return new TransitListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransitListAdapter.TransitListViewHolder holder, int position) {
        TransitListItem transitListItem = transitList.get(position);
        holder.transitTotalTime.setText(transitListItem.getTotalTime());
        holder.transitArrivalTime.setText(transitListItem.getArrivalTime());
        holder.walkTime.setText(transitListItem.getTotalWalk());
        holder.transitFare.setText(transitListItem.getPayment());
        holder.transitStart.setText(transitListItem.getFirstStartStation());
        holder.transitEnd.setText(transitListItem.getLastEndStation());
        holder.trafficDistance.setText(transitListItem.getTotalDistance());
        holder.mapObj.setText(transitListItem.getMapObj());
        holder.jsonInfo.setText(transitListItem.getJsonInfo());
    }

    @Override
    public int getItemCount() {
        return transitList.size();
    }

    public class TransitListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView transitTotalTime;
        public TextView transitArrivalTime;
        public TextView walkTime;
        public TextView transitFare;
        public TextView transitStart;
        public TextView transitEnd;
        public TextView trafficDistance;
        public TextView mapObj;
        public TextView jsonInfo;

        public TransitListViewHolder(View itemView) {
            super(itemView);
            transitTotalTime = (TextView)itemView.findViewById(R.id.transitTotalTime);
            transitArrivalTime = (TextView)itemView.findViewById(R.id.transitArrivalTime);
            walkTime = (TextView)itemView.findViewById(R.id.walkTime);
            transitFare = (TextView)itemView.findViewById(R.id.transitFare);
            transitStart = (TextView)itemView.findViewById(R.id.transitStart);
            transitEnd = (TextView)itemView.findViewById(R.id.transitEnd);
            trafficDistance = (TextView)itemView.findViewById(R.id.trafficDistance);
            mapObj = (TextView)itemView.findViewById(R.id.mapObj);
            jsonInfo = (TextView)itemView.findViewById(R.id.jsonInfo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            int position = getAdapterPosition();
//            String mapObj = transitList.get(position).getMapObj();
//            Log.d("MapAcTransitListAdapter","mapObjInfo: "+mapObj);
//            StringTokenizer tokenizer = new StringTokenizer(mapObj, "@");
//            String mapObjNO = tokenizer.nextToken();
//            String mapObjOK = tokenizer.nextToken();
//            String fixMapObj = "0:0@"+mapObjOK;
//            Log.d("MapAcTransitListAdapter","mapObjInfo: mapObjNO: "+mapObjNO+", mapObjOK: "+mapObjOK+" ,fixMapObj: "+fixMapObj);
//            ODsayService oDsayService = ODsayService.init(context, "qZ53QAfoVn+Qbb1virh0sq2k8r0TLHSIlOqsVDTIypw");
//            oDsayService.setReadTimeout(5000);
//            oDsayService.setConnectionTimeout(5000);
//            OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
//                @Override
//                public void onSuccess(ODsayData oDsayData, API api) {
//                    if (api == API.LOAD_LANE){
//                        String JSonresult = oDsayData.getJson().toString();
//                        Log.d("MapAcTransitListAdapter","ODsayJSonresult: LOAD_LANE: "+JSonresult);
//                        try {
//                            JSONObject jsonObject = new JSONObject(JSonresult);
//                            JSONObject result = jsonObject.getJSONObject("result");
//                            JSONArray lane = result.getJSONArray("lane");
//                            for (int i=0; i<lane.length(); i++){
//                                JSONObject jsonLane = lane.getJSONObject(i);
//                                JSONArray section = jsonLane.getJSONArray("section");
//                                for (int j=0; j<section.length(); j++){
//                                    JSONObject jsonSection = section.getJSONObject(j);
//                                    JSONArray graphPos = jsonSection.getJSONArray("graphPos");
//                                    for (int k=0; k<graphPos.length(); k++){
//                                        JSONObject jsonGraphPos = graphPos.getJSONObject(k);
//                                        String x = jsonGraphPos.getString("x");
//                                        String y = jsonGraphPos.getString("y");
//                                        Log.d("MapAcTransitListAdapter","xInfo: "+x);
//                                        Log.d("MapAcTransitListAdapter","yInfo: "+y);
//                                    }
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onError(int i, String s, API api) {
//                    if (api == API.LOAD_LANE){}
//                }
//            };
//            oDsayService.requestLoadLane(fixMapObj, onResultCallbackListener);
        }
    }
}
