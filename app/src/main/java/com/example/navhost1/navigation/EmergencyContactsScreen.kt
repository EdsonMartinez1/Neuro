package com.example.navhost1.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ContentResolver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val DarkBg      = Color(0xFF0F172A)
private val DarkBgBot   = Color(0xFF090D16)
private val CardColor   = Color(0xFF1E293B).copy(alpha = 0.75f)
private val VioletLight = Color(0xFFC084FC)
private val Violet      = Color(0xFF8B5CF6)
private val WhiteSoft   = Color(0xFFF8FAFC)
private val GrayText    = Color(0xFF94A3B8)

data class EmergencyContact(
    val id:       String = "",
    val nombre:   String = "",
    val telefono: String = ""
)

@Composable
fun EmergencyContactsScreen(navController: NavHostController) {
    val auth    = FirebaseAuth.getInstance()
    val db      = FirebaseFirestore.getInstance()
    val uid     = auth.currentUser?.uid ?: return
    val context = LocalContext.current

    val contactsRef = db.collection("usuarios")
        .document(uid)
        .collection("wearable")
        .document("contactos")
        .collection("lista")

    var contacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    // Lanzador de la app de contactos del sistema
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let {
            try {
                var nombre   = ""
                var telefono = ""
                val contentResolver = context.contentResolver

                contentResolver.query(
                    uri,
                    arrayOf(
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts._ID
                    ),
                    null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        nombre = cursor.getString(
                            cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                        ) ?: ""

                        val contactId = cursor.getString(
                            cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                        )

                        contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(contactId),
                            null
                        )?.use { phoneCursor ->
                            if (phoneCursor.moveToFirst()) {
                                telefono = phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                                ) ?: ""
                            }
                        }
                    }
                }

                if (nombre.isNotBlank() && telefono.isNotBlank()) {
                    contactsRef.add(
                        hashMapOf(
                            "nombre"   to nombre,
                            "telefono" to telefono
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("CONTACTS", "Error leyendo contacto: ${e.message}")
            }
        }
    }

// Pide permiso y luego abre contactos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) contactPickerLauncher.launch(null)
    }
    LaunchedEffect(Unit) {
        contactsRef.addSnapshotListener { snapshot, _ ->
            contacts = snapshot?.documents?.map { doc ->
                EmergencyContact(
                    id       = doc.id,
                    nombre   = doc.getString("nombre")   ?: "",
                    telefono = doc.getString("telefono") ?: ""
                )
            } ?: emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBg, DarkBgBot)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Contactos de", color = GrayText, fontSize = 14.sp)
                    Text(
                        text       = "emergencia",
                        color      = WhiteSoft,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Abre la app de contactos del sistema
                FloatingActionButton(
                    onClick = {
                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                context, android.Manifest.permission.READ_CONTACTS
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            contactPickerLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                        }
                    },
                    containerColor = Violet,
                    contentColor   = WhiteSoft,
                    modifier       = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Se sincronizan automáticamente con tu reloj.",
                color    = GrayText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(20.dp))

            if (contacts.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier         = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Violet.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint               = VioletLight,
                                modifier           = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text       = "Sin contactos",
                            color      = WhiteSoft,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text      = "Toca + para agregar contactos\nde emergencia desde tu agenda.",
                            color     = GrayText,
                            fontSize  = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(contacts) { contact ->
                        EmergencyContactCard(
                            contact  = contact,
                            onDelete = { contactsRef.document(contact.id).delete() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyContactCard(contact: EmergencyContact, onDelete: () -> Unit) {
    Surface(
        shape    = RoundedCornerShape(18.dp),
        color    = CardColor,
        border   = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Violet.copy(alpha = 0.2f))
                ) {
                    Text(
                        text       = contact.nombre.first().uppercase(),
                        color      = VioletLight,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text       = contact.nombre,
                        color      = WhiteSoft,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text     = contact.telefono,
                        color    = GrayText,
                        fontSize = 13.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint               = Color(0xFFEF4444)
                )
            }
        }
    }
}