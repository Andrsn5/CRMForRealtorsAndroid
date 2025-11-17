package com.company.crm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.company.crm.Config
import com.company.crm.data.api.ApiService
import com.company.crm.data.api.ApiServiceKtor
import com.company.crm.data.local.AppDatabase
import com.company.crm.data.local.dao.ClientDao
import com.company.crm.data.local.dao.ConditionDao
import com.company.crm.data.local.dao.DealDao
import com.company.crm.data.local.dao.EmployeeDao
import com.company.crm.data.local.dao.MeetingDao
import com.company.crm.data.local.dao.ObjectDao
import com.company.crm.data.local.dao.PhotoDao
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.prefs.UserPreferencesImpl
import com.company.crm.data.repository.admin.AdminClientRepositoryImpl
import com.company.crm.data.repository.admin.AdminConditionRepositoryImpl
import com.company.crm.data.repository.admin.AdminDealRepositoryImpl
import com.company.crm.data.repository.admin.AdminEmployeeRepositoryImpl
import com.company.crm.data.repository.admin.AdminMeetingRepositoryImpl
import com.company.crm.data.repository.admin.AdminObjectRepositoryImpl
import com.company.crm.data.repository.admin.AdminPhotoRepositoryImpl
import com.company.crm.data.repository.admin.AdminTaskRepositoryImpl
import com.company.crm.data.repository.user.UserClientRepositoryImpl
import com.company.crm.data.repository.user.UserConditionRepositoryImpl
import com.company.crm.data.repository.user.UserDealRepositoryImpl
import com.company.crm.data.repository.user.UserEmployeeRepositoryImpl
import com.company.crm.data.repository.user.UserMeetingRepositoryImpl
import com.company.crm.data.repository.user.UserObjectRepositoryImpl
import com.company.crm.data.repository.user.UserPhotoRepositoryImpl
import com.company.crm.data.repository.user.UserTaskRepositoryImpl
import com.company.crm.domain.repository.admin.AdminClientRepository
import com.company.crm.domain.repository.admin.AdminConditionRepository
import com.company.crm.domain.repository.admin.AdminDealRepository
import com.company.crm.domain.repository.admin.AdminEmployeeRepository
import com.company.crm.domain.repository.admin.AdminMeetingRepository
import com.company.crm.domain.repository.admin.AdminObjectRepository
import com.company.crm.domain.repository.admin.AdminPhotoRepository
import com.company.crm.domain.repository.admin.AdminTaskRepository
import com.company.crm.domain.repository.user.UserClientRepository
import com.company.crm.domain.repository.user.UserConditionRepository
import com.company.crm.domain.repository.user.UserDealRepository
import com.company.crm.domain.repository.user.UserEmployeeRepository
import com.company.crm.domain.repository.user.UserMeetingRepository
import com.company.crm.domain.repository.user.UserObjectRepository
import com.company.crm.domain.repository.user.UserPhotoRepository
import com.company.crm.domain.repository.user.UserTaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- DataStore (Preferences)
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { ctx.preferencesDataStoreFile("user_prefs") }
        )
    }

    // wrapper for DataStore (UserPreferences)
    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferencesImpl(dataStore)
    }

    // --- Ktor client
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = io.ktor.client.plugins.logging.LogLevel.ALL
            }

            install(HttpTimeout) {
                requestTimeoutMillis = Config.API_TIMEOUT_SECONDS * 1000
                connectTimeoutMillis = Config.API_TIMEOUT_SECONDS * 1000
                socketTimeoutMillis = Config.API_TIMEOUT_SECONDS * 1000
            }

            defaultRequest {
                url.takeFrom(URLBuilder().takeFrom(Config.BASE_URL))
                contentType(ContentType.Application.Json)
            }
        }
    }

    // --- ApiService
    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient,
        userPrefs: UserPreferences
    ): ApiService {
        return ApiServiceKtor(client, userPrefs)
    }

    // --- Room
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Config.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideClientDao(db: AppDatabase): ClientDao = db.clientDao()

    @Provides
    fun provideConditionDao(db: AppDatabase): ConditionDao = db.conditionDao()

    @Provides
    fun provideDealDao(db: AppDatabase): DealDao = db.dealDao()

    @Provides
    fun provideEmployeeDao(db: AppDatabase): EmployeeDao = db.employeeDao()

    @Provides
    fun provideMeetingDao(db: AppDatabase): MeetingDao = db.meetingDao()

    @Provides
    fun provideObjectDao(db: AppDatabase): ObjectDao = db.objectDao()

    @Provides
    fun providePhotoDao(db: AppDatabase): PhotoDao = db.photoDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    // --- Repositories
    @Provides
    @Singleton
    fun provideUserTaskRepository(
        api: ApiService,
        db: AppDatabase,
        prefs: UserPreferences
    ): UserTaskRepository = UserTaskRepositoryImpl(api, db.taskDao(), prefs)

    @Provides
    @Singleton
    fun provideAdminTaskRepository(
        api: ApiService,
        db: AppDatabase,
        prefs: UserPreferences
    ): AdminTaskRepository = AdminTaskRepositoryImpl(api, db.taskDao(), prefs)

    @Provides
    @Singleton
    fun provideUserClientRepository(
        api: ApiService,
        db: AppDatabase,
        prefs: UserPreferences
    ): UserClientRepository = UserClientRepositoryImpl(api, db.clientDao(), prefs)

    @Provides
    @Singleton
    fun provideAdminClientRepository(
        api: ApiService,
        db: AppDatabase,
        prefs: UserPreferences
    ): AdminClientRepository = AdminClientRepositoryImpl(api, db.clientDao(), prefs)

    @Provides
    @Singleton
    fun provideUserObjectRepository(
        api: ApiService,
        objectDao: ObjectDao,
        prefs: UserPreferences
    ): UserObjectRepository = UserObjectRepositoryImpl(api, objectDao, prefs)

    @Provides
    @Singleton
    fun provideAdminObjectRepository(
        api: ApiService,
        objectDao: ObjectDao,
        prefs: UserPreferences
    ): AdminObjectRepository = AdminObjectRepositoryImpl(api, objectDao, prefs)

    @Provides
    @Singleton
    fun provideUserConditionRepository(
        api: ApiService,
        conditionDao: ConditionDao,
        prefs: UserPreferences
    ): UserConditionRepository = UserConditionRepositoryImpl(api, conditionDao, prefs)

    @Provides
    @Singleton
    fun provideAdminConditionRepository(
        api: ApiService,
        conditionDao: ConditionDao,
        prefs: UserPreferences
    ): AdminConditionRepository = AdminConditionRepositoryImpl(api, conditionDao, prefs)

    @Provides
    @Singleton
    fun provideUserDealRepository(
        api: ApiService,
        dealDao: DealDao,
        prefs: UserPreferences
    ): UserDealRepository = UserDealRepositoryImpl(api, dealDao, prefs)

    @Provides
    @Singleton
    fun provideAdminDealRepository(
        api: ApiService,
        dealDao: DealDao,
        prefs: UserPreferences
    ): AdminDealRepository = AdminDealRepositoryImpl(api, dealDao, prefs)

    @Provides
    @Singleton
    fun provideUserMeetingRepository(
        api: ApiService,
        meetingDao: MeetingDao,
        prefs: UserPreferences
    ): UserMeetingRepository = UserMeetingRepositoryImpl(api, meetingDao, prefs)

    @Provides
    @Singleton
    fun provideAdminMeetingRepository(
        api: ApiService,
        meetingDao: MeetingDao,
        prefs: UserPreferences
    ): AdminMeetingRepository = AdminMeetingRepositoryImpl(api, meetingDao, prefs)

    @Provides
    @Singleton
    fun provideUserEmployeeRepository(
        api: ApiService,
        employeeDao: EmployeeDao,
        prefs: UserPreferences
    ): UserEmployeeRepository = UserEmployeeRepositoryImpl(api, employeeDao, prefs)

    @Provides
    @Singleton
    fun provideAdminEmployeeRepository(
        api: ApiService,
        employeeDao: EmployeeDao,
        prefs: UserPreferences
    ): AdminEmployeeRepository = AdminEmployeeRepositoryImpl(api, employeeDao, prefs)


    @Provides
    @Singleton
    fun provideUserPhotoRepository(
        api: ApiService,
        photoDao: PhotoDao,
        prefs: UserPreferences
    ): UserPhotoRepository = UserPhotoRepositoryImpl(api, photoDao, prefs)

    @Provides
    @Singleton
    fun provideAdminPhotoRepository(
        api: ApiService,
        photoDao: PhotoDao,
        prefs: UserPreferences
    ): AdminPhotoRepository = AdminPhotoRepositoryImpl(api, photoDao, prefs)

}