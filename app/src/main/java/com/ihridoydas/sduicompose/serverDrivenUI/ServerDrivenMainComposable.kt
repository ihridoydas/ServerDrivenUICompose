/*
* MIT License
*
* Copyright (c) 2024 Hridoy Chandra Das
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
package com.ihridoydas.sduicompose.serverDrivenUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ihridoydas.sduicompose.common.ROUNDED_CORNER_SHAPE_PERCENT
import timber.log.Timber

@Composable
fun ServerDrivenMainComposable(viewModel: ServerDrivenViewModel = viewModel()) {
    val layoutInformation by viewModel.layoutInformationFlow.collectAsState()

    when (layoutInformation) {
        null -> LoadingComponent()
        else -> NewsFeedScreen(layoutInformation = layoutInformation!!)
    }
}

sealed interface LayoutType {
    data object List : LayoutType

    data class Grid(val columns: Int) : LayoutType
}

data class LayoutMeta(
    val layoutType: LayoutType,
    val favoriteEnabled: Boolean,
)

data class LayoutInformation(
    val layoutMeta: LayoutMeta,
    val layoutData: List<ServerDrivenViewModel.NewsItem>,
)

@Composable
fun NewsFeedScreen(layoutInformation: LayoutInformation) {
    when (layoutInformation.layoutMeta.layoutType) {
        is LayoutType.List -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = layoutInformation.layoutData, key = { newsItem -> newsItem.id }) {
                    NewsItemComponent(
                        newsItem = it,
                        favoriteEnabled = layoutInformation.layoutMeta.favoriteEnabled,
                    )
                }
            }
        }
        is LayoutType.Grid -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(layoutInformation.layoutMeta.layoutType.columns),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = layoutInformation.layoutData, key = { newsItem -> newsItem.id }) {
                    NewsItemComponent(
                        newsItem = it,
                        favoriteEnabled = layoutInformation.layoutMeta.favoriteEnabled,
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingComponent() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
            color = Color.Blue,
            progress = 100f,
        )
    }
}

@Composable
fun NewsItemComponent(
    newsItem: ServerDrivenViewModel.NewsItem,
    favoriteEnabled: Boolean,
) {
    Column(
        modifier = Modifier
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = newsItem.title)
            Spacer(modifier = Modifier.weight(1f))
            if (favoriteEnabled) {
                val icon = if (newsItem.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder
                Icon(
                    imageVector = icon,
                    contentDescription = "Favorite",
                    modifier = Modifier.clickable {
                        Timber.tag("Favorite").e("Handle onClick for %s", newsItem.id)
                    },
                )
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(color = Color.DarkGray, shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE_PERCENT)),
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = newsItem.description)
        }
    }
}
