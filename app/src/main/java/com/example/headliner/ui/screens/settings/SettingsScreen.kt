package com.example.headliner.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var countryExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Settings")
        ExposedDropdownMenuBox(expanded = countryExpanded, onExpandedChange = { countryExpanded = !countryExpanded }) {
            OutlinedTextField(
                value = settings.country.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Country") },
                leadingIcon = {
                    if (settings.country.flag.isNotEmpty()) {
                        AsyncImage(
                            model = settings.country.flag,
                            contentDescription = "${settings.country.label} flag",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(countryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = countryExpanded, onDismissRequest = { countryExpanded = false }) {
                viewModel.countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country.label) },
                        onClick = { viewModel.setCountry(country); countryExpanded = false },
                        leadingIcon = {
                            if (country.flag.isNotEmpty()) {
                                AsyncImage(
                                    model = country.flag,
                                    contentDescription = "${country.label} flag",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(expanded = languageExpanded, onExpandedChange = { languageExpanded = !languageExpanded }) {
            OutlinedTextField(
                value = settings.language.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Language") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(languageExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                viewModel.languages.forEach { language ->
                    DropdownMenuItem(text = { Text(language.label) }, onClick = { viewModel.setLanguage(language); languageExpanded = false })
                }
            }
        }
        TextButton(onClick = { confirmClear = true }) { Text("Clear Saved Articles") }
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            confirmButton = { TextButton(onClick = { viewModel.clearSaved(); confirmClear = false }) { Text("Clear") } },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } },
            title = { Text("Clear saved articles?") },
            text = { Text("This removes offline saved articles from this device.") }
        )
    }
}
