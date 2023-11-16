package milwaukee

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import milwaukee.component.MkeFonts
import milwaukee.component.MkePreferences
import milwaukee.component.MkeWindowSizing
import milwaukee.init.AssetRegistryLoadingScreen
import milwaukee.init.InitialAssetLoader
import org.lwjgl.glfw.GLFW
import paintbox.PaintboxGame
import paintbox.PaintboxSettings
import paintbox.ResizeAction
import paintbox.logging.Logger
import paintbox.registry.AssetRegistry
import paintbox.util.gdxutils.disposeQuietly
import java.io.File


class MkeGame(paintboxSettings: PaintboxSettings) : PaintboxGame(paintboxSettings) {

    companion object {

        lateinit var instance: MkeGame
            private set

        fun createPaintboxSettings(launchArguments: List<String>, logger: Logger, logToFile: File?): PaintboxSettings =
            PaintboxSettings(
                launchArguments, logger, logToFile, Milwaukee.VERSION, Milwaukee.DEFAULT_SIZE,
                ResizeAction.ANY_SIZE, Milwaukee.MINIMUM_SIZE
            )
    }

    //region Components

    lateinit var preferences: MkePreferences
        private set
    lateinit var fonts: MkeFonts
        private set
    lateinit var windowSizing: MkeWindowSizing
        private set

    //endregion

    override fun getTitle(): String = "${Milwaukee.TITLE} ${Milwaukee.VERSION}"

    override fun create() {
        super.create()
        instance = this

        this.reloadableLocalizationInstances = listOf(Localization)

        val lwjgl3Graphics: Lwjgl3Graphics = Gdx.graphics as Lwjgl3Graphics
        GLFW.glfwSetWindowAspectRatio(lwjgl3Graphics.window.windowHandle, 16, 9)

        // Add components
        val gdxPrefs = Gdx.app.getPreferences("milwaukee")
        preferences = MkePreferences(this, gdxPrefs).apply {
            this.load()
            this.setStartupSettings(this@MkeGame)
            lwjgl3Graphics.window.setVisible(true)
        }
        fonts = MkeFonts(this).apply { this.addFontsToCache() }
        windowSizing = MkeWindowSizing(this)

        // Register asset loaders
        AssetRegistry.addAssetLoader(InitialAssetLoader())

        // Screens and loading screen
        fun initializeScreens() {
        }
        setScreen(AssetRegistryLoadingScreen(this).apply {
            onStart = {}
            onAssetLoadingComplete = {
                initializeScreens()
            }
            nextScreenProducer = {
                null
            }
        })

        if (MkeArguments.logMissingLocalizations) {
            this.reloadableLocalizationInstances.forEach { it.logMissingLocalizations(false) }
        }
    }


    override fun dispose() {
        super.dispose()

        preferences.disposeQuietly()
        fonts.disposeQuietly()
        windowSizing.disposeQuietly()
    }
}