package com.example.cityreporter.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Date.formatToString(pattern: String = "dd.MM.yyyy HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^\\+?[0-9]{9,15}$"))
}

fun String.isValidPassword(): Boolean {
    // Minimum 8 znaków, co najmniej jedna wielka litera, jedna mała, jedna cyfra i jeden znak specjalny
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}$"
    return this.matches(Regex(passwordRegex))
}

fun String.getPasswordRequirements(): List<Pair<String, Boolean>> {
    return listOf(
        "Minimum 8 znaków" to (this.length >= 8),
        "Co najmniej jedna wielka litera" to this.contains(Regex("[A-Z]")),
        "Co najmniej jedna mała litera" to this.contains(Regex("[a-z]")),
        "Co najmniej jedna cyfra" to this.contains(Regex("\\d")),
        "Co najmniej jeden znak specjalny (@\$!%*?&#)" to this.contains(Regex("[@\$!%*?&#]"))
    )
}