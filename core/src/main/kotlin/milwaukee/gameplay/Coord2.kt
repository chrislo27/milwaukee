package milwaukee.gameplay


data class Coord2(val x: Int, val y: Int) {

    companion object {
        val ZERO: Coord2 = Coord2(0, 0)
    }

    fun toCoord3(z: Int): Coord3 = Coord3(this.x, this.y, z)


    fun add(other: Coord2): Coord2 {
        return Coord2(this.x + other.x, this.y + other.y)
    }

    operator fun plus(other: Coord2): Coord2 = this.add(other)

    operator fun unaryMinus(): Coord2 = this.copy(x = -x, y = -y)
}
