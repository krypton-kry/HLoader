package com.krypton.animeid.screens

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.krypton.animeid.plugins.AnimeDetails
import com.krypton.animeid.plugins.SearchList
import com.krypton.animeid.plugins.getAnimeDetails
import com.krypton.animeid.plugins.getAnimeList
import com.krypton.animeid.utils.SearchListHorizontal

@Composable
fun AnimeScreenHeader(
    title: String,
    imageUrl: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = size.height / 2f,
                            endY = size.height
                        ),
                        blendMode = BlendMode.Multiply
                    )
                }
        )

        // Title on top of image
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),

            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

/*
@Composable
fun AnimeDetailScreen(title: String, url: String, img: String) {
    var details by remember { mutableStateOf<AnimeDetails?>(null) }
    var hasShownToast by remember { mutableStateOf(false) } // to prevent infinite toasts

    LaunchedEffect(Unit) {
        details = getAnimeDetails(url)
    }

    details?.let { animeDetails ->
        if(animeDetails.downloadUrl.isEmpty() && !hasShownToast) {
            Toast.makeText(LocalContext.current, "Failed to get dl url! Retry later", Toast.LENGTH_SHORT).show()
            hasShownToast = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AnimeScreenHeader(title, img)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "URL: $url",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
*/
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    // 1) Create an infinite transition that animates a float from 0f → 1f
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .drawWithCache {
                // 2) Calculate gradient width relative to the box width
                val gradientWidth = size.width / 2f
                // 3) Compute start/end based on animation progress
                val startX = lerp(-gradientWidth, size.width + gradientWidth, progress)
                val endX = startX + gradientWidth

                // 4) Create a brush that moves across
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.6f),
                        Color.LightGray.copy(alpha = 0.2f),
                        Color.LightGray.copy(alpha = 0.6f)
                    ),
                    start = Offset(startX, 0f),
                    end = Offset(endX, 0f)
                )

                onDrawWithContent {
                    drawRect(brush = brush)
                }
            }
    )
}

// Helper to linearly interpolate
private fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)

@Composable
fun GenreItem(genre: String) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable { /* handle click */ }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun startDownload(context: Context, url: String, fileName: String) {
    // Create the DownloadManager instance
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = url.toUri()

    // Create the request to start the download
    val request = DownloadManager.Request(uri).apply {
        setTitle(fileName)  // Set the title (file name)
        setDescription("Downloading...")  // Description (will be shown in the notification)
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)  // Show notification when done
    }

    // Start the download
    downloadManager.enqueue(request)

    // Notify the user that the download has started
    Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
}

@Composable
fun AnimeDetailScreen(
    title: String,
    url: String,
    img: String,
    navController: NavController
) {
    // Track loading state
    var details by remember { mutableStateOf(AnimeDetails()) }
    var animeList by remember { mutableStateOf(emptyList<SearchList>()) }
    var batch by remember { mutableStateOf(emptyList<AnimeDetails>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        details = getAnimeDetails(url)
        animeList = getAnimeList(details.animePage)
        if (animeList.size > 1) {
            for (a in animeList) {
                if(a.url != url) {
                    batch += getAnimeDetails(a.url)
                }
            }
        }
        isLoading = false
    }

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header Item (Fixed)
        item {
            AnimeScreenHeader(title, img)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Show shimmer if loading, else display the content
        if (isLoading) {
            items(6) {  // e.g. 6 placeholder rows
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (it == 0) 200.dp else 16.dp)
                        .padding(vertical = if (it == 0) 0.dp else 4.dp)
                )
            }
        } else {
            // Anime Description
            item {

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = details.description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (details.downloadUrl.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                    Button(
                        onClick = {
                            val name = title.replace("`", "").replace(" ", "-").lowercase()
                            startDownload(context, details.downloadUrl, "$name.mp4")
                            if (details.subsUrl.isNotEmpty()) {
                                startDownload(context, details.subsUrl, "$name.srt")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(.9f)
                            .padding(vertical = 10.dp)
                            .padding(horizontal = 10.dp)

                    ) {
                        Text(text = "Download")
                    }
                }
                }
            }

            if (batch.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                // download current
                                val name = title.replace("`", "").replace(" ", "-").lowercase()
                                startDownload(context, details.downloadUrl, "$name.mp4")
                                if (details.subsUrl.isNotEmpty()) {
                                    startDownload(context, details.subsUrl, "$name.srt")
                                }

                                // download rest
                                for (b in batch) {
                                    val name =
                                        b.title.replace("`", "").replace(" ", "-").lowercase()
                                    startDownload(context, b.downloadUrl, "$name.mp4")
                                    if (details.subsUrl.isNotEmpty()) {
                                        startDownload(context, b.subsUrl, "$name.srt")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .padding(vertical = 10.dp)
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(text = "Batch Download (${batch.size + 1})")
                        }
                    }
                }
            }
            // Anime Genre with Horizontal Scroll
            item {
                Text(
                    text = "Genre",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    for (g in details.genre) {
                        item {
                            GenreItem(g)
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Similar Anime List
            item {
                Text(
                    text = "Similar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                SearchListHorizontal(
                    items = details.similar,
                    isLoading = details.similar.isEmpty(),
                    navController = navController
                )
            }
        }
    }
}