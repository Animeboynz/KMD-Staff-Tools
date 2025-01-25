package com.animeboynz.kmd.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.animeboynz.kmd.utils.Constants.SECONDARY_ALPHA

fun Modifier.secondaryItemAlpha(): Modifier = this.alpha(SECONDARY_ALPHA)