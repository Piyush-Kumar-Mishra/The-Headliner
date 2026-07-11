package com.example.headliner.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.headliner.data.local.datastore.SettingsDataStore
import com.example.headliner.domain.model.AppSettings
import com.example.headliner.domain.model.Country
import com.example.headliner.domain.model.Language
import com.example.headliner.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val repository: NewsRepository
) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsDataStore.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    val countries = settingsDataStore.countryOptions
    val languages = settingsDataStore.languageOptions

    fun setCountry(country: Country) = viewModelScope.launch { settingsDataStore.setCountry(country) }
    fun setLanguage(language: Language) = viewModelScope.launch { settingsDataStore.setLanguage(language) }
    fun clearSaved() = viewModelScope.launch { repository.clearSavedArticles() }
}
