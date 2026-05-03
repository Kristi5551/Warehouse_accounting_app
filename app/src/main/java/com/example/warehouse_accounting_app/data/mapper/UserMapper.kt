package com.example.warehouse_accounting_app.data.mapper

import com.example.warehouse_accounting_app.data.remote.dto.response.UserBriefResponseDto
import com.example.warehouse_accounting_app.data.remote.dto.response.UserResponseDto
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserPick
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

fun UserBriefResponseDto.toUserPick(): UserPick = UserPick(id = id, fullName = fullName)
