package com.example.openmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSAO_REQUERIDA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissoes, PERMISSAO_REQUERIDA);
            }
        }

        //Pega o mapa adicionada no arquivo activity_main.xml
        MapView mapa = (MapView) findViewById(R.id.mapaId);
        //Fonte de imagens
        mapa.setTileSource(TileSourceFactory.MAPNIK);

        //Cria um ponto de referência com base na latitude e longitude
        GeoPoint pontoInicial = new GeoPoint(-7.082433, -41.468516);

        IMapController mapController = mapa.getController();
        //Faz zoom no mapa
        mapController.setZoom(15);
        //Centraliza o mapa no ponto de referência
        mapController.setCenter(pontoInicial);

        //Cria um marcador no mapa
        Marker startMarker = new Marker(mapa);
        startMarker.setPosition(pontoInicial);
        startMarker.setTitle("Ponto Inicial");
        //Posição do ícone
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapa.getOverlays().add(startMarker);


        GeoPoint pontoFinal = new GeoPoint(-7.078601, -41.462174);
        Marker endMarker = new Marker(mapa);
        endMarker.setPosition(pontoFinal);
        endMarker.setTitle("Ponto Final");
        //Posição do ícone
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapa.getOverlays().add(endMarker);


        //Matriz com pontos geográficos(latitudes e longitudes) por onde a rota deve ser traçada
        //Utilizando a API do Google Maps, nós só conseguimos desenhar uma rota com até 10 pontos
        //Com a API do OpemStreetMap o números de pontos é ilimitado
        double matrizPontos[][] = {
                {-7.082433, -41.468516},
                {-7.082545, -41.468478},
                {-7.082544, -41.468392},
                {-7.082351, -41.468155},
                {-7.082136, -41.467914},
                {-7.082082, -41.467858},
                {-7.081876, -41.467814},
                {-7.081705, -41.467789},
                {-7.080256, -41.467683},
                {-7.078318, -41.467548},
                {-7.077923, -41.467508},
                {-7.077820, -41.467493},
                {-7.077697, -41.467366},
                {-7.077643, -41.467301},
                {-7.077497, -41.467417},
                {-7.077331, -41.467523},
                {-7.076784, -41.467403},
                {-7.076304, -41.467351},
                {-7.076023, -41.467331},
                {-7.075864, -41.467264},
                {-7.075754, -41.467225},
                {-7.075813, -41.466974},
                {-7.076019, -41.466495},
                {-7.078601, -41.462174},};


        //Cria uma lista de pontos (GeoPoint) pela latitude e longitude
        ArrayList<GeoPoint> pontos = new ArrayList<>();
        for (double[] array : matrizPontos) {
            pontos.add(new GeoPoint(array[0], array[1]));
        }


        //Cria o objeto gerenciador de rotas
        RoadManager roadManager = new OSRMRoadManager(this);
        Road road = null;
        try {
            //Chama a classe(DesenhaRotaTask) que executa tarefas assincronas, passa os pontos de referências
            //para a classe DesenhaRotaTask traçar a rota
            road = new DesenhaRotaTask(pontos, roadManager).execute(roadManager).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Desenha a rota
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        //Adiciona a rota no mapa
        mapa.getOverlays().add(roadOverlay);
        //atualiza o mapa
        mapa.invalidate();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSAO_REQUERIDA: {
                // Se a solicitação de permissão foi cancelada o array vem vazio.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão cedida, recria a activity para carregar o mapa, só será executado uma vez
                    this.recreate();

                }

            }
        }
    }
}
