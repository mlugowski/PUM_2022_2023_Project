package pl.edu.uwr.pum.projekt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime
import java.util.*

class FragmentForm: Fragment() {
    private lateinit var dbHandler: DBHandler
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DBHandler(requireActivity())

        arguments?.let {
            taskId = it.getInt("taskId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskTitle: TextInputEditText = view.findViewById(R.id.taskFormTitleInput)
        val subtasksRV: RecyclerView = view.findViewById(R.id.subtasksFormRV)
        val taskDone: CheckBox = view.findViewById(R.id.taskDoneForm)
        val taskDescription: TextInputEditText = view.findViewById(R.id.taskDescriptionFormInput)
        val taskDate: TextView = view.findViewById(R.id.taskDateText)
        val taskDateCalendarButton: ImageButton = view.findViewById(R.id.taskDateCalendar)
        val taskReminder: TextView = view.findViewById(R.id.taskReminderText)
        val taskReminderCalendarButton: ImageButton = view.findViewById(R.id.taskReminderCalendar)
        val addSubtaskButton: FloatingActionButton = view.findViewById(R.id.subtaskAddForm)
        val taskDeleteButton: ImageButton = view.findViewById(R.id.taskDeleteButton)
        val taskAddButton: Button = view.findViewById(R.id.taskAddButton)
        val priorityInput: EditText = view.findViewById(R.id.priorityInput)

        var dateTime: LocalDateTime = LocalDateTime.now()
        taskDate.text = dateTime.toString()

        taskDateCalendarButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    dateTime = LocalDateTime.ofInstant(pickedDateTime.toInstant(), pickedDateTime.getTimeZone().toZoneId())
                    taskDate.text = dateTime.toString()
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }

        var reminderDateTime: LocalDateTime = LocalDateTime.now()
        taskReminder.text = reminderDateTime.toString()

        taskReminderCalendarButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
                TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    reminderDateTime = LocalDateTime.ofInstant(pickedDateTime.toInstant(), pickedDateTime.getTimeZone().toZoneId())
                    taskReminder.text = reminderDateTime.toString()
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }

        if (taskId == 0) {
            taskDeleteButton.visibility = View.INVISIBLE

            subtasksRV.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SubtasksAdapter(dbHandler)
            }

            val adapter = subtasksRV.adapter as SubtasksAdapter
            addSubtaskButton.setOnClickListener {
                adapter.subtasksList.add(Subtask("", false, 0)) // change taskid
                adapter.notifyDataSetChanged()
            }

            taskAddButton.setOnClickListener {
                val title = taskTitle.text.toString()
                val date = dateTime
                val reminder = reminderDateTime
                val priority = priorityInput.text.toString().toInt()
                val done = taskDone.isChecked
                val description = taskDescription.text.toString()
                val newTask: Task = Task(title, date, reminder, priority, done, description)
                dbHandler.addTask(newTask)
                val taskId = dbHandler.getTasks().last().id
                for (subtask: Subtask in adapter.subtasksList) {
                    subtask.taskId = taskId
                    println(subtask.title)
                    dbHandler.addSubtask(subtask)
                }

                findNavController().popBackStack()
            }


        } else {

            taskAddButton.text = "SAVE"
            var currentTask: Task = dbHandler.getTask(taskId)
            taskTitle.setText(currentTask.title)
            taskDone.isChecked = currentTask.done
            taskDescription.setText(currentTask.description)
            taskDate.text = currentTask.date.toString()
            taskReminder.text = currentTask.reminder.toString()
            priorityInput.setText(currentTask.priority.toString())

            dateTime = currentTask.date
            reminderDateTime = currentTask.reminder



            subtasksRV.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SubtasksAdapter(dbHandler, currentTask)
            }

            val adapter = subtasksRV.adapter as SubtasksAdapter
            addSubtaskButton.setOnClickListener {
                dbHandler.addSubtask(Subtask("", false, currentTask.id))
                adapter.reload()
                adapter.notifyDataSetChanged()
            }
            taskAddButton.setOnClickListener {
                currentTask.title = taskTitle.text.toString()
                currentTask.date = dateTime
                currentTask.reminder = reminderDateTime
                currentTask.priority = priorityInput.text.toString().toInt()
                currentTask.done = taskDone.isChecked
                currentTask.description = taskDescription.text.toString()
                dbHandler.updateTask(currentTask)
                findNavController().popBackStack()
            }

            taskDeleteButton.setOnClickListener {
                dbHandler.deleteSubtasks(currentTask)
                dbHandler.deleteTask(currentTask)
                findNavController().popBackStack()
            }


        }

    }


    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }
}