package milwaukee.gameplay.machines

import milwaukee.gameplay.Coord3


data class CollisionMask(
    val otherCollisions: List<Coord3>
) {
    
    fun rotate90Cw(): CollisionMask {
        return this.copy(otherCollisions = otherCollisions.map { coord3 ->
            coord3.copy(x = coord3.y, y = -coord3.x)
        })
    }
    
    fun rotate90Ccw(): CollisionMask {
        return this.copy(otherCollisions = otherCollisions.map { coord3 ->
            coord3.copy(x = -coord3.y, y = coord3.x)
        })
    }
    
    fun rotate180(): CollisionMask {
        return this.copy(otherCollisions = otherCollisions.map { coord3 ->
            coord3.copy(x = -coord3.x, y = -coord3.y)
        })
    }
    
    fun rotateCw(num90Turns: Int): CollisionMask {
        if (num90Turns < 0) return rotateCcw(-num90Turns)
        return when (num90Turns % 4) {
            0 -> this
            1 -> this.rotate90Cw()
            2 -> this.rotate180()
            3 -> this.rotate90Ccw()
            else -> error("Invalid when case")
        }
    }
    
    fun rotateCcw(num90Turns: Int): CollisionMask {
        if (num90Turns < 0) return rotateCw(-num90Turns)
        return when (num90Turns % 4) {
            0 -> this
            1 -> this.rotate90Ccw()
            2 -> this.rotate180()
            3 -> this.rotate90Cw()
            else -> error("Invalid when case")
        }
    }
}
