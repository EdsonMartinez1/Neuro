package com.example.navhost1.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RecomendacionesScreen() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var emocionDominante by remember {
        mutableStateOf("Analizando...")
    }

    var recomendacion by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("diarios")
            .document(uid)
            .collection("entradas")
            .get()
            .addOnSuccessListener { documentos ->

                var feliz = 0
                var triste = 0
                var ansioso = 0
                var enojado = 0
                var cansado = 0
                var tranquilo = 0

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

                val mayor = maxOf(
                    feliz,
                    triste,
                    ansioso,
                    enojado,
                    cansado,
                    tranquilo
                )

                when (mayor) {

                    feliz -> {
                        emocionDominante = "😊 Feliz"
                        recomendacion =
                            "Sigue realizando actividades que te generen bienestar."
                    }

                    triste -> {
                        emocionDominante = "😔 Triste"
                        recomendacion =
                            "Habla con alguien de confianza y realiza actividades que disfrutes."
                    }

                    ansioso -> {
                        emocionDominante = "😟 Ansioso"
                        recomendacion =
                            "Practica respiraciones profundas durante 5 minutos."
                    }

                    enojado -> {
                        emocionDominante = "😡 Enojado"
                        recomendacion =
                            "Toma un descanso antes de reaccionar y realiza actividad física."
                    }

                    cansado -> {
                        emocionDominante = "😴 Cansado"
                        recomendacion =
                            "Prioriza el descanso y mantente hidratado."
                    }

                    tranquilo -> {
                        emocionDominante = "😌 Tranquilo"
                        recomendacion =
                            "Mantén tus hábitos saludables y tu rutina emocional."
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "💡 Recomendaciones",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Emoción predominante",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = emocionDominante,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = recomendacion,
                    fontSize = 18.sp
                )
            }
        }
    }
}