package com.example.todolist.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.ui.adapters.TaskListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskListAdapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerTasks.adapter = taskListAdapter
        updateList()

        configureListeners()
        /** ARMAZENAR TASKS PARA NÃO PERDÊ-LAS AO SAIR DO APP E VOLTAR*/
        //DATA STORE
        //ROOM
    }

    private fun updateList() {
        val list = TaskDataSource.getList()

        if (list.isEmpty()) {
            binding.groupNoTask.visibility = View.VISIBLE
            binding.recyclerTasks.visibility = View.GONE
        } else {
            binding.groupNoTask.visibility = View.GONE
            binding.recyclerTasks.visibility = View.VISIBLE
        }
        taskListAdapter.submitList(list)
    }

    private fun configureListeners() {
        binding.fabCreateTask.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }

        taskListAdapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }

        taskListAdapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_NEW_TASK) {
            updateList()
        }
    }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }
}