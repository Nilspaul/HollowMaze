package com.hollowmaze.game.model
import android.content.ClipData.Item
import android.graphics.Color
import com.hollowmaze.game.controller.key.Key
import com.hollowmaze.game.controller.pausebutton.PauseButton
import android.graphics.Paint
import android.graphics.RectF
import com.hollowmaze.game.controller.CollisionWarning
import com.hollowmaze.game.controller.DirectionBorderController
import com.hollowmaze.game.controller.PingController
import com.hollowmaze.game.controller.bush.Bush
import com.hollowmaze.game.controller.controls.JoystickController
import com.hollowmaze.game.controller.cooldown.Cooldown
import com.hollowmaze.game.controller.door.Door
import com.hollowmaze.game.controller.items.Eye
import com.hollowmaze.game.controller.helper.Position
import com.hollowmaze.game.controller.helper.Size
import com.hollowmaze.game.controller.items.ItemController
import com.hollowmaze.game.controller.player.PlayerController
import com.hollowmaze.game.controller.trap.Trap
import kotlin.random.Random

class GameModel() {
    var window = Size(0f,0f)
    var canvasSize = Size(10_000f, 10_000f)
    var viewport = Position(0f, 0f)

    lateinit var player: PlayerController
    var control: JoystickController? = null
    var enemy: PlayerController? = null
    var pauseButton: PauseButton? = null
    var collisionWarning: CollisionWarning? = null
    var key: Key? = null
    var door: Door?  = null
    var directionBorderController : DirectionBorderController? = null
    // TODO: Walls auslagern
    var walls = mutableListOf<MaterialModel>()
    var bushes = mutableListOf<Bush>()
    var traps = mutableListOf<Trap>()
    var eyes = mutableListOf<Eye>()
    var items = mutableListOf<ItemController>()
    val pingController = PingController()
    var cooldown = Cooldown()

    fun pingCollidesWithWalls(): Pair<CollisionResult, MaterialModel>? {
        val ping = pingController.pings.lastOrNull() ?: return null
        if(pingController.pings.size>pingController.maxSize && !player.model.hasUnlimitedPings) {
            pingController.pings.clear()
            return null
        }
        val visibleArea = RectF(viewport.x, viewport.y, viewport.x + window.width, viewport.y + window.height)

        walls.forEach {
            if(RectF.intersects(visibleArea,it.boundingBox)) {
                it.isSquareCollidingWithPing(ping).let { collisionResult ->
                    collisionResult?.let { col ->
                        it.paint = Paint().apply {
                            this.color = Color.argb(
                                255,
                                Random.nextInt(0, 255),
                                Random.nextInt(0, 255),
                                Random.nextInt(0, 255)
                            )
                        }
                        return Pair(collisionResult, it)
                    }
                }
            }
        }
        return null
    }
}
