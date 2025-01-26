import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shareme.firebase.FirebaseNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.appwrite.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ShowNotesScreen(
    database: FirebaseDatabase,
    client: Client,
    bucketId: String = "6794e8a7001fcddb099c",
    modifier: Modifier = Modifier,
) {
    val storage = io.appwrite.services.Storage(client) // Initialize Appwrite Storage
    var notesList by remember { mutableStateOf<List<FirebaseNote>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Get the current user's email
    val currentUserEmail = remember { FirebaseAuth.getInstance().currentUser?.email }

    // Fetch notes from Firebase Realtime Database
    LaunchedEffect(currentUserEmail) {
        if (currentUserEmail == null) {
            // Handle case where user is not logged in or email is null
            notesList = emptyList()
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        try {
            val notesReference = database.getReference("notes")
            notesReference
                .orderByChild("receiverEmail")
                .equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tempList = snapshot.children.mapNotNull {
                            it.getValue(FirebaseNote::class.java)
                        }
                        notesList = tempList
                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                        isLoading = false
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notesList) { note ->
                NoteCard(note = note, storage = storage, bucketId = bucketId)
            }
        }
    }
}


// Composable for displaying each note
@Composable
fun NoteCard(note: FirebaseNote, storage: io.appwrite.services.Storage, bucketId: String) {
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch image from Appwrite Storage
    LaunchedEffect(note.imageUrl) {
        isLoading = true
        try {
            val result = withContext(Dispatchers.IO) {
                storage.getFileDownload(bucketId = bucketId, fileId = note.imageUrl)
            }
            imageData = result
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = Color(note.color)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                imageData?.let {
                    Image(
                        bitmap = convertImageByteArrayToBitmap(it).asImageBitmap(),
                        contentDescription = note.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// Helper function for converting byte array to bitmap
fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}
