package com.iudigital.radio.api

import android.util.Log
import com.iudigital.radio.model.RadioStation
import com.iudigital.radio.model.SampleRadioStations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/**
 * Integración con la API pública comunitaria Radio Browser (https://www.radio-browser.info/).
 * Permite obtener emisoras de radio reales en vivo de Colombia y del mundo en tiempo real.
 */
object RadioBrowserApi {

    private const val API_URL = "https://de1.api.radio-browser.info/json/stations/search?countrycode=CO&limit=15&order=votes&reverse=true"

    suspend fun fetchColombianStations(): List<RadioStation> = withContext(Dispatchers.IO) {
        try {
            val url = URL(API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "IUDigitalRadio/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val stationsList = mutableListOf<RadioStation>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val rawName = item.optString("name", "Emisora ${i + 1}").trim()
                    val streamUrl = item.optString("url_resolved", item.optString("url", ""))
                    val tags = item.optString("tags", "Variada")
                    val codec = item.optString("codec", "MP3").uppercase(Locale.getDefault())
                    val favicon = item.optString("favicon", "").trim()
                    val homepage = item.optString("homepage", "").trim()
                    val resolvedIconUrl = com.iudigital.radio.util.StationLogoResolver.resolve(rawName, favicon, homepage)

                    if (streamUrl.startsWith("http://") || streamUrl.startsWith("https://")) {
                        val genreClean = tags.split(",").firstOrNull()?.trim()
                            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                            ?: "Variada"

                        stationsList.add(
                            RadioStation(
                                id = item.optString("stationuuid", i.toString()),
                                name = rawName.ifEmpty { "Emisora $i" },
                                frequency = if (codec.contains("AAC")) "AAC HD" else "MP3 Live",
                                genre = genreClean,
                                streamUrl = streamUrl,
                                iconUrl = resolvedIconUrl
                            )
                        )
                    }
                }

                if (stationsList.isNotEmpty()) {
                    Log.d("RadioBrowserApi", "Se cargaron ${stationsList.size} emisoras reales desde Radio-Browser API")
                    return@withContext stationsList
                }
            }
        } catch (e: Exception) {
            Log.e("RadioBrowserApi", "Error al consultar Radio-Browser API: ${e.localizedMessage}")
        }

        // Fallback a las emisoras locales de respaldo en caso de desconexión
        return@withContext SampleRadioStations.defaultStations
    }
}
