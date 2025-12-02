package com.company.crm.repository.admin

import app.cash.turbine.test
import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.model.dto.ApiResponse
import com.company.crm.data.model.dto.TaskDto
import com.company.crm.data.model.entity.TaskEntity
import com.company.crm.data.prefs.AuthState
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.admin.AdminTaskRepositoryImpl
import com.company.crm.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AdminTaskRepositoryTest {

    // Моки зависимостей - создаем заглушки для всех зависимостей репозитория администратора
    private lateinit var apiService: ApiService
    private lateinit var taskDao: TaskDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var repository: AdminTaskRepositoryImpl

    // Метод, выполняемый перед каждым тестом - настраивает тестовое окружение
    @Before
    fun setUp() {
        // Создаем моки (заглушки) для всех зависимостей
        apiService = mockk()
        taskDao = mockk()
        userPreferences = mockk()
        // Создаем экземпляр тестируемого репозитория администратора с моками зависимостей
        repository = AdminTaskRepositoryImpl(apiService, taskDao, userPreferences)
    }
    @Test
    fun `observeAllTasks should throw exception for non-admin`() = runTest {
        // Arrange
        every { userPreferences.authStateFlow } returns flowOf(
            AuthState.Authenticated("token", 1, "EMPLOYEE") // не админ!
        )

        // Act & Assert
        repository.observeAllTasks().test {
            val exception = awaitError()
            assertTrue(exception is SecurityException)
        }
    }

    // Тест проверяет, что метод observeAllTasks возвращает все задачи из DAO для администратора
    @Test
    fun `observeAllTasks should return all tasks from dao for admin`() = runTest {
        // Arrange - подготовка тестовых данных
        val adminId = 1
        val taskEntities = listOf(
            createTaskEntity(1, "Task 1", 1),
            createTaskEntity(2, "Task 2", 2), // задача другого пользователя
            createTaskEntity(3, "Task 3", 3)  // задача третьего пользователя
        )

        // Настраиваем поведение моков:
        // userPreferences возвращает поток с авторизованным состоянием администратора
        every { userPreferences.authStateFlow } returns flowOf(
            AuthState.Authenticated("token", adminId, "ADMIN") // роль ADMIN
        )
        // taskDao возвращает поток со всеми задачами (независимо от пользователя)
        every { taskDao.observeAll() } returns flowOf(taskEntities)

        // Act & Assert - выполняем действие и проверяем результат
        repository.observeAllTasks().test {
            // Ожидаем первый элемент потока
            val tasks = awaitItem()

            // Проверяем, что получены все 3 задачи (админ видит все)
            assertEquals(3, tasks.size)
            // Проверяем заголовки задач
            assertEquals("Task 1", tasks[0].title)
            assertEquals("Task 2", tasks[1].title)
            assertEquals("Task 3", tasks[2].title)

            // Ожидаем завершение потока
            awaitComplete()
        }
    }

    @Test
    fun `observeAllTasks should throw exception when user is not admin`() = runTest {
        // Arrange
        val employeeId = 1

        // Настраиваем мок для возврата роли обычного пользователя
        every { userPreferences.authStateFlow } returns flowOf(
            AuthState.Authenticated("token", employeeId, "EMPLOYEE") // роль EMPLOYEE, не ADMIN
        )

        // Act & Assert
        repository.observeAllTasks().test {
            // Ожидаем исключение как событие в Flow
            val exception = awaitError()
            assertTrue(exception.message?.contains("admin", ignoreCase = true) == true)
        }
    }

    // Тест проверяет обновление всех задач через API с сохранением в DAO для администратора
    @Test
    fun `refreshAllTasks should fetch all tasks from api and save to dao for admin`() = runTest {
        // Arrange
        val adminId = 1
        val apiTasks = listOf(
            createTaskDto(1, "Task 1"),
            createTaskDto(2, "Task 2"),
            createTaskDto(3, "Task 3")
        )

        // Настраиваем поведение моков для suspend функций
        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN" // подтверждаем роль администратора
        coEvery { apiService.getAllTasks(adminId) } returns ApiResponse(
            success = true,
            data = apiTasks,
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.clearAll() } returns Unit // администратор очищает всю таблицу
        coEvery { taskDao.insertAll(any()) } returns Unit

        // Act - выполняем тестируемый метод
        repository.refreshAllTasks()

        // Assert - проверяем, что были вызваны ожидаемые методы
        coVerify {
            apiService.getAllTasks(adminId)
            taskDao.clearAll() // администратор очищает ВСЕ задачи, а не только свои
            taskDao.insertAll(any())
        }
    }

    // Тест проверяет обработку ошибки при обновлении всех задач
    @Test
    fun `refreshAllTasks should throw exception when api fails`() = runTest {
        // Arrange
        val adminId = 1

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.getAllTasks(adminId) } returns ApiResponse(
            success = false,
            data = null,
            message = "API Error",
            timestamp = System.currentTimeMillis()
        )

        // Act & Assert
        try {
            repository.refreshAllTasks()
            // Если исключение не было выброшено - тест не пройден
            fail("Expected exception")
        } catch (e: Exception) {
            // Проверяем сообщение об ошибке
            assertEquals("Failed to fetch all tasks: API Error", e.message)
        }
    }

    // Тест проверяет получение задачи по ID администратором (без проверки прав доступа)
    @Test
    fun `getTaskById should return any task for admin`() = runTest {
        // Arrange
        val adminId = 1
        val taskId = 1
        // Администратор может получить ЛЮБУЮ задачу, даже не свою
        val taskEntity = createTaskEntity(taskId, "Task 1", employeeId = 2) // задача другого пользователя

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { taskDao.getById(taskId) } returns taskEntity

        // Act
        val result = repository.getTaskById(taskId)

        // Assert
        assertNotNull(result)
        assertEquals("Task 1", result?.title)
        // Администратор получает задачу даже если она принадлежит другому пользователю
    }

    // Тест проверяет, что getTaskById возвращает null когда задача не найдена
    @Test
    fun `getTaskById should return null when task not found`() = runTest {
        // Arrange
        val adminId = 1
        val taskId = 999 // несуществующий ID

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { taskDao.getById(taskId) } returns null

        // Act
        val result = repository.getTaskById(taskId)

        // Assert
        assertNull(result)
    }

    // Тест проверяет создание новой задачи администратором
    @Test
    fun `upsertTask should create new task when id is zero`() = runTest {
        // Arrange
        val adminId = 1
        val newTask = createTaskDomain(0, "New Task") // ID = 0 означает новую задачу
        val createdTaskDto = createTaskDto(1, "New Task")

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.createTask(adminId, any()) } returns ApiResponse(
            success = true,
            data = createdTaskDto,
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.insert(any()) } returns Unit

        // Act
        repository.upsertTask(newTask)

        // Assert
        coVerify {
            // Проверяем, что был вызван метод создания (не обновления)
            apiService.createTask(adminId, any())
            taskDao.insert(any())
        }
    }

    // Тест проверяет обновление существующей задачи администратором
    @Test
    fun `upsertTask should update existing task when id is not zero`() = runTest {
        // Arrange
        val adminId = 1
        val existingTask = createTaskDomain(1, "Updated Task") // ID ≠ 0 означает существующую задачу
        val updatedTaskDto = createTaskDto(1, "Updated Task")

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.updateTask(adminId, existingTask.id, any()) } returns ApiResponse(
            success = true,
            data = updatedTaskDto,
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.insert(any()) } returns Unit

        // Act
        repository.upsertTask(existingTask)

        // Assert
        coVerify {
            // Проверяем, что был вызван метод обновления (не создания)
            apiService.updateTask(adminId, existingTask.id, any())
            taskDao.insert(any())
        }
    }

    // Тест проверяет обработку ошибки при создании/обновлении задачи
    @Test
    fun `upsertTask should throw exception when api fails`() = runTest {
        // Arrange
        val adminId = 1
        val task = createTaskDomain(1, "Task")

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.updateTask(adminId, task.id, any()) } returns ApiResponse(
            success = false,
            data = null,
            message = "Update failed",
            timestamp = System.currentTimeMillis()
        )

        // Act & Assert
        try {
            repository.upsertTask(task)
            fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Failed to upsert task: Update failed", e.message)
        }
    }

    // Тест проверяет удаление задачи администратором
    @Test
    fun `deleteTask should delete any task for admin`() = runTest {
        // Arrange
        val adminId = 1
        val taskId = 1

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.deleteTask(adminId, taskId) } returns ApiResponse(
            success = true,
            data = "Deleted",
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.deleteById(taskId) } returns Unit

        // Act
        repository.deleteTask(taskId)

        // Assert
        coVerify {
            apiService.deleteTask(adminId, taskId)
            taskDao.deleteById(taskId)
        }
    }

    // Тест проверяет обработку ошибки при удалении задачи
    @Test
    fun `deleteTask should throw exception when api fails`() = runTest {
        // Arrange
        val adminId = 1
        val taskId = 1

        coEvery { userPreferences.getUserId() } returns adminId
        coEvery { userPreferences.getRole() } returns "ADMIN"
        coEvery { apiService.deleteTask(adminId, taskId) } returns ApiResponse(
            success = false,
            data = null,
            message = "Delete failed",
            timestamp = System.currentTimeMillis()
        )

        // Act & Assert
        try {
            repository.deleteTask(taskId)
            fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Failed to delete task: Delete failed", e.message)
        }
    }

    // Вспомогательная функция для создания тестовой TaskEntity
    private fun createTaskEntity(id: Int, title: String, employeeId: Int): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = "Description",
            status = "PENDING",
            priority = "MEDIUM",
            responsibleId = employeeId,
            creatorId = employeeId,
            dueDate = "2024-01-01",
            updatedAt = System.currentTimeMillis()
        )
    }

    // Вспомогательная функция для создания тестового TaskDto
    private fun createTaskDto(id: Int, title: String): TaskDto {
        return TaskDto(
            id = id,
            title = title,
            description = "Description",
            status = "PENDING",
            priority = "MEDIUM",
            responsibleId = 1,
            creatorId = 1,
            dueDate = "2024-01-01"
        )
    }

    // Вспомогательная функция для создания тестового доменного объекта Task
    private fun createTaskDomain(id: Int, title: String): Task {
        return Task(
            id = id,
            title = title,
            description = "Description",
            status = "PENDING",
            priority = "MEDIUM",
            responsibleId = 1,
            creatorId = 1,
            dueDate = "2024-01-01",
        )
    }
}