package com.example.navhost1.screens

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun registrarUsoHerramienta(
    herramienta: String
) {

    val uid =
        FirebaseAuth.getInstance()
            .currentUser?.uid ?: return

    val db = FirebaseFirestore.getInstance()

    val ref =
        db.collection("usuarios")
            .document(uid)

    db.runTransaction { transaction ->

        val snapshot =
            transaction.get(ref)

        val herramientas =
            snapshot.get("herramientas.$herramienta")
                    as? Long ?: 0

        transaction.update(
            ref,
            "herramientas.$herramienta",
            herramientas + 1
        )
    }
}