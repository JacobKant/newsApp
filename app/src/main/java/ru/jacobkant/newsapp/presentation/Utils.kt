package ru.jacobkant.newsapp.presentation

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

object DateFormat {
    const val DEFAULT = "dd.MM.yyyy HH:mm"
}

fun LocalDateTime.toStringFormat(
    pattern: String = DateFormat.DEFAULT,
    locale: Locale = Locale.getDefault()
): String = this.format(DateTimeFormatter.ofPattern(pattern).withLocale(locale))