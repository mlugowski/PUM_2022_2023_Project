package pl.edu.uwr.pum.projekt


data class Subtask(var title: String, var done: Boolean, var taskId: Int) {
    var id: Int = 0
    constructor(id: Int, title: String, done: Boolean, taskId: Int) : this(title, done, taskId) {
        this.id = id
    }
}