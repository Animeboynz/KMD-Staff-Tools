package com.animeboynz.kmd.ui.preferences.options

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.animeboynz.kmd.R
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.collectAsState
import com.animeboynz.kmd.presentation.Screen
import com.animeboynz.kmd.presentation.components.preferences.MultiChoiceSegmentedButton
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

                    var orderNumberPadding by remember {
                        mutableStateOf(preferences.orderNumberPadding.get())
                    }
                    ReplacingTextFieldPreference(
                        value = orderNumberPadding,
                        onValueChange = { orderNumberPadding = it.toString().toInt() },
                        title = stringResource(R.string.pref_general_order_number_pad),
                        description = stringResource(R.string.pref_general_order_number_pad_description),
                        textToValue = {
                            preferences.orderNumberPadding.set(
                                if (it.toInt() < 1) 1
                                else if (it.toInt() > 8) 8
                                else it.toInt())
                            it
                        },
                        keyboardType = KeyboardType.Number,
                    )
                }
            }
        }
    }

    @Composable
    fun <T> ReplacingTextFieldPreference(
        value: T,
        onValueChange: (T) -> Unit,
        title: String,
        description: String?,
        textToValue: (String) -> T?,
        keyboardType: KeyboardType = KeyboardType.Unspecified,
    ) {
        TextFieldPreference(
            value = value,
            onValueChange = onValueChange,
            title = { Text(text = title) },
            summary = { Text(text = value.toString()) },
            textField = { value, onValueChange, onOk ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                ) {
                    Text(text = description ?: "")

                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        keyboardActions = KeyboardActions(onDone = { onOk() }),
                    )
                }
            },
            textToValue = textToValue,
        )
    }
}
