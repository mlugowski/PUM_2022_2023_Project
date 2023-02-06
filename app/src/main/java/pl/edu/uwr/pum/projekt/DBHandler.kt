package pl.edu.uwr.pum.projekt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime

class DBHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Tasks.db"
        private const val TABLE_TASKS = "TasksTable"
        private const val TABLE_SUBTASKS = "SubtasksTable"

        private const val COLUMN_ID_TASKS = "_id"
        private const val COLUMN_TITLE_TASKS = "title"
        private const val COLUMN_DATE_TASKS = "date"
        private const val COLUMN_REMINDER_TASKS = "reminder"
        private const val COLUMN_PRIORITY_TASKS = "priority"
        private const val COLUMN_DONE_TASKS = "done"
        private const val COLUMN_DESCRIPTION_TASKS = "description"

        private const val COLUMN_ID_SUBTASKS = "_id"
        private const val COLUMN_TITLE_SUBTASKS = "title"
        private const val COLUMN_DONE_SUBTASKS = "done"
        private const val COLUMN_TASKID_SUBTASKS = "taskId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TASKS_TABLE =
            "CREATE TABLE $TABLE_TASKS(" +
                    "$COLUMN_ID_TASKS INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_TITLE_TASKS TEXT," +
                    "$COLUMN_DATE_TASKS TEXT," +
                    "$COLUMN_REMINDER_TASKS TEXT," +
                    "$COLUMN_PRIORITY_TASKS INTEGER," +
                    "$COLUMN_DONE_TASKS INTEGER," +
                    "$COLUMN_DESCRIPTION_TASKS TEXT)"
        db?.execSQL(CREATE_TASKS_TABLE)
        val CREATE_SUBTASKS_TABLE =
            "CREATE TABLE $TABLE_SUBTASKS(" +
                    "$COLUMN_ID_SUBTASKS INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "$COLUMN_TITLE_SUBTASKS TEXT," +
                    "$COLUMN_DONE_SUBTASKS INTEGER," +
                    "$COLUMN_TASKID_SUBTASKS INTEGER)"
        db?.execSQL(CREATE_SUBTASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SUBTASKS")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE_TASKS, task.title)
        contentValues.put(COLUMN_DATE_TASKS, task.date.toString())
        contentValues.put(COLUMN_REMINDER_TASKS, task.reminder.toString())
        contentValues.put(COLUMN_PRIORITY_TASKS, task.priority)
        contentValues.put(COLUMN_DONE_TASKS, task.done)
        contentValues.put(COLUMN_DESCRIPTION_TASKS, task.description)
        db.insert(TABLE_TASKS, null, contentValues)
        db.close()
    }

    fun deleteTask(task: Task) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID_TASKS=${task.id}", null)
        db.close()
    }

    fun addSubtask(subtask: Subtask) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE_SUBTASKS, subtask.title)
        contentValues.put(COLUMN_DONE_SUBTASKS, subtask.done)
        contentValues.put(COLUMN_TASKID_SUBTASKS, subtask.taskId)
        db.insert(TABLE_SUBTASKS, null, contentValues)
        db.close()
    }

    fun deleteSubtask(subtask: Subtask) {
        val db = this.writableDatabase
        db.delete(TABLE_SUBTASKS, "$COLUMN_ID_TASKS=${subtask.id}", null)
        db.close()
    }

    fun deleteSubtasks(task: Task) {
        val db = this.writableDatabase
        db.delete(TABLE_SUBTASKS, "$COLUMN_TASKID_SUBTASKS=${task.id}", null)
        db.close()
    }

    fun getTasks() : List<Task> {
        val tasks: MutableList<Task> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS", null)
        if (cursor.moveToFirst()) {
            do {
                tasks.add(Task(cursor.getInt(0), cursor.getString(1),
                    LocalDateTime.parse(cursor.getString(2)),
                    LocalDateTime.parse(cursor.getString(3)), cursor.getInt(4),
                    cursor.getInt(5)==1, cursor.getString(6)))
            } while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return tasks
    }

    fun getTask(taskId: Int) : Task {
        lateinit var task: Task
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_ID_TASKS='$taskId'", null)
        if (cursor.moveToFirst()) {
            do {
                task = Task(cursor.getInt(0), cursor.getString(1),
                    LocalDateTime.parse(cursor.getString(2)),
                    LocalDateTime.parse(cursor.getString(3)), cursor.getInt(4),
                    cursor.getInt(5)==1, cursor.getString(6))
            } while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return task
    }


    fun getSubtasks(task: Task) : List<Subtask> {
        val subtasks: MutableList<Subtask> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBTASKS WHERE $COLUMN_TASKID_SUBTASKS='${task.id}'", null)
        if (cursor.moveToFirst()) {
            do {
                subtasks.add(Subtask(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)==1,
                    cursor.getInt(3)))
            } while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return subtasks
    }

    fun updateTask (task: Task) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE_TASKS, task.title)
        contentValues.put(COLUMN_DATE_TASKS, task.date.toString())
        contentValues.put(COLUMN_REMINDER_TASKS, task.reminder.toString())
        contentValues.put(COLUMN_PRIORITY_TASKS, task.priority)
        contentValues.put(COLUMN_DONE_TASKS, task.done)
        contentValues.put(COLUMN_DESCRIPTION_TASKS, task.description)
        db.update(TABLE_TASKS, contentValues, "$COLUMN_ID_TASKS=${task.id}", null)
        db.close()
    }

    fun updateSubtask (subtask: Subtask) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE_SUBTASKS, subtask.title)
        contentValues.put(COLUMN_DONE_SUBTASKS, subtask.done)
        contentValues.put(COLUMN_TASKID_SUBTASKS, subtask.taskId)
        db.update(TABLE_SUBTASKS, contentValues, "$COLUMN_ID_SUBTASKS=${subtask.id}", null)
        db.close()
    }


}