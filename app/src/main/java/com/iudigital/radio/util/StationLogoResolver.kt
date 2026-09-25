package com.iudigital.radio.util

import android.net.Uri

/**
 * Resuelve el logo/icono de cada emisora usando fuentes que realmente funcionan.
 *
 * ESTRATEGIA:
 * 1. Si la API devuelve un favicon con formato decodificable (PNG, JPG, JPEG, WebP, GIF) → usarlo directo
 * 2. Si el favicon es .ico / .bmp o formato no soportado → usar Google Favicon Proxy (devuelve PNG)
 * 3. Si no hay favicon pero hay homepage → usar Google Favicon Proxy con el dominio
 * 4. Si no hay nada → null (el componente muestra badge con iniciales)
 *
 * Android BitmapFactory (usado por Coil) NO soporta .ico ni .bmp, así que esos
 * se redirigen al proxy de Google que siempre devuelve PNG.
 */
object StationLogoResolver {

    // Extensiones que Android BitmapFactory puede decodificar
    private val SUPPORTED_EXTENSIONS = setOf("png", "jpg", "jpeg", "webp", "gif")

    // Extensiones que NO se pueden decodificar en Android
    private val UNSUPPORTED_EXTENSIONS = setOf("ico", "bmp", "svg", "tiff", "tif")

    /**
     * Resuelve la mejor URL de logo disponible para una emisora.
     */
    fun resolve(stationName: String, apiFavicon: String?, homepage: String?): String? {
        val favicon = apiFavicon?.trim()
        val home = homepage?.trim()

        // 1) Favicon de la API disponible y con formato soportado → usarlo directo
        if (!favicon.isNullOrBlank() && favicon.looksLikeUrl()) {
            if (isSupportedImageFormat(favicon)) {
                // Corregir URLs de imgur (imgur.com/ID.jpg → i.imgur.com/ID.jpg)
                val fixed = fixImgurUrl(favicon)
                return fixed
            }

            // Favicon es .ico/.bmp/no soportado → extraer dominio y usar Google proxy
            val domain = extractDomain(favicon)
            if (domain != null) {
                return googleFaviconProxy(domain)
            }
        }

        // 2) Sin favicon usable, pero hay homepage → Google Favicon Proxy
        if (!home.isNullOrBlank() && home.looksLikeUrl()) {
            val domain = extractDomain(home)
            if (domain != null) {
                return googleFaviconProxy(domain)
            }
        }

        // 3) Sin ninguna fuente → null (el componente muestra badge con iniciales)
        return null
    }

    /**
     * Google devuelve un PNG del favicon de cualquier dominio, tamaño hasta 256px.
     */
    private fun googleFaviconProxy(domain: String): String {
        return "https://www.google.com/s2/favicons?domain=$domain&sz=128"
    }

    /**
     * Determina si la URL apunta a un formato de imagen que Android puede decodificar.
     */
    private fun isSupportedImageFormat(url: String): Boolean {
        val ext = getExtension(url)
        // Si no podemos determinar extensión, asumir que es usable (muchos CDN no usan extensión)
        if (ext.isNullOrBlank()) return true
        // Si es una extensión conocida como no soportada, rechazar
        if (ext in UNSUPPORTED_EXTENSIONS) return false
        // Si es una extensión soportada, aceptar
        if (ext in SUPPORTED_EXTENSIONS) return true
        // Extensión desconocida → intentar usarla (puede ser un CDN con ruta sin extensión)
        return true
    }

    /**
     * Extrae la extensión de una URL ignorando query params y fragments.
     */
    private fun getExtension(url: String): String? {
        return try {
            val path = Uri.parse(url).path ?: return null
            val lastDot = path.lastIndexOf('.')
            if (lastDot >= 0 && lastDot < path.length - 1) {
                path.substring(lastDot + 1).lowercase()
            } else null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Extrae el dominio de una URL (sin "www.")
     */
    private fun extractDomain(url: String): String? {
        return try {
            val uri = Uri.parse(url)
            uri.host?.removePrefix("www.")
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Corrige URLs de imgur: imgur.com/ID.ext → i.imgur.com/ID.ext
     */
    private fun fixImgurUrl(url: String): String {
        if (url.contains("imgur.com/") && !url.contains("i.imgur.com/")) {
            return url.replace("://imgur.com/", "://i.imgur.com/")
        }
        return url
    }

    /**
     * Verifica que un string se parece a una URL HTTP válida
     */
    private fun String.looksLikeUrl(): Boolean {
        val trimmed = this.trim()
        return trimmed.startsWith("http://") || trimmed.startsWith("https://")
    }
}
