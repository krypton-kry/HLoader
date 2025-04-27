package com.krypton.animeid.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.krypton.animeid.plugins.SearchList
import com.krypton.animeid.plugins.searchAnime
import com.krypton.animeid.utils.SearchListHorizontal
import com.krypton.animeid.utils.SearchListVertical

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<SearchList>()) }

    // Whenever query changes and is >2 chars, run the search
    LaunchedEffect(query) {
        searchResults = if (query.length > 2) {
            searchAnime(query)
        } else {
            emptyList()
        }
    }

    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search anime...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        SearchListVertical(
            list = searchResults,
            isLoading = searchResults.isEmpty() && query.length > 2,
            navController = navController
        )
    }
}
