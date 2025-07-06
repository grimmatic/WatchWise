package com.vakifbank.WatchWise.utils

object GenreUtils {
    private val genreMap = mapOf(
        28 to "Aksiyon",
        12 to "Macera",
        16 to "Animasyon",
        35 to "Komedi",
        80 to "Suç",
        99 to "Belgesel",
        18 to "Drama",
        10751 to "Aile",
        14 to "Fantastik",
        36 to "Tarih",
        27 to "Korku",
        10402 to "Müzik",
        9648 to "Gizem",
        10749 to "Romantik",
        878 to "Bilim Kurgu",
        10770 to "TV Film",
        53 to "Gerilim",
        10752 to "Savaş",
        37 to "Western"
    )

    fun getGenreNames(genreIds: List<Int>?): String {
        if (genreIds.isNullOrEmpty()) return "Bilinmiyor"

        return genreIds.mapNotNull { genreMap[it] }
            .take(3) // İlk 3 genre'ı al
            .joinToString(", ")
            .ifEmpty { "Bilinmiyor" }
    }
}