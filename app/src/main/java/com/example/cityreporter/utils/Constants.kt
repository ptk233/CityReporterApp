package com.example.cityreporter.utils

object Constants {
    const val MAX_PHOTOS_PER_REPORT = 5
    const val PREFS_NAME = "city_reporter_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_ROLE = "user_role"
    
    // API endpoints
    // const val BASE_URL = "http://10.0.2.2:8080/" // dla emulatora Android (lokalny serwer)
    // const val BASE_URL = "http://192.168.1.X:8080/" // dla prawdziwego urządzenia - podmień X na IP twojego komputera
    
    // Production URL (Railway)
    const val BASE_URL = "https://city-reporter-backend-production-xxxx.up.railway.app/" // ⚠️ ZAMIEŃ na swój URL z Railway!
    
    // Map settings
    const val DEFAULT_MAP_ZOOM = 15f
    const val DEFAULT_LAT = 51.107883 // Wrocław
    const val DEFAULT_LNG = 17.038538
    const val DEFAULT_SEARCH_RADIUS_KM = 5.0
    
    // UI settings
    const val SPLASH_SCREEN_DURATION = 2000L // milliseconds
    
    // Image settings
    const val IMAGE_COMPRESSION_QUALITY = 80
    const val MAX_IMAGE_SIZE = 1024 // px
}
