// ui/emergency/EmergencyCountryData.kt
// ui/emergency/EmergencyCountryData.kt
package com.example.tripshare.ui.emergency

data class LocalEmergencyNumbers(
    val countryName: String,
    val police: String,
    val ambulance: String,
    val fire: String
)

object EmergencyCountryData {
    // Default to GSM Standard (112 works in most countries as a redirect)
    val DEFAULT = LocalEmergencyNumbers("International/Default", "112", "112", "112")

    private val countryMap = mapOf(
        "MY" to LocalEmergencyNumbers("Malaysia", "999", "999", "999"),
        "JP" to LocalEmergencyNumbers("Japan", "110", "119", "119"),      // 110 Police, 119 Fire/Amb
        "US" to LocalEmergencyNumbers("United States", "911", "911", "911"),
        "GB" to LocalEmergencyNumbers("United Kingdom", "999", "999", "999"), // 112 also works
        "CN" to LocalEmergencyNumbers("China", "110", "120", "119"),      // 120 Amb, 119 Fire
        "KR" to LocalEmergencyNumbers("South Korea", "112", "119", "119"),// 112 Police, 119 Fire/Amb
        "ID" to LocalEmergencyNumbers("Indonesia", "110", "112", "113"),  // 112 is now unified emergency
        "SG" to LocalEmergencyNumbers("Singapore", "999", "995", "995"),
        "TH" to LocalEmergencyNumbers("Thailand", "191", "1669", "199")   // 1669 is Medical
    )

    fun getNumbers(countryCode: String?): LocalEmergencyNumbers {
        return countryMap[countryCode?.uppercase()] ?: DEFAULT
    }
}