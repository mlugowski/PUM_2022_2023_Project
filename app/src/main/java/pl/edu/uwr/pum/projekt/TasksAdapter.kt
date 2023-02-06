package pl.edu.uwr.pum.projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(private val dbHandler: DBHandler) :  RecyclerView.Adapter<TasksAdapter.TasksViewHolder>(){

    private var tasksList = dbHandler.getTasks() as MutableList<Task>
    private var sort = "-"

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        var taskDate: TextView = itemView.findViewById(R.id.taskDate)
        var taskPriority: TextView = itemView.findViewById(R.id.taskPriority)
        var taskDone: CheckBox = itemView.findViewById(R.id.taskDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tasks_item, parent, false)
        return TasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        var item = tasksList[position]
        holder.taskTitle.text = item.title
        holder.taskDate.text = "Data wykonania: " + item.date
        holder.taskPriority.text = item.priority.toString()
        holder.taskDone.isChecked = item.done


        holder.taskTitle.setOnClickListener {
            val action = FragmentTasksDirections.actionFragmentTasksToFragmentForm(item.id)
            holder.itemView.findNavController().navigate(action)
        }

        holder.taskDone.setOnClickListener {
            item.done = item.done != true
            dbHandler.updateTask(item)
        }

    }

    fun sortTasks(sortingBy: String) {
        sort = sortingBy
        when {
            (sort == "Priority") -> tasksList.sortBy {it.priority}
            (sort == "Date") -> tasksList.sortBy {it.date}
            (sort == "Alphabetically") -> tasksList.sortBy {it.title}
            (sort == "Done") -> tasksList.sortBy {it.done}

        }
    }
}