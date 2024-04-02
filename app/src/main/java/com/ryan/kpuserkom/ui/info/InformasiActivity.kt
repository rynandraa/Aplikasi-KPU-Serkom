package com.ryan.kpuserkom.ui.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.ryan.kpu.R


class InformasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi)

        // Mengatur tampilan tombol kembali di action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inisialisasi WebView.
        val webView = findViewById<WebView>(R.id.webView)

        // Mengaktifkan JavaScript pada WebView.
        webView.settings.javaScriptEnabled = true

        // Menangani event selesai memuat halaman web.
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Menjalankan JavaScript untuk menampilkan alert pada halaman web.
                view.loadUrl("javascript:alert('Web KPU berhasil dimuat')")
            }
        }

        // Menangani event alert JavaScript di dalam WebView.
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: android.webkit.JsResult): Boolean {
                // Menampilkan pesan alert dengan Toast.
                Toast.makeText(this@InformasiActivity, message, Toast.LENGTH_LONG).show()
                result.confirm() // Mengkonfirmasi penanganan alert.
                return true
            }
        }

        // Memuat URL website KPU.
        webView.loadUrl("https://www.kpu.go.id/")
    }

    override fun onSupportNavigateUp(): Boolean {
        // Mengaktifkan fungsi tombol kembali di action bar.
        onBackPressed()
        return true
    }
}