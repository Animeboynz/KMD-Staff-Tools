package com.animeboynz.kmd.ui.preferences.options

import android.os.Build
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.MultiChoiceSegmentedButton
import com.animeboynz.kmd.presentation.components.preferences.ReplacingTextFieldPreference
import com.animeboynz.kmd.ui.theme.DarkMode
import com.animeboynz.kmd.ui.theme.spacing
import com.animeboynz.kmd.utils.Constants
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.TextFieldPreference
import org.koin.compose.koinInject

import androidx.annotation.IntRange

object GeneralPreferencesScreen : Screen() {
    private fun readResolve(): Any = GeneralPreferencesScreen

    @Composable
    override fun Content() {
        val preferences = koinInject<GeneralPreferences>()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_general_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            ProvidePreferenceLocals {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_general_category_store)) },
                    )

                    var storeName by remember {
                        mutableStateOf(preferences.storeName.get())
                    }
                    ReplacingTextFieldPreference(
                        value = storeName,
                        onValueChange = { storeName = it },
                        title = stringResource(R.string.pref_general_store_name),
                        description = stringResource(R.string.pref_general_store_name_description),
                        textToValue = {
                            preferences.storeName.set(it)
                            it
                        },
                    )

                    var storeNumber by remember {
                        mutableStateOf(preferences.storeNumber.get())
                    }
                    ReplacingTextFieldPreference(
                        value = storeNumber,
                        onValueChange = { storeNumber = it },
                        title = stringResource(R.string.pref_general_store_number),
                        description = stringResource(R.string.pref_general_store_number_description),
                        textToValue = {
                            preferences.storeNumber.set(it)
                            it
                        },
                    )

                    var stockCheckRegion by remember {
                        mutableStateOf(preferences.stockCheckRegion.get())
                    }
                    ReplacingTextFieldPreference(
                        value = stockCheckRegion,
                        onValueChange = { stockCheckRegion = it },
                        title = "Stock Check Region",
                        description = "This is the region used when checking for nearby stock levels. Enter a region like 'Auckland' or a postcode like '1010'",
                        textToValue = {
                            preferences.stockCheckRegion.set(it)
                            it
                        },
                    )

                    var countryCode by remember {
                        mutableStateOf(preferences.countryCode.get())
                    }

                    val countryCodes = persistentListOf("NZ", "AU", "US", "GB", "CA", "FR", "DE")
                    ReplacingTextFieldPreference(
                        value = countryCode,
                        onValueChange = { countryCode = it },
                        title = stringResource(R.string.pref_general_country_code),
                        description = stringResource(R.string.pref_general_country_code_description),
                        textToValue = {
                            if (countryCodes.contains(it))
                            {
                                preferences.countryCode.set(it)
                                it
                            } else {
                                preferences.countryCode.set("NZ")
                                "NZ"
                            }
                        },
                    )

                    var orderNumberPadding by remember {
                        mutableStateOf(preferences.orderNumberPadding.get())
                    }
//                    val orderNumberPaddings by preferences.orderNumberPadding.collectAsState()
//                    SliderItem(
//                        value = orderNumberPaddings,
//                        label = "skui",
//                        valueText = orderNumberPaddings.toString(),
//                        min = 1,
//                        max = 8,
//                        onChange = {
//                            preferences.orderNumberPadding.set(it)
//                            true
//                        },
//                    )

                    ReplacingTextFieldPreference(
                        value = orderNumberPadding,
                        onValueChange = { orderNumberPadding = it.toString().toInt() },
                        title = stringResource(R.string.pref_general_order_number_pad),
                        description = stringResource(R.string.pref_general_order_number_pad_description),
                        textToValue = {
                            if (it.toInt() < 1)
                            {
                                preferences.orderNumberPadding.set(1)
                                1
                            } else if (it.toInt() > 8) {
                                preferences.orderNumberPadding.set(8)
                                8
                            } else {
                                preferences.orderNumberPadding.set(it.toInt())
                                it.toInt()
                            }
                        },
                        keyboardType = KeyboardType.Number,
                    )
                }
            }
        }
    }

//    @Composable
//    fun SliderItem(
//        label: String,
//        value: Int,
//        valueText: String,
//        onChange: (Int) -> Unit,
//        max: Int,
//        min: Int = 0,
//    ) {
//        val haptic = LocalHapticFeedback.current
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp, vertical = 10.dp), // Match padding with other preference items
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(24.dp),
//        ) {
//            Column(modifier = Modifier.weight(0.5f)) {
//                Text(
//                    text = label,
//                    style = MaterialTheme.typography.bodyMedium, // Match font size with other preference items
//                )
//                Text(
//                    text = valueText,
//                    style = MaterialTheme.typography.bodySmall // Match font size with other preference items
//                )
//            }
//
//            Sliders(
//                modifier = Modifier.weight(1.5f),
//                value = value,
//                onValueChange = f@{
//                    if (it == value) return@f
//                    onChange(it)
//                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                },
//                valueRange = min..max,
//            )
//        }
//    }
//
//    @Composable
//    fun Sliders(
//        value: Int,
//        onValueChange: (Int) -> Unit,
//        modifier: Modifier = Modifier,
//        enabled: Boolean = true,
//        valueRange: ClosedRange<Int> = 0..1,
//        @IntRange(from = 0) steps: Int = with(valueRange) { (endInclusive - start) - 1 },
//        onValueChangeFinished: (() -> Unit)? = null,
//        colors: SliderColors = SliderDefaults.colors(),
//        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
//    ) {
//        Slider(
//            value = value.toFloat(),
//            onValueChange = { onValueChange(it.toInt()) },
//            modifier = modifier,
//            enabled = enabled,
//            valueRange = with(valueRange) { start.toFloat()..endInclusive.toFloat() },
//            steps = steps,
//            onValueChangeFinished = onValueChangeFinished,
//            colors = colors,
//        )
//    }
}
