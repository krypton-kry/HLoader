package com.krypton.animeid.plugins

import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

const val TAG = "krypton"

// All plugin files must have 3 functions as follows
private const val BASE_URL = "https://animeidhentai.com"

data class SearchList(val title: String, val url: String, val img: String)

//TODO(krypton) : Add More if needed
data class AnimeDetails(
    var title: String = "",
    var downloadUrl: String = "",
    var subsUrl: String = "",
    var description: String = "",
    var animePage: String = "",
    var genre: List<String> = emptyList(),
    var similar: List<SearchList> = emptyList()
)

suspend fun getLatestEpisodes(): List<SearchList> {
    return withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect("$BASE_URL/year/2025/").get()
            val elem = doc.select("article.por.poster.anime")
            val searchResults = emptyList<SearchList>().toMutableList()
            for (e in elem) {
                val a = e.select("a")
                val title = a.attr("aria-label")
                val url = a.attr("href")
                val img = e.select("div > figure > img").attr("src")
                searchResults += SearchList(title, url, img)
            }
            searchResults
        } catch (e: Exception) {
            Log.w("krypton", "getEpisodes: [ERROR] $e")
            emptyList<SearchList>()
        }
    }
}

suspend fun getAnimeDetails(url: String): AnimeDetails {
    return withContext(Dispatchers.IO) {
        try {
            val detail = AnimeDetails()
            val doc = Jsoup.connect(url).get()

            // title
            detail.title = doc.select("header > h1").text()

            // extract genre
            val genres = doc.select("div.genres.mgt.df.fww.por > a")
            for (genre in genres) {
                detail.genre += genre.text()
            }

            // description
            detail.description =
                doc.selectFirst("div.mgb2.link-co.description > div > p")?.text() ?: ""
            if (detail.description.isEmpty()) {
                detail.description =
                    doc.selectFirst("div.mgb2.link-co.description > p:nth-child(1)")?.text()
                        ?: "No Description Available"
            }

            // anime page
            detail.animePage = "$BASE_URL/hentai/" + url.split("-episode")[0].split("/").last()

            // extract similar
            val similar = doc.select("div.embla__slide")
            for (s in similar) {
                val imgBase = s.select("img")
                val img = imgBase.attr("src")
                val title = imgBase.attr("alt")
                val baseUrl = s.select("a").attr("href")
                detail.similar += SearchList(title, baseUrl, img)
            }

            // extract dl
            val initUrl = doc.select("iframe")
            for (iframe in initUrl) {
                if (iframe.attr("src").contains("https://nhplayer.com")) {
                    val res = Jsoup.connect(iframe.attr("src")).get()
                    val base = res.selectFirst(".servers>ul>li")?.attr("data-id")
                    val baseUrl = "https://google.com$base".toUri()

                    val u = baseUrl.getQueryParameters("u") // actual url
                    val s = baseUrl.getQueryParameters("s") // sub url
                    //val i = baseUrl.getQueryParameters("i") // preview image

                    if (s.isNotEmpty()) {
                        detail.subsUrl = String(Base64.decode(s[0], Base64.DEFAULT), Charsets.UTF_8)
                    }
                    if (u.isNotEmpty()) {
                        detail.downloadUrl =
                            String(Base64.decode(u[0], Base64.DEFAULT), Charsets.UTF_8)
                    }
                }
            }
            detail
        } catch (e: Exception) {
            Log.w("krypton", "getAnimeDetails: $e")
            AnimeDetails()
        }
    }
}

suspend fun getAnimeList(url: String): List<SearchList> {
    return withContext(Dispatchers.IO) {
        try {
            val animeList = emptyList<SearchList>().toMutableList()
            val doc = Jsoup.connect(url).get()
            val res = doc.select("article.por.poster.anime")

            for (s in res) {
                val imgBase = s.select("img")
                val img = imgBase.attr("src")
                val title = imgBase.attr("alt")
                val baseUrl = s.select("a").attr("href")
                animeList += SearchList(title, baseUrl, img)
            }
            animeList
        } catch (e: Exception) {
            Log.i(TAG, "getAnimeList: $e")
            emptyList()
        }
    }
}

suspend fun searchAnime(query: String): List<SearchList> {
    return withContext(Dispatchers.IO) {
        try {
            var searchRes = emptyList<SearchList>().toMutableList()
            val doc = Jsoup.connect("$BASE_URL/?s=$query").get()
            val res = doc.select("article.por.poster.anime")
            for (s in res) {
                val imgBase = s.select("img")
                val img = imgBase.attr("src")
                val title = imgBase.attr("alt")
                val baseUrl = s.select("a").attr("href")
                searchRes += SearchList(title, baseUrl, img)
            }
            searchRes
        } catch (e: Exception) {
            emptyList()
        }
    }
}