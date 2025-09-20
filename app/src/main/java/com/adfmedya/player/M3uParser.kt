package com.adfmedya.player

object M3uParser {
    // Basit M3U parser: #EXTINF satırını takip eden URL’i alır
    fun parse(text: String): List<Channel> {
        val lines = text.lines()
        val out = mutableListOf<Channel>()
        var currentName: String? = null

        for (raw in lines) {
            val line = raw.trim()
            if (line.startsWith("#EXTINF", true)) {
                // #EXTINF:-1 tvg-id="..." group-title="...", Kanal Adı
                val name = line.substringAfter(",", "")
                currentName = if (name.isNotEmpty()) name.trim() else null
            } else if (line.startsWith("http://") || line.startsWith("https://")) {
                val name = currentName ?: "Kanal"
                out.add(Channel(name, line))
                currentName = null
            }
        }
        return out
    }
}
