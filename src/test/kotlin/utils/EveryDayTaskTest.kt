package utils

import data.GameData
import data.Player
import org.jetbrains.kotlin.com.google.common.collect.ConcurrentHashMultiset
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime

internal class EveryDayTaskTest {

    @Test
    fun testEveryDayTask() {

        val players = ConcurrentHashMultiset.create<Player>()
        players.add(Player("Vasya", "123", points = 3, lastChanged = LocalDateTime.now(), country = "UA"))
        players.add(Player("Petya", "456", points = 2, lastChanged = LocalDateTime.now(), country = "UA"))
        players.add(Player("Grisha", "789", points = 1, lastChanged = LocalDateTime.now(), country = "BLR"))
        players.add(Player("Marusja", "785", points = 0, lastChanged = LocalDateTime.now(), country = "BLR"))

        val gameData = GameData(playersToSave = players)

        val dailyTask = EveryDayTask {

            gameData.playersToSave.forEach { player ->
                if (player.points > 0) {
                    player.points -= 1
                    player.lastChanged = LocalDateTime.now()
                }
            }
        }
        val timeOfDay = LocalTime.of(10, 0)

        dailyTask.start(timeOfDay)
        Thread.sleep(1000)
        dailyTask.stop()

        gameData.playersToSave.forEach { player ->
            println(player)
            assert(
                player.points == when (player.name) {
                    "Vasya" -> 2
                    "Petya" -> 1
                    "Grisha" -> 0
                    else -> 0
                }
            )
        }
    }

}