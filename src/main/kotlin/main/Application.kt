package main


import data.GameData
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import requests.readPlayersFromJsonFile
import requests.writePlayersToJsonFile
import utils.DailyTaskCoroutinesOnce
import java.time.LocalDateTime
import java.time.LocalTime


@SpringBootApplication(scanBasePackages = ["api", "configuration"])
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Starting program...")
            GameData.instance.playersToSave = readPlayersFromJsonFile("playersData.txt")
            if (GameData.instance.playersToSave.isNotEmpty()) {
                println(GameData.instance.playersToSave)
                val iterator = GameData.instance.playersToSave.iterator()
                while (iterator.hasNext()) {
                    val player = iterator.next()
                    if (player.points < 1) {
                        iterator.remove()
                    }
                }
            }

            val task = DailyTaskCoroutinesOnce {
                println("Everyone got minus one point ${LocalDateTime.now()}")
                GameData.instance.playersToSave.forEach { player ->
                    if (player.points > 0) {
                        player.points -= 1
                        player.lastChanged = LocalDateTime.now()
                    }
                }
            }
            task.start(LocalTime.of(23, 59))


            runApplication<Application>(*args)

            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    println("Program is about to exit")
                    writePlayersToJsonFile("playersData.txt")
                }
            })
        }
    }
}



