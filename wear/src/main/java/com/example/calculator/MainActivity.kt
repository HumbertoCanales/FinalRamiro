package com.example.calculator

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import java.util.*
import java.util.concurrent.ExecutionException

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textV: TextView
    lateinit var counterV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textV = findViewById(R.id.text)
        counterV = findViewById(R.id.counter)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                counterV.text = "Recalculando en ${millisUntilFinished / 1000} segundos..."
            }
            override fun onFinish() {
                getLocation(this)
            }
        }
        getLocation(timer)
    }

    inner class SendMessage(private val path: String, private val msg: String) : Thread() {
        override fun run(){
            val nodeListTask = Wearable.getNodeClient(applicationContext).connectedNodes
            try {
                val nodes = Tasks.await(nodeListTask)
                for(node in nodes) {
                    val sendMessageTask = Wearable.getMessageClient(this@MainActivity)
                        .sendMessage(node.id, path, msg.toByteArray())
                    try {
                        val result = Tasks.await(sendMessageTask)
                    } catch (exception: ExecutionException){
                        // TODO
                    }
                }
            } catch (exception: ExecutionException){
                // TODO
            }
        }
    }

    fun getLocation(timer: CountDownTimer) {
        textV.text = "Obteniendo ubicación..."
        counterV.text = ""
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            textV.text = "Sin acceso a la ubicación"
            timer.start()
            return
        }
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location : Location? ->
                var msg = "Sin acceso a la ubicación"
                if (location != null) {
                    msg = "Tu ubicación es:\nLatitud: ${location?.latitude}\nLongitud: ${location?.longitude}"
                    val loc = "{latitude: ${location?.latitude}, longitude: ${location?.longitude}, date: '${Calendar.getInstance().time}'}"
                    SendMessage("/my_path", loc).start()
                }
                textV.text = msg
                Thread.sleep(1000)
                timer.start()
            }
    }
}