package main

import api.playersToSave
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import requests.readPlayersFromJsonFile
import requests.writePlayersToJsonFile


@SpringBootApplication(scanBasePackages = ["api", "configuration"])
class Application {

}

fun main(args: Array<String>) {

    println("Starting program...")
    readPlayersFromJsonFile("playersData.txt")
    if (playersToSave.isNotEmpty()) {
        println(playersToSave)
        val iterator = playersToSave.iterator()
        while (iterator.hasNext()) {
            val player = iterator.next()
            if (player.points < 1) {
                iterator.remove()
            }
        }
        println(playersToSave)
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

