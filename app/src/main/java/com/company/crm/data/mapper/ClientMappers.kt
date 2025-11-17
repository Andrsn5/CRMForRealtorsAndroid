package com.company.crm.data.mapper

import com.company.crm.data.model.dto.ClientDto
import com.company.crm.data.model.entity.ClientEntity
import com.company.crm.domain.model.Client
import com.google.android.gms.common.api.Api

fun ClientEntity.toDomain(): Client = Client(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    phone = this.phone,
    clientType = this.clientType,
    budget = this.budget
)

fun ClientDto.toEntity(): ClientEntity = ClientEntity(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    phone = this.phone,
    clientType = this.clientType,
    budget = this.budget,
    updatedAt = System.currentTimeMillis()
)

fun Client.toDto(): ClientDto = ClientDto(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    phone = this.phone,
    clientType = this.clientType,
    budget = this.budget
)