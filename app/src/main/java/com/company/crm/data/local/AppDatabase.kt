package com.company.crm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.company.crm.data.local.dao.ClientDao
import com.company.crm.data.local.dao.ConditionDao
import com.company.crm.data.local.dao.DealDao
import com.company.crm.data.local.dao.EmployeeDao
import com.company.crm.data.local.dao.MeetingDao
import com.company.crm.data.local.dao.ObjectDao
import com.company.crm.data.local.dao.PhotoDao
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.model.entity.ClientEntity
import com.company.crm.data.model.entity.ConditionEntity
import com.company.crm.data.model.entity.DealEntity
import com.company.crm.data.model.entity.EmployeeEntity
import com.company.crm.data.model.entity.MeetingEntity
import com.company.crm.data.model.entity.ObjectEntity
import com.company.crm.data.model.entity.PhotoEntity
import com.company.crm.data.model.entity.TaskEntity

@Database(entities = [TaskEntity::class, EmployeeEntity::class, ObjectEntity::class, ClientEntity::class, PhotoEntity::class, MeetingEntity::class, DealEntity::class, ConditionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun clientDao(): ClientDao
    abstract fun conditionDao(): ConditionDao
    abstract fun dealDao(): DealDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun meetingDao(): MeetingDao
    abstract fun objectDao(): ObjectDao
    abstract fun photoDao(): PhotoDao
    abstract fun taskDao(): TaskDao
}