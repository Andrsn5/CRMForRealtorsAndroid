package com.company.crm.data.mapper

import com.company.crm.data.model.dto.EmployeeDto
import com.company.crm.data.model.entity.EmployeeEntity
import com.company.crm.domain.model.Employee

fun EmployeeEntity.toDomain(): Employee = Employee(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    position = this.position,
    role = this.role
)

fun EmployeeDto.toEntity(): EmployeeEntity = EmployeeEntity(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    position = this.position,
    role = this.role,
    updatedAt = System.currentTimeMillis()
)

fun Employee.toDto(): EmployeeDto = EmployeeDto(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    position = this.position,
    role = this.role
)