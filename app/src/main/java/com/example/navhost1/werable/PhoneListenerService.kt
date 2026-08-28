package com.example.navhost1.werable

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.navhost1.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class PhoneListenerService : WearableListenerService() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e("WEAR", "Usuario no autenticado — ignorando mensaje del reloj")
            return
        }

        val baseRef = db.collection("usuarios").document(uid).collection("wearable")

        when (messageEvent.path) {

            "/session" -> {
                val json = JSONObject(String(messageEvent.data))
                val data = hashMapOf(
                    "modo"        to json.getString("mode"),
                    "patron"      to json.getString("pattern"),
                    "duracion"    to json.getInt("duration"),
                    "feedback"    to json.getString("feedback"),
                    "puntaje"     to json.getInt("score"),
                    "timestamp"   to json.getLong("timestamp"),
                    "fecha"       to com.google.firebase.Timestamp.now()
                )
                baseRef.document("sesiones_respiracion")
                    .collection("registros")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("WEAR", "Sesión de respiración guardada en Firebase")
                    }
                    .addOnFailureListener {
                        Log.e("WEAR", "Error guardando sesión: ${it.message}")
                    }
            }

            "/biometric" -> {
                val json = JSONObject(String(messageEvent.data))
                val data = hashMapOf(
                    "tipo"        to json.getString("typeName"),
                    "valor"       to json.getDouble("value"),
                    "unidad"      to json.getString("unit"),
                    "rango"       to json.getString("range"),
                    "timestamp"   to json.getLong("timestamp"),
                    "fecha"       to com.google.firebase.Timestamp.now()
                )
                baseRef.document("biometria")
                    .collection("registros")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("WEAR", "Biometría guardada en Firebase")
                    }
                    .addOnFailureListener {
                        Log.e("WEAR", "Error guardando biometría: ${it.message}")
                    }
            }

            "/sos" -> {
                val json = JSONObject(String(messageEvent.data))
                val data = hashMapOf(
                    "numero"      to json.getString("number"),
                    "timestamp"   to json.getLong("timestamp"),
                    "fecha"       to com.google.firebase.Timestamp.now()
                )
                baseRef.document("alertas_sos")
                    .collection("registros")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("WEAR", "Alerta SOS guardada en Firebase")
                    }
                    .addOnFailureListener {
                        Log.e("WEAR", "Error guardando SOS: ${it.message}")
                    }
            }

            "/crisis" -> {
                val json = JSONObject(String(messageEvent.data))
                val data = hashMapOf(
                    "fcMaxima"    to json.getDouble("peakBpm"),
                    "duracion"    to json.getInt("duration"),
                    "trigger"     to json.getString("trigger"),
                    "timestamp"   to json.getLong("timestamp"),
                    "fecha"       to com.google.firebase.Timestamp.now()
                )
                baseRef.document("crisis")
                    .collection("registros")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("WEAR", "Crisis guardada en Firebase")
                    }
                    .addOnFailureListener {
                        Log.e("WEAR", "Error guardando crisis: ${it.message}")
                    }

                // Notificación en el celular
                showCrisisNotification(this, json.getDouble("peakBpm").toInt(), json.getString("trigger"))
            }



            "/call" -> {
                val phone  = String(messageEvent.data)
                Log.d("WEAR", "Reloj pide llamar a: $phone")
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data  = android.net.Uri.parse("tel:$phone")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }

            "/anomaly" -> {
                val json = JSONObject(String(messageEvent.data))
                val data = hashMapOf(
                    "fc"          to json.getDouble("bpm"),
                    "rango"       to json.getString("range"),
                    "timestamp"   to json.getLong("timestamp"),
                    "fecha"       to com.google.firebase.Timestamp.now()
                )
                baseRef.document("biometria")
                    .collection("anomalias")
                    .add(data)
                    .addOnSuccessListener {
                        Log.d("WEAR", "Anomalía guardada en Firebase")
                    }
            }
            "/request-contacts" -> {
                Log.d("WEAR", "Reloj solicita contactos")
                val uid = auth.currentUser?.uid ?: return

                db.collection("usuarios")
                    .document(uid)
                    .collection("wearable")
                    .document("contactos")
                    .collection("lista")
                    .get()
                    .addOnSuccessListener { docs ->
                        val contactsArray = org.json.JSONArray()

                        docs.forEach { doc ->
                            val contact = org.json.JSONObject().apply {
                                put("name",  doc.getString("nombre")   ?: "")
                                put("phone", doc.getString("telefono") ?: "")
                            }
                            contactsArray.put(contact)
                        }

                        val responseData = contactsArray.toString().toByteArray()
                        val nodeId       = messageEvent.sourceNodeId

                        com.google.android.gms.wearable.Wearable
                            .getMessageClient(this)
                            .sendMessage(nodeId, "/contacts-response", responseData)
                            .addOnSuccessListener {
                                Log.d("WEAR", "Contactos enviados al reloj: ${docs.size()}")
                            }
                            .addOnFailureListener {
                                Log.e("WEAR", "Error enviando contactos: ${it.message}")
                            }
                    }
                    .addOnFailureListener {
                        Log.e("WEAR", "Error leyendo Firebase: ${it.message}")
                    }
            }
        }
    }

    private fun showCrisisNotification(context: Context, bpm: Int, trigger: String) {
        val channelId = "crisis_phone"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de crisis",
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }

        // Cambia MainActivity por la Activity principal de tu proyecto real
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta de crisis detectada")
            .setContentText("FC: $bpm bpm · $trigger")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val nm = context.getSystemService(NotificationManager::class.java)
        nm.notify(8001, notification)
    }
}