package com.animeboynz.kmd.presentation.components.order.tabs

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun OrderScrollableTabs(
    categories: List<String>,
    pagerState: PagerState,
    getNumberOfOrdersForCategory: (String) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    Column(
        modifier = Modifier.zIndex(1f),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = currentPageIndex,
            edgePadding = 0.dp,
            // TODO: use default when width is fixed upstream
            // https://issuetracker.google.com/issues/242879624
            divider = {},
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = currentPageIndex == index,
                    onClick = { onTabItemClick(index) },
                    text = {
                        TabText(
                            text = category,
                            badgeCount = getNumberOfOrdersForCategory(category),
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        HorizontalDivider()
    }
}

@Composable
fun TabText(text: String, badgeCount: Int? = null) {
    val pillAlpha = if (isSystemInDarkTheme()) 0.12f else 0.08f

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (badgeCount != null) {
            Pill(
                text = "$badgeCount",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = pillAlpha),
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
) {
    Surface(
        modifier = modifier
            .padding(start = 4.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = color,
        contentColor = contentColor,
    ) {
        Box(
            modifier = Modifier
                .padding(6.dp, 1.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                maxLines = 1,
            )
        }
    }
}