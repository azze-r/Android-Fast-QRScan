package com.azze.android.gms.samples.vision.barcodereader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azz.android.gms.samples.vision.barcodereader.R
import com.azz.android.gms.samples.vision.barcodereader.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanner = GmsBarcodeScanning.getClient(this)
        binding.readBarcode.setOnClickListener {
            launchScan(this)
        }

    }

    fun launchScan(context: Context) {

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                if (barcode != null)
                    buildDialog(barcode, context)
            }
            .addOnCanceledListener {
                // Task canceled
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "can't open this url on browser", Toast.LENGTH_LONG).show()
            }



    }

    
    private fun buildDialog(barcode: Barcode, context:Context){

        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.title))
            .setMessage(barcode.displayValue.toString())
            .setNeutralButton(resources.getString(R.string.decline)) { _, _ ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, barcode.displayValue)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                val url = barcode.displayValue
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                try {
                    startActivity(i)
                } catch (e: Exception) {
                    Toast.makeText(this, "can't open this url on browser", Toast.LENGTH_LONG).show()
                }
            }
            .show()
    }
}
