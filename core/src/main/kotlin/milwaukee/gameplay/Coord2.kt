package milwaukee.gameplay


data class Coord2(val x: Int, val y: Int) {

    fun toCoord3(z: Int): Coord3 = Coord3(this.x, this.y, z)
    
}
