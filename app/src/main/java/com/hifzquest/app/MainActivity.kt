package com.hifzquest.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.*
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var pendingRequest: PermissionRequest? = null
    private val micRequest = 501

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setContentView(web)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = true
        web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                    } else {
                        pendingRequest = request
                        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micRequest)
                    }
                } else request.deny()
            }
        }
        web.webViewClient = WebViewClient()
        web.loadUrl("file:///android_asset/index.html")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == micRequest) {
            val req = pendingRequest
            pendingRequest = null
            if (results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED)
                req?.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
            else req?.deny()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
