package data

import java.time.LocalDateTime

data class Player(
    val name: String,
    val steam_id_64: String,
    val country: String?,
    var points: Int = 0,
    var seconds: Int = 0,
    var lastChanged: LocalDateTime
) {
    override fun toString(): String {
        return "name = $name, steam_id = $steam_id_64, country = $country, points = $points, lastChanged = $lastChanged, seconds = $seconds"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Player) {
            return false
        }
        return steam_id_64 == other.steam_id_64
    }

    override fun hashCode(): Int {
        return steam_id_64.hashCode()
    }

    fun addSeconds() {
        this.seconds += 60
        this.lastChanged = LocalDateTime.now()
    }

    fun addOnePoint() {
        this.points += 1
        this.lastChanged = LocalDateTime.now()
    }
    fun addTwoPoint() {
        this.points += 2
        this.lastChanged = LocalDateTime.now()
    }
    fun addThreePoint() {
        this.points += 3
        this.lastChanged = LocalDateTime.now()
    }
}