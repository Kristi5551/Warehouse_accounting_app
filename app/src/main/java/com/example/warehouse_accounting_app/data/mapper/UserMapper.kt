package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.UserResponseDto
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.model.UserStatus

fun UserResponseDto.toDomain(): User =
    User(
        id = id,
        email = email,
        fullName = fullName,
        role = UserRole.valueOf(role),
        status = UserStatus.valueOf(status),
    )
