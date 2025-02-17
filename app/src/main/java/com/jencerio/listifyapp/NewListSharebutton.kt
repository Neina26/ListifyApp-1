package com.jencerio.listifyapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.WindowInsets
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.compose.ui.focus.FocusRequester

val units = listOf("pcs", "kg", "g", "l", "ml", "pack", "dozen")

@Composable
fun AddNewListScreen(navController: NavHostController, shoppingListViewModel: ShoppingListViewModel) {
    var listName by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }
    var newItemUnit by remember { mutableStateOf(units.first()) }
    val items = remember { mutableStateListOf<ShoppingItem>() }
    var unitExpanded by remember { mutableStateOf(false) }

    // Get the keyboard controller to manage showing the keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // Ensure the keyboard appears and hide the system toolbar
    val context = LocalContext.current
    val window = (context as Activity).window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.systemBars())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create a New List",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )

        // List Name input field with focus request
        OutlinedTextField(
            value = listName,
            onValueChange = { listName = it },
            label = { Text("List Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .focusRequester(focusRequester) // Add focusRequester
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus() // Request focus when screen loads
        }

        // Item Name input field
        OutlinedTextField(
            value = newItemName,
            onValueChange = { newItemName = it },
            label = { Text("Item Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                }
        )

        // Quantity input field (keyboard type number)
        OutlinedTextField(
            value = newItemQuantity,
            onValueChange = { newItemQuantity = it },
            label = { Text("Quantity") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Box for Unit Selection Dropdown aligned to the right
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            OutlinedTextField(
                value = newItemUnit,
                onValueChange = {},
                readOnly = true,
                label = { Text("Unit") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { unitExpanded = !unitExpanded }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                            contentDescription = "Select unit"
                        )
                    }
                }
            )

            // Align the dropdown menu to appear right under the arrow icon
            DropdownMenu(
                expanded = unitExpanded,
                onDismissRequest = { unitExpanded = false },
                offset = DpOffset(x = 270.dp, y = 5.dp)
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            newItemUnit = unit
                            unitExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val quantity = newItemQuantity.toIntOrNull() ?: 1
                if (newItemName.isNotBlank() && newItemUnit.isNotBlank()) {
                    items.add(ShoppingItem(newItemName, quantity, newItemUnit))
                    newItemName = ""
                    newItemQuantity = ""
                    newItemUnit = units.first()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
        ) {
            Text("Add Item")
        }

        // Display List of Added Items
        if (items.isNotEmpty()) {
            Text(
                "Items in List:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            items.forEach { item ->
                Text(
                    "- ${item.name} (x${item.quantity} ${item.unit})",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }
        } else {
            Text("No items added yet.", color = Color.Gray)
        }

        // Share List Button
        Button(
            onClick = {
                shareListViaEmail(listName, items)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text("Share List via Email")
        }

        // Save List Button
        Button(
            onClick = {
                if (listName.isNotBlank()) {
                    shoppingListViewModel.addShoppingList(listName, items.toList())
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Save List")
        }
    }
}

// Function to share the list via email
private fun shareListViaEmail(listName: String, items: List<ShoppingItem>) {
    val context = LocalContext.current
    val emailBody = buildString {
        append("Here is my shopping list: $listName\n\n")
        items.forEach { item ->
            append("- ${item.name} (x${item.quantity} ${item.unit})\n")
        }
    }

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_SUBJECT, "My Shopping List: $listName")
        putExtra(Intent.EXTRA_TEXT, emailBody)
    }

    context.startActivity(Intent.createChooser(emailIntent, "Send email"))
}
