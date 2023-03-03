package data

data class User private constructor(var username: String?, var password: String?) {
    companion object {
        private var instance: User? = null

        fun getInstance(): User {
            if (instance == null) {
                instance = User(null, null)
            }
            return instance!!
        }
    }

    fun setName(name: String) {
        this.username = name
    }

    fun setPass(newPassword: String) {
        this.password = newPassword
    }
}