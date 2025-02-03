package com.example.listify.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavoritesScreen() {
    val favoriteItems = remember { mutableStateListOf("Milk", "Bread", "Eggs", "Potatoes") }
    var newItem by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", color = Color.White) },
                backgroundColor = Color(0xFF2E7D32) // Dark Green
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFC8E6C9)) // Light Green
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text("Add Item") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newItem.isNotEmpty() && !favoriteItems.contains(newItem)) {
                            favoriteItems.add(newItem)
                            newItem = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF43A047)) // Green
                ) {
                    Text("Add to Favorites", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(favoriteItems) { item ->
                        FavoriteItemRow(item = item, onRemove = { favoriteItems.remove(item) })
                    }
                }
            }
        }
    )
}

@Composable
fun FavoriteItemRow(item: String, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFF66BB6A), // Medium Green
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Remove",
                fontSize = 14.sp,
                color = Color(0xFF1B5E20), // Darkest Green
                modifier = Modifier.clickable { onRemove() }
            )
        }
    }
}
