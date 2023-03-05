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
import org.springframework.web.context.request.async.DeferredResult
import requests.getListOfPlayers
import requests.login


@Controller
class Controller() {

    private val gameData = GameData.instance
    val user = User.getInstance()
    var cookies = ""

    private var job: Job? = null

    @GetMapping("/startCoroutine")
    @ResponseBody
    fun startCoroutine(): DeferredResult<String> {
        val deferredResult = DeferredResult<String>()
        if (job == null) {
            job = GlobalScope.launch {
                while (isActive) {

                    delay(1000)
                    gameData.playersOnServer = getListOfPlayers(cookies = cookies)
                    delay(5000)
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
                                    if (p.seconds > 50000) {
                                        val bufferPlayer1 = gameData.getPlayer(p.steam_id_64)
                                        bufferPlayer1?.addPoint()
                                        bufferPlayer1?.seconds = 0
                                        bufferPlayer1?.let { gameData.updatePlayer(it) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            println("Coroutine started.")
            deferredResult.setResult("Coroutine started.")
        } else {
            println("Coroutine is already running.")
            deferredResult.setResult("Coroutine is already running.")
        }
        return deferredResult
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
}