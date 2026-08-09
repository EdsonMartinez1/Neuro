package com.example.navhost1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun EstadisticasScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var feliz by remember { mutableStateOf(0) }
    var triste by remember { mutableStateOf(0) }
    var ansioso by remember { mutableStateOf(0) }
    var enojado by remember { mutableStateOf(0) }
    var cansado by remember { mutableStateOf(0) }
    var tranquilo by remember { mutableStateOf(0) }

    var xp by remember { mutableStateOf(0L) }

    var racha by remember { mutableStateOf(0L) }

    var arbolNivel by remember { mutableStateOf(1L) }



    LaunchedEffect(Unit) {

        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener {

                xp =
                    it.getLong("xp") ?: 0
            }

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .get()
            .addOnSuccessListener { documentos ->

                db.collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener {

                        xp =
                            it.getLong("xp") ?: 0

                        racha =
                            it.getLong("racha") ?: 0

                        arbolNivel =
                            it.getLong("arbolNivel") ?: 1
                    }

                feliz = 0
                triste = 0
                ansioso = 0
                enojado = 0
                cansado = 0
                tranquilo = 0

                for (doc in documentos) {

                    when (doc.getString("emocion")) {

                        "😊 Feliz" -> feliz++
                        "😔 Triste" -> triste++
                        "😟 Ansioso" -> ansioso++
                        "😡 Enojado" -> enojado++
                        "😴 Cansado" -> cansado++
                        "😌 Tranquilo" -> tranquilo++
                    }
                }
            }
    }

    val total = feliz + triste + ansioso + enojado + cansado + tranquilo

    val porcentajeFeliz =
        if (total > 0) (feliz * 100) / total else 0

    val porcentajeTriste =
        if (total > 0) (triste * 100) / total else 0

    val porcentajeAnsioso =
        if (total > 0) (ansioso * 100) / total else 0

    val porcentajeEnojado =
        if (total > 0) (enojado * 100) / total else 0

    val porcentajeCansado =
        if (total > 0) (cansado * 100) / total else 0

    val porcentajeTranquilo =
        if (total > 0) (tranquilo * 100) / total else 0

    val bienestar = porcentajeFeliz + porcentajeTranquilo

    val estadoBienestar = when {
        bienestar >= 70 -> "🌟 Excelente"
        bienestar >= 50 -> "😊 Bueno"
        bienestar >= 30 -> "😐 Regular"
        else -> "⚠️ Necesita atención"
    }

    val emocionDominante =
        listOf(
            "😊 Feliz" to feliz,
            "😔 Triste" to triste,
            "😟 Ansioso" to ansioso,
            "😡 Enojado" to enojado,
            "😴 Cansado" to cansado,
            "😌 Tranquilo" to tranquilo
        ).maxByOrNull { it.second }
            ?.first ?: "Sin datos"

    val recomendacion = when (emocionDominante) {

        "😟 Ansioso" ->
            "Te recomendamos realizar ejercicios de respiración consciente."

        "😔 Triste" ->
            "Practica gratitud para enfocarte en aspectos positivos."

        "😡 Enojado" ->
            "Realiza una meditación guiada para recuperar la calma."

        "😴 Cansado" ->
            "Descansa y realiza una sesión breve de respiración."

        "😊 Feliz" ->
            "Sigue fortaleciendo tus hábitos positivos."

        else ->
            "Mantén tu bienestar emocional con actividades diarias."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "📊 Estadísticas Emocionales",
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "💡 Consejo de NeuraBloom",
                    fontSize = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = recomendacion,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "⭐ XP acumulada",
                    fontSize = 18.sp
                )

                Text(
                    text = "$xp",
                    fontSize = 30.sp
                )
            }
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "🔥 Racha actual",
                    fontSize = 18.sp
                )

                Text(
                    text = "$racha días",
                    fontSize = 30.sp
                )
            }
        }



        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "🌳 Árbol emocional",
                    fontSize = 18.sp
                )

                Text(
                    text = "Nivel $arbolNivel",
                    fontSize = 30.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "🧠 Nivel de Bienestar",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "🏆 Emoción dominante",
                            fontSize = 18.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = emocionDominante,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = estadoBienestar,
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📈 Distribución emocional",
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("😊 Feliz ($feliz)")
        LinearProgressIndicator(
            progress = { porcentajeFeliz / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("😔 Triste ($triste)")
        LinearProgressIndicator(
            progress = { porcentajeTriste / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("😟 Ansioso ($ansioso)")
        LinearProgressIndicator(
            progress = { porcentajeAnsioso / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("😡 Enojado ($enojado)")
        LinearProgressIndicator(
            progress = { porcentajeEnojado / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("😴 Cansado ($cansado)")
        LinearProgressIndicator(
            progress = { porcentajeCansado / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("😌 Tranquilo ($tranquilo)")
        LinearProgressIndicator(
            progress = { porcentajeTranquilo / 100f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Total de registros: $total",
            fontSize = 18.sp
        )
    }
}