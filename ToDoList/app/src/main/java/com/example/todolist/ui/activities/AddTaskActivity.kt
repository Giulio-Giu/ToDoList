package com.example.todolist.ui.activities

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.extensions.*
import com.example.todolist.model.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilAddTaskTitle.text = it.title
                binding.tilAddTaskDescription.text = it.description
                binding.tilAddTaskDate.text = it.date
                binding.tilAddTaskHour.text = it.hour
            }
        }

        configureListeners()
    }

    private fun configureListeners() {
        binding.tilAddTaskDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.apply {
                addOnPositiveButtonClickListener {
                    val timeZone = TimeZone.getDefault()
                    val offSet = timeZone.getOffset(Date().time) * -1
                    binding.tilAddTaskDate.text = Date(it + offSet).formatDate()
                }
            }.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilAddTaskHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().apply {
                setTimeFormat(TimeFormat.CLOCK_24H)
            }.build()

            timePicker.apply {
                addOnPositiveButtonClickListener {
                    val hourFormat =
                        if (timePicker.hour < 10) "0${timePicker.hour}" else "${timePicker.hour}"
                    val minuteFormat =
                        if (timePicker.minute < 10) "0${timePicker.minute}" else "${timePicker.minute}"

                    binding.tilAddTaskHour.text = "$hourFormat:$minuteFormat"
                }
            }.show(supportFragmentManager, "TIME_PICKER_TAG")
        }

        binding.mbtnAddTaskCancel.setOnClickListener {
            finish()
        }

        binding.mbtnAddTaskCreate.setOnClickListener {
            validateFields()?.let {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_title_error)
                    .setMessage(it)
                    .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } ?: run {
                val newTask = Task(
                    title = binding.tilAddTaskTitle.text,
                    description = binding.tilAddTaskDescription.text,
                    date = binding.tilAddTaskDate.text,
                    hour = binding.tilAddTaskHour.text,
                    id = intent.getIntExtra(TASK_ID, 0)
                )

                TaskDataSource.insertTask(newTask)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun validateFields(): String? {
        return when {
            binding.tilAddTaskTitle.text.isEmpty() -> resources.getString(R.string.add_task_message_title_required)
            binding.tilAddTaskDescription.text.isEmpty() -> resources.getString(R.string.add_task_message_description_required)
            binding.tilAddTaskDate.text.isEmpty() -> resources.getString(R.string.add_task_message_date_required)
            binding.tilAddTaskHour.text.isEmpty() -> resources.getString(R.string.add_task_message_hour_required)
            else -> null
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}