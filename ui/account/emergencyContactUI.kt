package com.example.tripshare.ui.account

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tripshare.data.model.EmergencyContactEntity

data class EmergencyContactUi(
    val name: String = "",
    val relationship: String = "",
    val countryCode: String = "+60",
    val phone: String = ""
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeDropdown(
    value: String,
    enabled: Boolean,
    onSelected: (String) -> Unit
) {
    val countryCodes = listOf(
        "+60" to "Malaysia",
        "+65" to "Singapore",
        "+62" to "Indonesia",
        "+66" to "Thailand",
        "+63" to "Philippines",
        "+84" to "Vietnam"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Code") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor() // ✅ IMPORTANT: anchor the menu to this field
                .width(120.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { (code, country) ->
                DropdownMenuItem(
                    text = { Text("$code  $country") },
                    onClick = {
                        onSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}


fun List<EmergencyContactUi>.toEntities(userId: Long): List<EmergencyContactEntity> =
    this.map { ui ->
        EmergencyContactEntity(
            id = 0L,                // let Room autogenerate
            userId = userId,
            name = ui.name.trim(),
            relationship = ui.relationship.trim(),
            phone = ui.phone.trim()
        )
    }.filter { it.name.isNotBlank() || it.phone.isNotBlank() }

