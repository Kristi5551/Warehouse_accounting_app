package com.example.warehouse_accounting_app.core.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeFormatters {
    val isoLocal: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun parseIso(value: String): LocalDateTime = LocalDateTime.parse(value, isoLocal)
}
