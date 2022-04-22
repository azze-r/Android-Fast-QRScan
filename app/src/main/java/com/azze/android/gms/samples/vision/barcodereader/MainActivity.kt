/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azze.android.gms.samples.vision.barcodereader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import android.net.Uri
import java.lang.Exception
import androidx.appcompat.app.AlertDialog
import com.azz.android.gms.samples.vision.barcodereader.R
import com.azze.android.gms.samples.vision.barcodereader.ui.barcode.BarcodeCaptureActivity


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
class MainActivity : Activity(), View.OnClickListener {

    private var statusMessage: TextView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.read_barcode).setOnClickListener(this)

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        if (v.id == R.id.read_barcode) {
            // launch barcode activity.
            val intent = Intent(this, BarcodeCaptureActivity::class.java)
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
            //            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
            startActivityForResult(intent, RC_BARCODE_CAPTURE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    if (barcode != null) {
                        Log.i("tryhard", barcode.displayValue)
                        val builder = AlertDialog.Builder(this,R.style.MyDialogTheme)
                        builder.setTitle("Code Bar Detected")
                        builder.setMessage(barcode.displayValue)
                        // add the buttons

                        builder.setPositiveButton("Open") { _, _ ->
                            val url = barcode.displayValue
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            try {
                                startActivity(i)
                            } catch (e: Exception) {
                                Toast.makeText(this, "can't open this url on browser", Toast.LENGTH_LONG).show()
                            }
                        }

                        builder.setNeutralButton("share") { _, _ ->
                            // do something like...
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, barcode.displayValue)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(shareIntent)
                        }

                        // create and show the alert dialog
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        statusMessage?.setText(R.string.barcode_failure)
                        Log.d(TAG, "No barcode captured, intent data is null")
                    }
                } else {
                    statusMessage?.text = String.format(getString(R.string.barcode_error),
                            CommonStatusCodes.getStatusCodeString(resultCode))
                }
            }
            else{
                Toast.makeText(this, "code bar null", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        private val RC_BARCODE_CAPTURE = 9001
        private val TAG = "BarcodeMain"
    }
}
