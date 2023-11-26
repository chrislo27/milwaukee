package milwaukee.gameplay


data class Coord3(val x: Int, val y: Int, val z: Int) {
    
    companion object {
        val ZERO: Coord3 = Coord3(0, 0, 0)
    }
    
    fun add(other: Coord3): Coord3 {
        return Coord3(this.x + other.x, this.y + other.y, this.z + other.z)
    }
    
    operator fun plus(other: Coord3): Coord3 = this.add(other)

    fun add(other: Coord2): Coord3 {
        return Coord3(this.x + other.x, this.y + other.y, this.z)
    }

    operator fun plus(other: Coord2): Coord3 = this.add(other)
    
    operator fun unaryMinus(): Coord3 = this.copy(x = -x, y = -y, z = -z)
    
}
