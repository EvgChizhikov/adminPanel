package utils

import kotlinx.coroutines.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DailyTaskCoroutinesOnce(private val task: () -> Unit) {
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start(timeOfDay: LocalTime) {
        job = coroutineScope.launch {
            while (true) {
                val now = LocalDateTime.now(ZoneId.systemDefault())

                val nextRun: LocalDateTime = if (now.isBefore(timeOfDay.atDate(LocalDate.now()))) {
                    timeOfDay.atDate(LocalDate.now())
                } else {
                    timeOfDay.atDate(LocalDateTime.now().plusHours(24).toLocalDate())
                }
                val initialDelay = abs(now.until(nextRun, ChronoUnit.NANOS))
                val delay = convertNanosToMillisAndNanos(initialDelay)
                println(delay)

                delay(delay.first)
                task.invoke()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private fun convertNanosToMillisAndNanos(nanos: Long): Pair<Long, Long> {
        val millis = TimeUnit.NANOSECONDS.toMillis(nanos)
        val remainingNanos = nanos - TimeUnit.MILLISECONDS.toNanos(millis)
        return Pair(millis, remainingNanos)
    }
}