enum class DangerLevel(val dangerLevel: Int) {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    fun getLevel(): Int = dangerLevel

}