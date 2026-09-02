package com.example.navhost1.habits

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

data class Habit(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val completado: Boolean = false,
    val fecha: Long = 0L,
    val xp: Int = 10,
    val generadoPorIA: Boolean = false,
    val xpOtorgado: Boolean = false,
    val fechaXP: Long = 0L
)

class HabitRepository {

    private val db =
        FirebaseFirestore.getInstance()

    fun puedeCrearHabit(
        uid: String,
        onResult: (Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .collection("habitos")
            .get()
            .addOnSuccessListener { documentos ->

                val cantidadHabitos =
                    documentos.size()

                onResult(cantidadHabitos < 7)
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }

    fun crearHabit(
        uid: String,
        nombre: String,
        categoria: String,
        descripcion: String,
        xp: Int = 10,
        generadoPorIA: Boolean = false,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val habitosRef =
            db.collection("usuarios")
                .document(uid)
                .collection("habitos")

        habitosRef
            .get()
            .addOnSuccessListener { documentos ->

                val cantidadHabitos =
                    documentos.size()

                if (cantidadHabitos >= 7) {

                    onError(
                        IllegalStateException(
                            "Has alcanzado el máximo de 7 hábitos activos."
                        )
                    )

                    return@addOnSuccessListener
                }

                val habitRef =
                    habitosRef.document()

                val habit =
                    Habit(
                        id = habitRef.id,
                        nombre = nombre,
                        categoria = categoria,
                        descripcion = descripcion,
                        completado = false,
                        fecha = System.currentTimeMillis(),
                        xp = xp,
                        generadoPorIA = generadoPorIA,
                        xpOtorgado = false,
                        fechaXP = 0L
                    )

                habitRef
                    .set(habit)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onError(error)
                    }
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }

    private fun esHoy(fecha: Long): Boolean {

        if (fecha <= 0L) {
            return false
        }

        val hoy = Calendar.getInstance()

        val fecha = Calendar.getInstance().apply {
            timeInMillis = fecha
        }

        return hoy.get(Calendar.YEAR) ==
                fecha.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) ==
                fecha.get(Calendar.DAY_OF_YEAR)
    }

    fun obtenerHabitos(
        uid: String,
        onSuccess: (List<Habit>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .collection("habitos")
            .get()
            .addOnSuccessListener { documentos ->

                val habitos =
                    documentos.map { documento ->

                        val habit =
                            documento.toObject(Habit::class.java)

                        val completadoFirebase =
                            documento.getBoolean("completado") ?: false

                        val fechaXP =
                            documento.getLong("fechaXP") ?: 0L

                        val completadoHoy =
                            completadoFirebase && esHoy(fechaXP)

                        habit.copy(
                            id = documento.id,
                            completado = completadoHoy
                        )
                    }

                onSuccess(habitos)
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }

    fun completarHabit(
        uid: String,
        habitId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val habitRef =
            db.collection("usuarios")
                .document(uid)
                .collection("habitos")
                .document(habitId)

        val userRef =
            db.collection("usuarios")
                .document(uid)

        db.runTransaction { transaction ->

            val habitSnapshot =
                transaction.get(habitRef)

            val userSnapshot =
                transaction.get(userRef)

            val fechaXP =
                habitSnapshot.getLong("fechaXP") ?: 0L

            val xpHabit =
                habitSnapshot.getLong("xp") ?: 10L

            val xpActual =
                userSnapshot.getLong("xp") ?: 0L

            val rachaHabitosActual =
                userSnapshot.getLong("rachaHabitos")?.toInt() ?: 0

            val ultimoDiaHabito =
                userSnapshot.getLong("ultimoDiaHabito") ?: 0L

            val hoy =
                Calendar.getInstance()

            val fechaUltimaXP =
                Calendar.getInstance().apply {
                    timeInMillis = fechaXP
                }

            val mismoDia =
                fechaXP > 0L &&
                        hoy.get(Calendar.YEAR) ==
                        fechaUltimaXP.get(Calendar.YEAR) &&
                        hoy.get(Calendar.DAY_OF_YEAR) ==
                        fechaUltimaXP.get(Calendar.DAY_OF_YEAR)

            val fechaUltimoHabito =
                Calendar.getInstance().apply {
                    timeInMillis = ultimoDiaHabito
                }

            val mismoDiaHabito =
                ultimoDiaHabito > 0L &&
                        hoy.get(Calendar.YEAR) ==
                        fechaUltimoHabito.get(Calendar.YEAR) &&
                        hoy.get(Calendar.DAY_OF_YEAR) ==
                        fechaUltimoHabito.get(Calendar.DAY_OF_YEAR)

            val ayer =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -1)
                }

            val ultimoFueAyer =
                ultimoDiaHabito > 0L &&
                        ayer.get(Calendar.YEAR) ==
                        fechaUltimoHabito.get(Calendar.YEAR) &&
                        ayer.get(Calendar.DAY_OF_YEAR) ==
                        fechaUltimoHabito.get(Calendar.DAY_OF_YEAR)

            /*
             * Si este hábito ya fue completado hoy,
             * no hacemos nada.
             */
            if (!mismoDia) {

                transaction.update(
                    habitRef,
                    "completado",
                    true
                )

                /*
                 * Actualizamos la racha solamente
                 * cuando todavía no se ha registrado
                 * actividad de hábitos hoy.
                 */
                if (!mismoDiaHabito) {

                    val nuevaRachaHabitos =
                        if (ultimoFueAyer) {
                            rachaHabitosActual + 1
                        } else {
                            1
                        }

                    transaction.update(
                        userRef,
                        "rachaHabitos",
                        nuevaRachaHabitos
                    )

                    transaction.update(
                        userRef,
                        "ultimoDiaHabito",
                        hoy.timeInMillis
                    )
                }

                /*
                 * El hábito recibe XP una vez por día.
                 */
                transaction.update(
                    habitRef,
                    "xpOtorgado",
                    true
                )

                transaction.update(
                    habitRef,
                    "fechaXP",
                    hoy.timeInMillis
                )

                transaction.update(
                    userRef,
                    "xp",
                    xpActual + xpHabit
                )
            }

        }.addOnSuccessListener {

            onSuccess()

        }.addOnFailureListener { error ->

            onError(error)
        }
    }
    fun descompletarHabit(
        uid: String,
        habitId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val habitRef =
            db.collection("usuarios")
                .document(uid)
                .collection("habitos")
                .document(habitId)

        val userRef =
            db.collection("usuarios")
                .document(uid)

        db.runTransaction { transaction ->

            val habitSnapshot =
                transaction.get(habitRef)

            val userSnapshot =
                transaction.get(userRef)

            val xpOtorgado =
                habitSnapshot.getBoolean("xpOtorgado") ?: false

            val xpHabit =
                habitSnapshot.getLong("xp") ?: 10L

            val xpActual =
                userSnapshot.getLong("xp") ?: 0L

            if (xpOtorgado) {

                val nuevoXp =
                    (xpActual - xpHabit).coerceAtLeast(0L)

                transaction.update(
                    userRef,
                    "xp",
                    nuevoXp
                )

                transaction.update(
                    habitRef,
                    "xpOtorgado",
                    false
                )

                transaction.update(
                    habitRef,
                    "fechaXP",
                    0L
                )
            }

            transaction.update(
                habitRef,
                "completado",
                false
            )

        }.addOnSuccessListener {

            onSuccess()

        }.addOnFailureListener { error ->

            onError(error)
        }
    }
    fun eliminarHabit(
        uid: String,
        habitId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .collection("habitos")
            .document(habitId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }

    fun editarHabit(
        uid: String,
        habitId: String,
        nombre: String,
        categoria: String,
        descripcion: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        db.collection("usuarios")
            .document(uid)
            .collection("habitos")
            .document(habitId)
            .update(
                mapOf(
                    "nombre" to nombre,
                    "categoria" to categoria,
                    "descripcion" to descripcion
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onError(error)
            }
    }

}