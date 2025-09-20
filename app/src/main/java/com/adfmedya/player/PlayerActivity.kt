package com.adfmedya.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adfmedya.player.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerActivity : AppCompatActivity() {
    private lateinit var b: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(b.root)

        title = intent.getStringExtra("name") ?: "ADF Medya"
        val url = intent.getStringExtra("url") ?: return

        player = ExoPlayer.Builder(this).build().also { exo ->
            b.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
