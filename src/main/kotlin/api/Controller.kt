package api

import data.Player
import data.User
import kotlinx.coroutines.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.async.DeferredResult
import requests.getListOfPlayers
import requests.login

public var playersToSave: HashSet<Player> = mutableSetOf<Player>() as HashSet<Player>
public var playersOnServer: HashSet<Player> = mutableSetOf<Player>() as HashSet<Player>

@Controller
class Controller {


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
                    playersOnServer = getListOfPlayers(cookies = cookies)
                    delay(60000)
                    val playersAfterDelay = getListOfPlayers(cookies = cookies)
                    playersAfterDelay.forEach {
                        if (playersOnServer.contains(it)) {
                            if (!playersToSave.contains(it)) {
                                it.addSeconds()
                                playersToSave.add(it)
                            } else {
                                val bufferPlayer = playersToSave.first { el -> el.steam_id_64 == it.steam_id_64 }
                                playersToSave.remove(bufferPlayer)
                                bufferPlayer.addSeconds()
                                playersToSave.add(bufferPlayer)

                                val copyOfPlayersToSave = ArrayList(playersToSave)
                                copyOfPlayersToSave.forEach { p ->
                                    if (p.seconds > 1800) {
                                        val bufferPlayer1 = playersToSave.first { el -> el.steam_id_64 == p.steam_id_64 }
                                        playersToSave.remove(bufferPlayer1)
                                        bufferPlayer1.seconds = 0
                                        bufferPlayer1.addPoint()
                                        playersToSave.add(bufferPlayer1)
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
    fun getPlayersButton(): HashSet<Player> {
        println("update")
//        val playersFromServer = getListOfPlayers(cookies = cookies)
//        playersFromServer.forEach {
//            if (!playersToSave.contains(it)) {
//                playersToSave.add(it)
//                println("В список был добавлен $it")
//            }
//        }
        println(playersToSave)
        return playersToSave
    }

    @GetMapping("/updateList")
    @ResponseBody
    fun updateList(): HashSet<Player> {
        return playersToSave
    }

}