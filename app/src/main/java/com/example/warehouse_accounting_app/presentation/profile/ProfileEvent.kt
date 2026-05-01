package com.example.warehouse_accounting_app.presentation.profile

sealed interface ProfileEvent {
    data object LoadUser : ProfileEvent
}
