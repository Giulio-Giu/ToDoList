package com.example.todolist.datasource

import com.example.todolist.model.Task

object TaskDataSource {
    private val listTask = arrayListOf<Task>()

    fun getList() = listTask.toList()

    fun insertTask(task: Task) {
        if (task.id == 0) {
            listTask.add(task.copy(id = listTask.size + 1))
        } else {
            listTask[task.id-1] = task
        }
    }

    fun findById(taskId: Int) = listTask.find { it.id == taskId }

    fun deleteTask(task: Task) {
        listTask.remove(task)
    }
}