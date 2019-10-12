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

package com.google.android.gms.samples.vision.barcodereader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.app.Activity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import java.lang.Exception


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
class MainActivity : Activity(), View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private val autoFocus: CompoundButton? = null
    private val useFlash: CompoundButton? = null
    private var statusMessage: TextView? = null
    private var barcodeValue: WebView? = null
    var flag = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusMessage = findViewById<View>(R.id.status_message) as TextView
//        barcodeValue = findViewById<View>(R.id.barcode_value) as WebView
//        barcodeValue!!.webViewClient = WebViewClient()
//        barcodeValue!!.loadUrl("https://www.google.fr")
//
//        barcodeValue!!.settings.javaScriptEnabled = true

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

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * [.RESULT_CANCELED] if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     *
     *
     *
     * You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     *
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     * @see .startActivityForResult
     *
     * @see .createPendingResult
     *
     * @see .setResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    statusMessage!!.setText(R.string.barcode_success)
                    Log.i("tryhard",barcode.displayValue)
//                    barcodeValue!!.loadUrl(barcode.displayValue)
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(barcode.displayValue))
                        startActivity(browserIntent)
                    }
                    catch (e:Exception){
                        Toast.makeText(this, "this file is not a pdf", Toast.LENGTH_LONG).show()

                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue)
                } else {
                    statusMessage!!.setText(R.string.barcode_failure)
                    Log.d(TAG, "No barcode captured, intent data is null")
                }
            } else {
                statusMessage!!.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
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
