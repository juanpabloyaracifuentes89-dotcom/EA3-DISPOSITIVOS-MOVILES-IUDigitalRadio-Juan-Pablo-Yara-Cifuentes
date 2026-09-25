package com.iudigital.radio.model

import android.os.Parcelable
import com.iudigital.radio.util.StationLogoResolver
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos que representa una Emisora de Radio en IU Digital Radio.
 * Implementa Parcelable para ser guardado sin pérdidas en rememberSaveable.
 */
@Parcelize
data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val genre: String,
    val streamUrl: String,
    val iconUrl: String? = null,
    val isLive: Boolean = true
) : Parcelable

object SampleRadioStations {
    val defaultStations = listOf(
        RadioStation(
            id = "1",
            name = "IU Digital Stereo",
            frequency = "104.5 FM",
            genre = "Institucional & Variada",
            streamUrl = "https://stream.zeno.fm/f3wvbbqmdg8uv",
            iconUrl = StationLogoResolver.resolve("IU Digital Stereo", null, "https://www.iudigital.edu.co/")
        ),
        RadioStation(
            id = "2",
            name = "Caracol Radio Colombia",
            frequency = "100.9 FM",
            genre = "Noticias / En Vivo",
            streamUrl = "https://stream.zeno.fm/0r0xa792kwzuv",
            iconUrl = StationLogoResolver.resolve("Caracol Radio", null, "https://www.caracol.com.co/")
        ),
        RadioStation(
            id = "3",
            name = "Blu Radio",
            frequency = "89.9 FM",
            genre = "Noticias / Deportes",
            streamUrl = "https://stream.zeno.fm/2r78yhn554zuv",
            iconUrl = StationLogoResolver.resolve("Blu Radio", null, "https://www.bluradio.com/")
        ),
        RadioStation(
            id = "4",
            name = "Olímpica Stereo",
            frequency = "105.9 FM",
            genre = "Salsa / Tropical",
            streamUrl = "https://stream.zeno.fm/uzwqt7k0408uv",
            iconUrl = StationLogoResolver.resolve("Olímpica Stereo", null, "https://olimpicastereo.com.co/")
        ),
        RadioStation(
            id = "5",
            name = "Radio Nacional de Colombia",
            frequency = "95.9 FM",
            genre = "Cultura / Noticias",
            streamUrl = "https://stream.zeno.fm/f3wvbbqmdg8uv",
            iconUrl = StationLogoResolver.resolve("Radio Nacional de Colombia", null, "https://www.radionacional.co/")
        )
    )
}
