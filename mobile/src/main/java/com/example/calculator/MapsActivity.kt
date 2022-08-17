package com.example.calculator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.calculator.databinding.ActivityMapsBinding
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var msgNumber = 1
    private lateinit var position: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intentFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Add a marker in Sydney and move the camera
        position = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(position).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
0
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val data = p1?.getStringExtra("message")
            println("Mensaje recibido N°${msgNumber++}")
            val location = JSONObject(data)
            val newPosition = LatLng(location["latitude"] as Double, location["longitude"] as Double)
            if(position != newPosition){
                position = newPosition
                mMap.addMarker(MarkerOptions().position(position).title(location["date"] as String?))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                Toast.makeText(this@MapsActivity, "Ubicación actualizada", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MapsActivity, "Permaneces en la misma ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}