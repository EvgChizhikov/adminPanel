package utils

import kotlinx.coroutines.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class DailyTaskCoroutinesPeriod(private val startTime: LocalTime, private val endTime: LocalTime, private val task: suspend () -> Unit,) {
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun start() {
        job = coroutineScope.launch {
            while (true) {
                val now = LocalDateTime.now(ZoneId.systemDefault())

                if (now.toLocalTime() < startTime || now.toLocalTime() >= endTime) {
                    // sleep until the start time
                    val sleepTime = now.until(LocalDateTime.of(now.toLocalDate(), startTime), ChronoUnit.MILLIS)
                    delay(sleepTime)
                    continue
                }
                task.invoke()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}