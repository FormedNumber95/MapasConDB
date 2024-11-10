package es.icjardin.pruebamapasAketza

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.icjardin.pruebamapasAketza.databinding.ActivityMapsDemoBinding

/**
 * Activity principal para mostrar un mapa con los marcadores obtenidos desde la base de datos.
 * Implementa la interfaz [OnMapReadyCallback] para manipular el mapa una vez que esté listo.
 */
class MapasDeDB : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsDemoBinding
    lateinit var dbHelper: DatabaseHelper

    /** Lista de marcadores obtenidos de la base de datos. */
    var marcadores= mutableListOf<Marcador>()

    /**
     * Método onCreate, inicializa la actividad y configura el mapa.
     * @param savedInstanceState estado de la actividad previamente guardado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        binding = ActivityMapsDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        marcadores=dbHelper.getMarcadores()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Método llamado cuando el mapa está listo para ser manipulado.
     * Agrega los marcadores de la base de datos al mapa y configura el comportamiento de los clics en los marcadores.
     * @param googleMap instancia de [GoogleMap] lista para ser manipulada.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Descometnar esto para añadir las ciudades
        //for(ciudad in dbHelper.ciudades){
        //    dbHelper.insertPoint(ciudad)
        //}
        val puntos:List<Marcador> =dbHelper.getMarcadores()
        for(punto in puntos){
            val lugar=LatLng(punto.latitud,punto.longitud)
            mMap.addMarker(MarkerOptions().position(lugar).title(punto.titulo))
        }

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow() // Mostrar la ventana de información
            true // Return true to consume the event
        }

        val centro = LatLng(40.416775, -3.703790)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centro))

        mMap.setOnInfoWindowClickListener { marker ->
            val intent = Intent(this, MarcadorActivity::class.java) // Iniciar la actividad
            intent.putExtra("point", marker.title) // Pasar el título del marcador
            startActivity(intent) // Lanzar la actividad
        }
    }
}