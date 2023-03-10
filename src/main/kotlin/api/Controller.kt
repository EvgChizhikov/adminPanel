package api

import data.GameData
import data.Player
import data.User
import kotlinx.coroutines.*
import org.jetbrains.kotlin.com.google.common.collect.ConcurrentHashMultiset
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import requests.getListOfPlayers
import requests.login
import utils.DailyTaskCoroutinesPeriod
import java.time.LocalDateTime
import java.time.LocalTime


@Controller
class Controller() {

    private val gameData = GameData.instance
    val user = User.getInstance()
    var cookies = ""

    private var job: Job? = null

    @GetMapping("/example")
    fun exampleController(
        @RequestParam param1: Int,
        @RequestParam param2: Int,
        @RequestParam param3: Int,
        @RequestParam param4: Int,
        @RequestParam param5: Int
    ) :String {
        val result = "Received parameters: $param1, $param2, $param3, $param4, $param5"
        println(result)
        return "rasp"
    }

    @GetMapping("/example1")
    fun exampleController() :String {
        // Do something with the parameters
        return "rasp"
    }

    @GetMapping("/startCoroutine1")
    @ResponseBody
    fun startCoroutine1() {

        val task1 = DailyTaskCoroutinesPeriod(startTime = LocalTime.of(21, 59), endTime = LocalTime.of(23, 59)) {
            println("Running task1 at ${LocalDateTime.now()}")
            delay(1000)
            gameData.playersOnServer = getListOfPlayers(cookies = cookies)
            delay(60000)
            val playersAfterDelay = getListOfPlayers(cookies = cookies)
            playersAfterDelay.forEach {
                if (gameData.playersOnServer.contains(it)) {
                    if (!gameData.playersToSave.contains(it)) {
                        it.addSeconds()
                        gameData.addPlayer(it)
                    } else {
                        val bufferPlayer = gameData.getPlayer(it.steam_id_64)
                        bufferPlayer?.addSeconds()
                        bufferPlayer?.let { gameData.updatePlayer(it) }

                        gameData.playersToSave.forEach { p ->
                            if (p.seconds >= 1800) {
                                val bufferPlayer1 = gameData.getPlayer(p.steam_id_64)
                                bufferPlayer1?.addOnePoint()
                                bufferPlayer1?.seconds = 0
                                bufferPlayer1?.let { gameData.updatePlayer(it) }
                            }
                        }
                    }
                }
            }
        }
        task1.start()
    }

    @GetMapping("/stopCoroutine")
    @ResponseBody
    fun stopCoroutine(): String {
        if (job != null) {
            job?.cancel()
            job = null
            println("Coroutine stopped.")
            return "Coroutine stopped."
        } else {
            println("Coroutine is not running.")
            return "Coroutine is not running."
        }
    }

    @GetMapping("/")
    fun index(model: Model): String {
        return "adminDataForLogin"
    }

    @PostMapping("/save")
    fun save(@RequestParam name: String?, @RequestParam password: String?): String {
        user.setName(name ?: "null")
        user.setPass(password ?: "null")
        user.username?.let { user.password?.let { it1 -> cookies = login(login = it, password = it1) ?: "" } }
        return "redirect:/players"
    }

    @GetMapping("/players")
    fun getPlayers(): String {
        println("stranica")
        return "players"
    }

    @GetMapping("/getPlayersList")
    @ResponseBody
    fun getPlayersButton(): ConcurrentHashMultiset<Player> {
        println("update")
        println(gameData.playersToSave)
        return gameData.playersToSave
    }

    @GetMapping("/updateList")
    @ResponseBody
    fun updateList(): ConcurrentHashMultiset<Player> {
        return gameData.playersToSave
    }

    @GetMapping("/rasp")
    fun toSchedules(): String {
        return "rasp"
    }
}