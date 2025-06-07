@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.noteappv01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.ui.theme.NoteAppTheme
import java.text.SimpleDateFormat
import java.util.*

// Data model
data class Note(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

// Navigation screens
sealed class Screen(val route: String) {
    object NotesList : Screen("notesList")
    object NoteDetail : Screen("noteDetail/{noteId}") {
        fun createRoute(noteId: String) = "noteDetail/$noteId"
    }
    object NoteEdit : Screen("noteEdit/{noteId}") {
        fun createRoute(noteId: String) = "noteEdit/$noteId"
        const val NEW_NOTE_ID = "new"
    }
}

// Main activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAppTheme {
                NotesApp()
            }
        }
    }
}

// Root app composable with navigation and state
@Composable
fun NotesApp() {
    val navController = rememberNavController()
    var notes by remember { mutableStateOf(emptyList<Note>()) }

    NavHost(navController, startDestination = Screen.NotesList.route) {
        composable(Screen.NotesList.route) {
            NotesListScreen(
                notes = notes,
                onNoteClick = { navController.navigate(Screen.NoteDetail.createRoute(it)) },
                onAddNote = { navController.navigate(Screen.NoteEdit.createRoute(Screen.NoteEdit.NEW_NOTE_ID)) },
                onDeleteNote = { id -> notes = notes.filter { it.id != id } }
            )
        }
        composable(Screen.NoteDetail.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            notes.find { it.id == noteId }?.let { note ->
                NoteDetailScreen(
                    note = note,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.NoteEdit.createRoute(note.id)) }
                )
            } ?: navController.popBackStack()
        }
        composable(Screen.NoteEdit.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val isNew = noteId == Screen.NoteEdit.NEW_NOTE_ID
            val note = if (isNew) null else notes.find { it.id == noteId }

            NoteEditScreen(
                note = note,
                onSave = { title, content ->
                    notes = if (isNew) {
                        notes + Note(title = title, content = content)
                    } else {
                        notes.map {
                            if (it.id == noteId) it.copy(title = title, content = content, updatedAt = System.currentTimeMillis()) else it
                        }
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// Notes list screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    onAddNote: () -> Unit,
    onDeleteNote: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(note, onClick = { onNoteClick(note.id) }, onDelete = { onDeleteNote(note.id) })
                }
            }
        }
    }
}

// Single note item card
@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Updated: ${SimpleDateFormat("MMM dd, HH:mm").format(Date(note.updatedAt))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Note detail screen
@Composable
fun NoteDetailScreen(note: Note, onBack: () -> Unit, onEdit: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize()
        ) {
            Text(note.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Text(
                text = "Created: ${SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(note.createdAt))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Last updated: ${SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(note.updatedAt))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Note edit/create screen
@Composable
fun NoteEditScreen(note: Note?, onSave: (String, String) -> Unit, onBack: () -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue(note?.title ?: "")) }
    var content by remember { mutableStateOf(TextFieldValue(note?.content ?: "")) }
    val titleError = remember { mutableStateOf<String?>(null) }
    val contentError = remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var valid = true
        titleError.value = when {
            title.text.isEmpty() -> { valid = false; "Title cannot be empty" }
            title.text.length < 3 -> { valid = false; "Title must be at least 3 characters" }
            title.text.length > 50 -> { valid = false; "Title must be at most 50 characters" }
            else -> null
        }
        contentError.value = when {
            content.text.isEmpty() -> { valid = false; "Content cannot be empty" }
            content.text.length > 500 -> { valid = false; "Content must be at most 500 characters" }
            else -> null
        }
        return valid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note == null) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (validate()) onSave(title.text, content.text) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Save") },
                text = { Text("Save") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError.value != null,
                supportingText = { titleError.value?.let { Text(it) } }
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                isError = contentError.value != null,
                supportingText = {
                    contentError.value?.let { Text(it) } ?: Text("${content.text.length}/500")
                }
            )
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun NotesListPreview() {
    NoteAppTheme {
        NotesListScreen(
            notes = listOf(
                Note(title = "Shopping List", content = "Milk, Eggs, Bread"),
                Note(title = "Meeting Notes", content = "Project timeline and tasks"),
                Note(title = "Ideas", content = "New app feature ideas")
            ),
            onNoteClick = {},
            onAddNote = {},
            onDeleteNote = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailPreview() {
    NoteAppTheme {
        NoteDetailScreen(
            note = Note(
                title = "Shopping List",
                content = "Milk\nEggs\nBread",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            onBack = {},
            onEdit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteEditPreview() {
    NoteAppTheme {
        NoteEditScreen(
            note = null,
            onSave = { _, _ -> },
            onBack = {}
        )
    }
}
