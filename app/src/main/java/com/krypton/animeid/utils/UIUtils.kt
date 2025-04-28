package com.krypton.animeid.utils

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krypton.animeid.plugins.SearchList


@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition()

    val translateAnim = transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,

        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        ),

        start = Offset.Zero, end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
}


@Composable
fun ShimmerCard(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(shimmerBrush(), shape = RoundedCornerShape(12.dp))
    )
}

@Composable
fun SearchListHorizontal(
    items: List<SearchList>,
    isLoading: Boolean,
    navController: NavController
) {

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            items(8) { item -> ShimmerCard(Modifier
                .width(200.dp)
                .height(300.dp)) }
        } else {
            items.forEach { item ->
                item {
                    SearchItemCard(
                        item, Modifier
                            .width(200.dp)
                            .height(300.dp)
                    ) {
                        navController.navigate(
                            "detail/${Uri.encode(item.title)}/${Uri.encode(item.url)}/${
                                Uri.encode(
                                    item.img
                                )
                            }"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchListVertical(
    list: List<SearchList>, isLoading: Boolean, navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            // Show 8 shimmer pairs (16 placeholders)
            items(8) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ShimmerCard(Modifier
                        .weight(1f)
                        .height(200.dp))
                    ShimmerCard(Modifier
                        .weight(1f)
                        .height(200.dp))
                }
            }
        } else {

            // Chunk your list into rows of 2 items each
            items(list.chunked(2)) { rowItems ->
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { item ->

                        SearchItemCard(
                            item, Modifier
                                .weight(1f)      // equal share of width
                                .height(200.dp)
                        ) {
                            navController.navigate(
                                "detail/${Uri.encode(item.title)}/" + "${Uri.encode(item.url)}/" + "${
                                    Uri.encode(
                                        item.img
                                    )
                                }"
                            )
                        }
                    }
                    // If odd count, fill the second column
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchItemCard(item: SearchList, modifier: Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(item.img).crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = rememberShimmerPainter()
            )

            Text(
                text = item.title,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun rememberShimmerPainter(): Painter {
    val brush = shimmerBrush()
    return remember {
        object : Painter() {
            override val intrinsicSize: Size get() = Size.Unspecified
            override fun DrawScope.onDraw() {
                drawRect(brush = brush)
            }
        }
    }
}
