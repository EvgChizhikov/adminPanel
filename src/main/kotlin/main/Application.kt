package main


import data.GameData
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import requests.readPlayersFromJsonFile
import requests.writePlayersToJsonFile


@SpringBootApplication(scanBasePackages = ["api", "configuration"])
class Application {
    companion object{
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
                println(GameData.instance.playersToSave)
            }

            runApplication<Application>(*args)

            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    // Code to be executed when the program exits
                    println("Program is about to exit")
                    writePlayersToJsonFile("playersData.txt")
                }
            })
        }
    }

}



