package com.company.crm.repository.user

import app.cash.turbine.test
import com.company.crm.data.api.ApiService
import com.company.crm.data.local.dao.TaskDao
import com.company.crm.data.model.dto.ApiResponse
import com.company.crm.data.model.dto.TaskDto
import com.company.crm.data.model.entity.TaskEntity
import com.company.crm.data.prefs.AuthState
import com.company.crm.data.prefs.UserPreferences
import com.company.crm.data.repository.user.UserTaskRepositoryImpl
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

class UserTaskRepositoryTest {

    // Моки зависимостей - создаем заглушки для всех зависимостей репозитория
    private lateinit var apiService: ApiService
    private lateinit var taskDao: TaskDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var repository: UserTaskRepositoryImpl

    // Метод, выполняемый перед каждым тестом - настраивает тестовое окружение
    @Before
    fun setUp() {
        // Создаем моки (заглушки) для всех зависимостей
        apiService = mockk()
        taskDao = mockk()
        userPreferences = mockk()
        // Создаем экземпляр тестируемого репозитория с моками зависимостей
        repository = UserTaskRepositoryImpl(apiService, taskDao, userPreferences)


        coEvery { userPreferences.getUserId() } returns 1
        coEvery { userPreferences.getRole() } returns "USER"
        every { userPreferences.authStateFlow } returns flowOf(
            AuthState.Authenticated("token", 1, "USER")
        )
    }

    // Тест проверяет, что метод observeMyTasks возвращает задачи из DAO
    @Test
    fun `observeMyTasks should return tasks from dao`() = runTest {
        // Arrange - подготовка тестовых данных
        val employeeId = 1
        val taskEntities = listOf(
            createTaskEntity(1, "Task 1", employeeId),
            createTaskEntity(2, "Task 2", employeeId)
        )

        // Настраиваем поведение моков:
        // userPreferences возвращает поток с авторизованным состоянием
        every { userPreferences.authStateFlow } returns flowOf(
            AuthState.Authenticated("token", employeeId, "ADMIN")
        )
        // taskDao возвращает поток с тестовыми задачами
        every { taskDao.observeTasksForEmployee(employeeId) } returns flowOf(taskEntities)

        // Act & Assert - выполняем действие и проверяем результат
        repository.observeMyTasks().test {
            // Ожидаем первый элемент потока
            val tasks = awaitItem()

            // Проверяем, что получено 2 задачи
            assertEquals(2, tasks.size)
            // Проверяем заголовки задач
            assertEquals("Task 1", tasks[0].title)
            assertEquals("Task 2", tasks[1].title)

            // Ожидаем завершение потока
            awaitComplete()
        }
    }

    // Тест проверяет обновление задач через API с сохранением в DAO
    @Test
    fun `refreshMyTasks should fetch from api and save to dao`() = runTest {
        // Arrange
        val employeeId = 1
        val apiTasks = listOf(
            createTaskDto(1, "Task 1"),
            createTaskDto(2, "Task 2")
        )

        // Настраиваем поведение моков для suspend функций (coEvery вместо every)
        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { apiService.getTasksForEmployee(employeeId) } returns ApiResponse(
            success = true,
            data = apiTasks,
            message = null,
            timestamp = System.currentTimeMillis() // Используем актуальное время
        )
        coEvery { taskDao.deleteByEmployeeId(employeeId) } returns Unit
        coEvery { taskDao.insertAll(any()) } returns Unit

        // Act - выполняем тестируемый метод
        repository.refreshMyTasks()

        // Assert - проверяем, что были вызваны ожидаемые методы
        coVerify {
            apiService.getTasksForEmployee(employeeId)
            taskDao.deleteByEmployeeId(employeeId)
            taskDao.insertAll(any())
        }
    }

    // Тест проверяет обработку ошибки при обновлении задач
    @Test
    fun `refreshMyTasks should throw exception when api fails`() = runTest {
        // Arrange
        val employeeId = 1

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { apiService.getTasksForEmployee(employeeId) } returns ApiResponse(
            success = false,
            data = null,
            message = "API Error",
            timestamp = System.currentTimeMillis()
        )

        // Act & Assert
        try {
            repository.refreshMyTasks()
            // Если исключение не было выброшено - тест не пройден
            fail("Expected exception")
        } catch (e: Exception) {
            // Проверяем сообщение об ошибке
            assertEquals("Failed to fetch tasks: API Error", e.message)
        }
    }

    // Тест проверяет получение задачи по ID когда пользователь имеет к ней доступ
    @Test
    fun `getMyTaskById should return task when accessible`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        val taskEntity = createTaskEntity(taskId, "Task 1", employeeId)

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns taskEntity
        // В реальной реализации проверка доступа происходит в репозитории через сравнение responsibleId/creatorId
        // Здесь просто возвращаем true, так как задача принадлежит пользователю

        // Act
        val result = repository.getMyTaskById(taskId)

        // Assert
        assertNotNull(result)
        assertEquals("Task 1", result?.title)
    }

    // Тест проверяет, что задача не возвращается когда пользователь не имеет к ней доступа
    @Test
    fun `getMyTaskById should return null when task not accessible`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        // Создаем задачу, которая принадлежит другому пользователю
        val taskEntity = createTaskEntity(taskId, "Task 1", employeeId = 2) // другой employeeId

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns taskEntity
        // Задача принадлежит другому пользователю, поэтому доступ должен быть запрещен

        // Act
        val result = repository.getMyTaskById(taskId)

        // Assert
        assertNull(result)
    }

    // Тест проверяет создание новой задачи
    @Test
    fun `createTask should call api and save to dao`() = runTest {
        // Arrange
        val employeeId = 1
        val task = createTaskDomain(1, "New Task")
        val createdTaskDto = createTaskDto(1, "New Task")

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { apiService.createTask(employeeId, any()) } returns ApiResponse(
            success = true,
            data = createdTaskDto,
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.insert(any()) } returns Unit

        // Act
        repository.createTask(task)

        // Assert
        coVerify {
            // Проверяем, что API был вызван с правильными параметрами
            apiService.createTask(employeeId, any())
            // Проверяем, что задача была сохранена в базу данных
            taskDao.insert(any())
        }
    }

    // Тест проверяет обновление существующей задачи
    @Test
    fun `updateMyTask should update task when user has access`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        val task = createTaskDomain(taskId, "Updated Task")
        val existingTaskEntity = createTaskEntity(taskId, "Original Task", employeeId)
        val updatedTaskDto = createTaskDto(taskId, "Updated Task")

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns existingTaskEntity
        coEvery { apiService.updateTask(employeeId, taskId, any()) } returns ApiResponse(
            success = true,
            data = updatedTaskDto,
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.insert(any()) } returns Unit

        // Act
        repository.updateMyTask(task)

        // Assert
        coVerify {
            apiService.updateTask(employeeId, taskId, any())
            taskDao.insert(any())
        }
    }

    // Тест проверяет, что обновление задачи запрещено когда пользователь не имеет доступа
    @Test
    fun `updateMyTask should throw exception when user has no access`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        val task = createTaskDomain(taskId, "Updated Task")
        // Задача принадлежит другому пользователю
        val existingTaskEntity = createTaskEntity(taskId, "Original Task", employeeId = 2)

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns existingTaskEntity

        // Act & Assert
        try {
            repository.updateMyTask(task)
            fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Access denied to this task", e.message)
        }
    }

    // Тест проверяет удаление задачи создателем
    @Test
    fun `deleteMyTask should delete task when user is creator`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        // Создаем задачу где пользователь является создателем
        val existingTaskEntity = createTaskEntity(taskId, "Task to delete", employeeId).copy(
            creatorId = employeeId // пользователь является создателем
        )

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns existingTaskEntity
        coEvery { apiService.deleteTask(employeeId, taskId) } returns ApiResponse(
            success = true,
            data = "Deleted",
            message = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { taskDao.deleteById(taskId) } returns Unit

        // Act
        repository.deleteMyTask(taskId)

        // Assert
        coVerify {
            apiService.deleteTask(employeeId, taskId)
            taskDao.deleteById(taskId)
        }
    }

    // Тест проверяет, что удаление задачи запрещено когда пользователь не является создателем
    @Test
    fun `deleteMyTask should throw exception when user is not creator`() = runTest {
        // Arrange
        val employeeId = 1
        val taskId = 1
        // Создаем задачу где пользователь НЕ является создателем
        val existingTaskEntity = createTaskEntity(taskId, "Task to delete", employeeId).copy(
            creatorId = 2 // создатель - другой пользователь
        )

        coEvery { userPreferences.getUserId() } returns employeeId
        coEvery { taskDao.getById(taskId) } returns existingTaskEntity

        // Act & Assert
        try {
            repository.deleteMyTask(taskId)
            fail("Expected exception")
        } catch (e: Exception) {
            assertEquals("Only task creator can delete the task", e.message)
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