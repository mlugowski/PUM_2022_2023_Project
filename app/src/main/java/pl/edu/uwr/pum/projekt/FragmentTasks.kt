package pl.edu.uwr.pum.projekt

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentTasks : Fragment() {
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DBHandler(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasksRV: RecyclerView = view.findViewById(R.id.tasksRV)
        tasksRV.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TasksAdapter(dbHandler)
        }
        val sortTasksButton: FloatingActionButton = view.findViewById(R.id.sortTasks)

        sortTasksButton.setOnClickListener {
            val items = arrayOf("Priority", "Date", "Alphabetically", "Done")
            val builder = AlertDialog.Builder(requireActivity())
            with (builder) {
                setTitle("Sort by")
                setItems(items) {
                    dialog, which ->
                    val adapter = tasksRV.adapter as TasksAdapter
                    adapter.sortTasks(items[which])
                    tasksRV.adapter?.notifyDataSetChanged()
                }
                show()
            }

        }
    }

    override fun onDestroy() {
        dbHandler.close()
        super.onDestroy()
    }

}