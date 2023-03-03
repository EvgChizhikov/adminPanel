package utils

import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class EveryDayTask(private val task: () -> Unit) {

    private var executor: ScheduledExecutorService? = null

    fun start(timeOfDay: LocalTime) {
        val now = LocalTime.now()
        val initialDelay = if (now.isBefore(timeOfDay)) {
            now.until(timeOfDay, ChronoUnit.SECONDS)
        } else {
            now.until(timeOfDay.plusHours(24), ChronoUnit.SECONDS)
        }

        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate(task, initialDelay, 24, TimeUnit.HOURS)
    }

    fun stop() {
        executor?.shutdown()
        executor = null
    }
}