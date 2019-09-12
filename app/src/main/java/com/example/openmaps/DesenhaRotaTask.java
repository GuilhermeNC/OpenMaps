package com.example.openmaps;

import android.os.AsyncTask;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;


public class DesenhaRotaTask extends AsyncTask<RoadManager, Void, Road> {
    ArrayList<GeoPoint> waypoints;
    RoadManager roadManager;

    public DesenhaRotaTask(ArrayList<GeoPoint> waypoints, RoadManager roadManager) {
        this.waypoints = waypoints;
        this.roadManager = roadManager;
    }

    @Override
    protected Road doInBackground(RoadManager... roadManagers) {
        //Cria a rota com base nos pontos(latitude e longitude) recebidos
        Road road = roadManager.getRoad(waypoints);
        return road;
    }
}