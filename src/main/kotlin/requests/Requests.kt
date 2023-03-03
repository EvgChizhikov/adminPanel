package requests

import data.Player
import com.google.gson.Gson
import data.GameData
import org.jetbrains.kotlin.com.google.common.collect.ConcurrentHashMultiset
import java.io.File
import java.io.InputStreamReader
import java.net.*
import java.time.LocalDateTime


fun login(server: String = "http://91.210.169.105:8014", login: String, password: String): String? {
    try {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        val url = URL("$server/api/login")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val jsonInputString = "{\"username\": \"$login\", \"password\": \"$password\"}"
        val input = jsonInputString.toByteArray(Charsets.UTF_8)
        val length = input.size

        connection.setFixedLengthStreamingMode(length)
        connection.setRequestProperty("Accept", "*/*")
//        connection.setRequestProperty("Accept-Encoding", "gzip, deflate")
//        connection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
        connection.setRequestProperty("Cache-Control", "max-age=0")
        connection.setRequestProperty("Connection", "keep-alive")
        connection.setRequestProperty("Content-Length", length.toString())
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Host", server)
        connection.setRequestProperty("Origin", server)
        connection.setRequestProperty("Referer", "$server/")
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36"
        )

        val os = connection.outputStream
        os.write(input, 0, input.size)

        connection.connect()
        connection.content

        val cookies = cookieManager.cookieStore.cookies
        return cookies[1].toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun getListOfPlayers(serverUrl: String = "http://91.210.169.105:8014", cookies: String): ConcurrentHashMultiset<Player> {
    val url = URL("$serverUrl/api/get_players")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Connection", "keep-alive")
    connection.setRequestProperty("Cookie", cookies)
    connection.setRequestProperty("Referer", "$serverUrl/")
//    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
    connection.setRequestProperty("charset", "UTF-8")


    val responseCode = connection.responseCode


    if (responseCode == HttpURLConnection.HTTP_OK) {
        return parsePlayers(InputStreamReader(connection.inputStream, "UTF-8").readText())
    } else {
        throw Exception("Failed to send GET request: $responseCode")
    }
}


fun parsePlayers(json: String): ConcurrentHashMultiset<Player> {
    val gson = Gson()
    val data = gson.fromJson(json, Map::class.java)

    val players = ConcurrentHashMultiset.create<Player>()
    val result = data["result"] as List<Map<String, Any>>
    for (playerData in result) {
        val name = playerData["name"] as String
        val steamId = playerData["steam_id_64"] as String
        val steamInfo = playerData["profile"] as Map<String, Any>?

        val country = steamInfo?.let { info ->
            val infoMap = info["steaminfo"] as Map<String, Any>?
            infoMap?.get("country") as String?
        }
        players.add(Player(name, steamId, country, lastChanged = LocalDateTime.now()))
    }

    return players
}

fun readPlayersFromJsonFile(filename: String): ConcurrentHashMultiset<Player> {
    val gson = Gson()
    val playersToSave = ConcurrentHashMultiset.create<Player>()
    if (!File(filename).readText().isNullOrEmpty()) {
        val playersArray = gson.fromJson(File(filename).readText(), Array<Player>::class.java)
        playersToSave.addAll(playersArray)
    }
    GameData.instance.playersToSave = playersToSave
    return playersToSave
}

fun writePlayersToJsonFile(filename: String) {
    val gson = Gson()
    val json = gson.toJson(GameData.instance.playersToSave)

    File(filename).writeText(json)
}



