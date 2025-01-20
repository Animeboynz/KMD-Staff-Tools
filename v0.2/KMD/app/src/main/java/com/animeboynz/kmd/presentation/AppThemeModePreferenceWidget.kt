package com.animeboynz.kmd.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.animeboynz.kmd.presentation.components.preferences.BasePreferenceWidget
import com.animeboynz.kmd.presentation.components.preferences.PrefsHorizontalPadding
import com.animeboynz.kmd.ui.theme.ThemeMode
import com.animeboynz.kmd.R

private val options = mapOf(
    ThemeMode.SYSTEM to R.string.pref_appearance_darkmode_system,
    ThemeMode.LIGHT to R.string.pref_appearance_darkmode_light,
    ThemeMode.DARK to R.string.pref_appearance_darkmode_dark,
)

@Composable
internal fun AppThemeModePreferenceWidget(
    value: ThemeMode,
    onItemClick: (ThemeMode) -> Unit,
) {
    BasePreferenceWidget(
        subcomponent = {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PrefsHorizontalPadding),
            ) {
                options.onEachIndexed { index, (mode, labelRes) ->
                    SegmentedButton(
                        checked = mode == value,
                        onCheckedChange = { onItemClick(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            options.size,
                        ),
                    ) {
                        Text(stringResource(labelRes))
                    }
                }
            }
        },
    )
}
