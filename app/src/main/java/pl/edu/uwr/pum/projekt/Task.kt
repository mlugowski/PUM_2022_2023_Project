package pl.edu.uwr.pum.projekt

import java.time.LocalDateTime

data class Task(var title: String, var date: LocalDateTime, var reminder: LocalDateTime, var priority: Int,
                var done: Boolean, var description: String) {
    var id: Int = 0
    constructor(id: Int, title: String, date: LocalDateTime, reminder: LocalDateTime, priority: Int,
                done: Boolean, description: String) :
            this(title, date, reminder, priority, done, description) {
        this.id = id
    }
}