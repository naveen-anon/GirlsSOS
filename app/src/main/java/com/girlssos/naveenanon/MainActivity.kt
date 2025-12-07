package com.girlssos.naveenanon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val emergencyNumbers = listOf(
        "9876543210",
        "9123456780"
    )

    private val primaryCallNumber = "9876543210"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sosBtn: Button = findViewById(R.id.btnSos)

        sosBtn.setOnClickListener {
            sendSOS()
        }
    }

    private fun sendSOS() {
        val client = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            sendOnlyMessage()
            return
        }

        client.lastLocation.addOnSuccessListener { location: Location? ->
            val link = if (location != null)
                "https://maps.google.com/?q=${location.latitude},${location.longitude}"
            else "Location unavailable"

            val msg = "SOS! I am in danger. My location: $link"

            val sms = SmsManager.getDefault()
            emergencyNumbers.forEach {
                sms.sendTextMessage(it, null, msg, null, null)
            }

            callPrimary()
            Toast.makeText(this, "SOS Sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendOnlyMessage() {
        val sms = SmsManager.getDefault()
        emergencyNumbers.forEach {
            sms.sendTextMessage(it, null,
                "SOS! I am in danger. Location not available.",
                null, null)
        }
        callPrimary()
    }

    private fun callPrimary() {
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:$primaryCallNumber")
        startActivity(i)
    }
}
