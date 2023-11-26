package milwaukee.gameplay.machines

import milwaukee.gameplay.Coord2
import milwaukee.gameplay.Coord3


data class CollisionMask(
    val centrePoint: Coord2,
    val nonCentreCollisions: List<Coord3>
) {
    
    //region Collision detection
    
    fun getAllCollidingPoints(): List<Coord3> =
        (this.nonCentreCollisions.map { it + this.centrePoint }.toSet() + this.centrePoint.toCoord3(0)).toList()
    
    fun isCollidingWith(other: CollisionMask): Boolean {
        val thisCollisions = this.getAllCollidingPoints().toSet()
        val otherCollisions = other.getAllCollidingPoints().toSet()
        
        return thisCollisions.any { it in otherCollisions }
    }
    
    //endregion
    
    //region Rotations
    
    fun rotate90Cw(): CollisionMask {
        return this.copy(nonCentreCollisions = nonCentreCollisions.map { coord3 ->
            coord3.copy(x = coord3.y, y = -coord3.x)
        })
    }
    
    fun rotate90Ccw(): CollisionMask {
        return this.copy(nonCentreCollisions = nonCentreCollisions.map { coord3 ->
            coord3.copy(x = -coord3.y, y = coord3.x)
        })
    }
    
    fun rotate180(): CollisionMask {
        return this.copy(nonCentreCollisions = nonCentreCollisions.map { coord3 ->
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
    
    //endregion
}
