package pl.edu.uwr.pum.projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView

class SubtasksAdapter (private val dbHandler: DBHandler, private val task: Task? = null) :  RecyclerView.Adapter<SubtasksAdapter.SubtasksViewHolder>(){

    var subtasksList: MutableList<Subtask> = if (task == null) mutableListOf<Subtask>() else dbHandler.getSubtasks(task) as MutableList<Subtask>

    inner class SubtasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var subtaskTitleInput: EditText = itemView.findViewById(R.id.subtaskTitleInput)
        var subtaskDone: CheckBox = itemView.findViewById(R.id.subtaskDone)
        var subtaskDelete: ImageButton = itemView.findViewById(R.id.deleteSubtask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_form_item, parent, false)
        return SubtasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subtasksList.size
    }

    override fun onBindViewHolder(holder: SubtasksViewHolder, position: Int) {
        val item = subtasksList[position]
        holder.subtaskTitleInput.setText(item.title)
        holder.subtaskDone.isChecked = item.done

        holder.subtaskDelete.setOnClickListener {
            if (task != null)
                dbHandler.deleteSubtask(item)
            else
                subtasksList.remove(subtasksList[position])
            reload()
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, itemCount)
        }

        holder.subtaskTitleInput.doAfterTextChanged {
            item.title = holder.subtaskTitleInput.text.toString()
            if (task != null) {
                dbHandler.updateSubtask(item)
            }
        }

        holder.subtaskDone.setOnClickListener {
            item.done = item.done == false
            if (task != null) {
                dbHandler.updateSubtask(item)
            }
        }

    }

    fun reload() {
        if (task != null)
            subtasksList =  dbHandler.getSubtasks(task) as MutableList<Subtask>
    }

}