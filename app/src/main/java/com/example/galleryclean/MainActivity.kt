package com.example.galleryclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.galleryclean.data.SampleData
import com.example.galleryclean.model.Picture
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { GalleryScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    val gallery = remember { mutableStateListOf<Picture>().apply { addAll(SampleData.generateSamplePictures()) } }
    var isGrid by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = if (searchText.isBlank())
        gallery.toList()
    else
        gallery.filter { it.author.contains(searchText, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Галерея") },
                actions = {
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(Icons.Filled.ViewModule, contentDescription = "Toggle")
                    }
                    IconButton(onClick = { gallery.clear() }) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = "Clear All")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(end = 50.dp, bottom = 10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }

    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = { Text("Поиск по автору") },
                singleLine = true
            )

            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { pic ->
                        PictureCard(pic,
                            onImageClick = {
                                val removed = pic
                                gallery.remove(removed)
                                scope.launch {
                                    val res = snackbarHostState.showSnackbar("Удалено: ${removed.author}", actionLabel = "Отмена")
                                    if (res == SnackbarResult.ActionPerformed) gallery.add(removed)
                                }
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { pic ->
                        PictureRow(pic,
                            onImageClick = {
                                val removed = pic
                                gallery.remove(removed)
                                scope.launch {
                                    val res = snackbarHostState.showSnackbar("Удалено: ${removed.author}", actionLabel = "Отмена")
                                    if (res == SnackbarResult.ActionPerformed) gallery.add(removed)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPictureDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { id, author, url ->
                val exists = gallery.any { it.id == id || it.url == url }
                if (exists) {
                    scope.launch { snackbarHostState.showSnackbar("Картинка с таким id или url уже существует") }
                } else {
                    gallery.add(Picture(id, author.trim(), url.trim()))
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PictureRow(picture: Picture, onImageClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageBox(url = picture.url, modifier = Modifier.size(96.dp).clickable { onImageClick() })
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = "ID: ${picture.id}", style = MaterialTheme.typography.labelMedium)
                Text(text = picture.author, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = picture.url, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun PictureCard(picture: Picture, onImageClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ImageBox(
                url = picture.url,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { onImageClick() }
            )
            Column(Modifier.padding(8.dp)) {
                Text(text = picture.author, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "ID: ${picture.id}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ImageBox(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Изображение",
        placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.placeholder),
        error = androidx.compose.ui.res.painterResource(id = R.drawable.error_img),
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPictureDialog(
    onDismiss: () -> Unit,
    onConfirm: (id: Int, author: String, url: String) -> Unit
) {
    var idText by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить картинку") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = idText,
                    onValueChange = { idText = it.filter { ch -> ch.isDigit() }.take(9) },
                    label = { Text("ID (целое)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Автор") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL (https)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val id = idText.toIntOrNull()
                if (id != null && author.isNotBlank() && url.isNotBlank()) {
                    onConfirm(id, author, url)
                }
            }) { Text("ОК") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}