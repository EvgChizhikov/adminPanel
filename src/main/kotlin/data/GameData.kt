package data

import org.jetbrains.kotlin.com.google.common.collect.ConcurrentHashMultiset


data class GameData(
    var playersToSave: ConcurrentHashMultiset<Player> = ConcurrentHashMultiset.create<Player>(),
    var playersOnServer: ConcurrentHashMultiset<Player> = ConcurrentHashMultiset.create<Player>()
) {
    companion object {
        val instance = GameData()
    }

    fun addPlayer(player: Player) {
        playersToSave.add(player)
    }

    fun removePlayer(player: Player) {
        playersToSave.remove(player)
    }

    fun updatePlayer(player: Player) {
        playersToSave.removeIf { it.steam_id_64 == player.steam_id_64 }
        playersToSave.add(player)
    }

    fun getPlayer(steam_id_64: String): Player? {
        return playersToSave.firstOrNull { it.steam_id_64 == steam_id_64 }
    }
}
