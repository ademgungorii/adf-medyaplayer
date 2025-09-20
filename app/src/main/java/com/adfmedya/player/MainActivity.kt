package com.adfmedya.player

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.adfmedya.player.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private var channels: List<Channel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnFetch.setOnClickListener { fetchPlaylist() }
        b.lvChannels.setOnItemClickListener { _, _, position, _ ->
            val ch = channels[position]
            val i = Intent(this, PlayerActivity::class.java)
            i.putExtra("name", ch.name)
            i.putExtra("url", ch.url)
            startActivity(i)
        }
    }

    private fun fetchPlaylist() {
        val server = b.etServer.text.toString().trim().removeSuffix("/")
        val user = b.etUser.text.toString().trim()
        val pass = b.etPass.text.toString().trim()
        if (server.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        val m3uUrl = "$server/get.php?username=$user&password=$pass&type=m3u&output=m3u8"

        Thread {
            try {
                val req = Request.Builder().url(m3uUrl).build()
                val res = client.newCall(req).execute()
                if (!res.isSuccessful) throw Exception("HTTP ${res.code}")
                val body = res.body?.string() ?: throw Exception("Boş yanıt")
                channels = M3uParser.parse(body)
                runOnUiThread {
                    if (channels.isEmpty()) Toast.makeText(this, "Kanal bulunamadı", Toast.LENGTH_SHORT).show()
                    val names = channels.map { it.name }
                    b.lvChannels.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show() }
            }
        }.start()
    }
}
