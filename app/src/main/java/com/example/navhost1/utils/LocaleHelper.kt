package com.example.navhost1.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    // ── Guarda el idioma seleccionado ─────────────────────────────────────────
    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    // ── Obtiene el idioma guardado (si no hay, usa el del sistema) ────────────
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val systemLocale = Locale.getDefault().language
        // Si el idioma del sistema es soportado, úsalo como default
        val supportedLanguages = listOf("es", "en", "fr")
        val defaultLang = if (systemLocale in supportedLanguages) systemLocale else "es"
        return prefs.getString(KEY_LANGUAGE, defaultLang) ?: defaultLang
    }

    // ── Aplica el idioma al contexto ──────────────────────────────────────────
    fun applyLanguage(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    // ── Aplica el idioma guardado al arrancar ─────────────────────────────────
    fun applyCurrentLanguage(context: Context): Context {
        val savedLang = getSavedLanguage(context)
        return applyLanguage(context, savedLang)
    }

    // ── Convierte nombre de idioma a código ───────────────────────────────────
    fun getCodeFromName(languageName: String): String {
        return when {
            languageName.contains("Español", ignoreCase = true) ||
                    languageName.contains("Spanish", ignoreCase = true) ||
                    languageName.contains("Espagnol", ignoreCase = true) -> "es"

            languageName.contains("Inglés", ignoreCase = true) ||
                    languageName.contains("English", ignoreCase = true) ||
                    languageName.contains("Anglais", ignoreCase = true) -> "en"

            languageName.contains("Francés", ignoreCase = true) ||
                    languageName.contains("French", ignoreCase = true)  ||
                    languageName.contains("Français", ignoreCase = true) -> "fr"

            else -> "es"
        }
    }
}