package utils

import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class DailyTask(private val task: () -> Unit) {
    fun start(timeOfDay: LocalTime, zoneId: ZoneId = ZoneId.systemDefault()) {
        val thread = Thread {
            while (true) {
                val now = LocalDateTime.now(ZoneId.systemDefault())

                val nextRun: LocalDateTime = if (now.isBefore(timeOfDay.atDate(LocalDate.now()))) {
                    timeOfDay.atDate(LocalDate.now())
                } else {
                    timeOfDay.atDate(LocalDateTime.now().plusHours(24).toLocalDate())
                }
                val initialDelay = now.until(nextRun, ChronoUnit.NANOS)
                val delay = convertNanosToMillisAndNanos(initialDelay)
                println(delay)
                Thread.sleep(delay.first, delay.second.toInt())
                task.invoke()
            }
        }
        thread.isDaemon = true
        thread.start()


    }

    private fun convertNanosToMillisAndNanos(nanos: Long): Pair<Long, Long> {
        val millis = TimeUnit.NANOSECONDS.toMillis(nanos)
        val remainingNanos = nanos - TimeUnit.MILLISECONDS.toNanos(millis)
        return Pair(millis, remainingNanos)
    }
}

