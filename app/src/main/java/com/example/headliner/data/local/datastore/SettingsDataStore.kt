package com.example.headliner.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.headliner.domain.model.AppSettings
import com.example.headliner.domain.model.Country
import com.example.headliner.domain.model.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("headliner_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val countryKey = stringPreferencesKey("country")
    private val languageKey = stringPreferencesKey("language")

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        val country = countryOptions.firstOrNull { it.code == preferences[countryKey] } ?: countryOptions.first()
        val language = languageOptions.firstOrNull { it.code == preferences[languageKey] } ?: languageOptions.first()
        AppSettings(country = country, language = language)
    }

    suspend fun setCountry(country: Country) {
        context.settingsDataStore.edit { it[countryKey] = country.code }
    }

    suspend fun setLanguage(language: Language) {
        context.settingsDataStore.edit { it[languageKey] = language.code }
    }

    val countryOptions: List<Country> by lazy {
        loadCountriesFromAssets()
    }

    private val fallbackCountries = listOf(
        Country("India", "in", "https://flagcdn.com/w320/in.png"),
        Country("United States", "us", "https://flagcdn.com/w320/us.png"),
        Country("United Kingdom", "gb", "https://flagcdn.com/w320/gb.png"),
        Country("Germany", "de", "https://flagcdn.com/w320/de.png"),
        Country("France", "fr", "https://flagcdn.com/w320/fr.png")
    )

    private fun loadCountriesFromAssets(): List<Country> {
        return try {
            val jsonString = context.assets.open("countries.json").bufferedReader().use { it.readText() }
            val jsonArray = org.json.JSONArray(jsonString)
            val list = mutableListOf<Country>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Country(
                        label = obj.getString("name"),
                        code = obj.getString("code"),
                        flag = obj.getString("flag")
                    )
                )
            }
            if (list.isEmpty()) fallbackCountries else list
        } catch (e: Exception) {
            fallbackCountries
        }
    }

    val languageOptions: List<Language> by lazy {
        loadLanguagesFromAssets()
    }

    private val fallbackLanguages = listOf(
        Language("English", "en"),
        Language("Hindi", "hi")
    )

    private fun loadLanguagesFromAssets(): List<Language> {
        return try {
            val jsonString = context.assets.open("languages.json").bufferedReader().use { it.readText() }
            val jsonArray = org.json.JSONArray(jsonString)
            val list = mutableListOf<Language>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Language(
                        label = obj.getString("name"),
                        code = obj.getString("code")
                    )
                )
            }
            if (list.isEmpty()) fallbackLanguages else list
        } catch (e: Exception) {
            fallbackLanguages
        }
    }
}
