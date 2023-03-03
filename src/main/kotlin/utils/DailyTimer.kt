package utils

import java.util.*

class DailyTimer(private val task: () -> Unit) {

    private var timer: Timer? = null

    fun start() {
        timer = Timer()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        timer?.schedule(object : TimerTask() {
            override fun run() {
                task.invoke()
            }
        }, calendar.time, 24 * 60 * 60 * 1000)
    }

    fun stop() {
        timer?.cancel()
        timer = null
    }
}