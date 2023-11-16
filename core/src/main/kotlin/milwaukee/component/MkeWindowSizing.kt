package milwaukee.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import milwaukee.Milwaukee
import milwaukee.MkeGame
import paintbox.util.WindowSize
import paintbox.util.gdxutils.isAltDown
import paintbox.util.gdxutils.isControlDown
import paintbox.util.gdxutils.isShiftDown


class MkeWindowSizing(main: MkeGame) : IMkeComponent {

    private var lastWindowed: WindowSize = Milwaukee.DEFAULT_SIZE.copy()
    
    init {
        main.inputMultiplexer.addProcessor(this.InputHandler())
    }

    fun attemptFullscreen() {
        lastWindowed = WindowSize(Gdx.graphics.width, Gdx.graphics.height)
        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
    }

    fun attemptEndFullscreen() {
        val last = lastWindowed
        Gdx.graphics.setWindowedMode(last.width, last.height)
    }

    fun attemptResetWindow() {
        Gdx.graphics.setWindowedMode(Milwaukee.DEFAULT_SIZE.width, Milwaukee.DEFAULT_SIZE.height)
    }

    private fun handleFullscreenKeybinds(keycode: Int): Boolean {
        // Fullscreen shortcuts:
        // F11 - Toggle fullscreen
        // Shift+F11 - Reset window to defaults
        // Alt+Enter - Toggle fullscreen
        val ctrl = Gdx.input.isControlDown()
        val shift = Gdx.input.isShiftDown()
        val alt = Gdx.input.isAltDown()
        if (!ctrl) {
            val currentlyFullscreen = Gdx.graphics.isFullscreen
            if (!alt && keycode == Input.Keys.F11) {
                if (!shift) {
                    if (currentlyFullscreen) {
                        attemptEndFullscreen()
                    } else {
                        attemptFullscreen()
                    }
                } else {
                    attemptResetWindow()
                }

                return true
            } else if (!shift && alt && keycode == Input.Keys.ENTER) {
                if (currentlyFullscreen) {
                    attemptEndFullscreen()
                } else {
                    attemptFullscreen()
                }

                return true
            }
        }

        return false
    }

    override fun dispose() {
    }
    
    private inner class InputHandler : InputAdapter() {

        override fun keyDown(keycode: Int): Boolean {
            val processed = super.keyDown(keycode)
            if (!processed) {
                if (handleFullscreenKeybinds(keycode)) {
                    return true
                }
            }
            return processed
        }
    }
}