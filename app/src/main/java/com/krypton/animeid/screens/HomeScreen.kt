package com.krypton.animeid.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.krypton.animeid.plugins.SearchList
import com.krypton.animeid.plugins.getLatestEpisodes
import com.krypton.animeid.utils.SearchListHorizontal
import kotlinx.coroutines.delay

@Composable
fun ScreenHeader(
    title: String,
    imageUrls: List<String>
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(imageUrls) {
        while (true) {
            delay(5000)
            if (imageUrls.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % imageUrls.size
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (imageUrls.isNotEmpty()) {
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Start from right
                        animationSpec = tween(700) // speed
                    ) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth }, // Exit to left
                                animationSpec = tween(700)
                            )
                },
                label = "SlideImage"
            ) { targetIndex ->
                AsyncImage(
                    model = imageUrls[targetIndex],
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
            }
        }

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

@Composable
fun HomeScreen(navController: NavController) {
    var episodes by remember { mutableStateOf<List<SearchList>>(emptyList()) }

    // Fetch the data asynchronously
    LaunchedEffect(Unit) {
        episodes = getLatestEpisodes()
    }

    // Extract images list
    val imageUrls = episodes.map { it.img }.reversed()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Pass the images to the header
        ScreenHeader(
            title = "HLoader",
            imageUrls = imageUrls
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchListHorizontal(
            items = episodes,
            isLoading = episodes.isEmpty(),
            navController = navController
        )
    }
}
