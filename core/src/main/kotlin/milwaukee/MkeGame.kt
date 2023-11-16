package milwaukee

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import org.lwjgl.glfw.GLFW
import paintbox.PaintboxGame
import paintbox.PaintboxSettings
import paintbox.ResizeAction
import paintbox.font.FontCache
import paintbox.font.PaintboxFont
import paintbox.font.PaintboxFontFreeType
import paintbox.font.PaintboxFontParams
import paintbox.i18n.ILocalization
import paintbox.logging.Logger
import paintbox.registry.AssetRegistry
import paintbox.util.WindowSize
import paintbox.util.gdxutils.isAltDown
import paintbox.util.gdxutils.isControlDown
import paintbox.util.gdxutils.isShiftDown
import milwaukee.init.AssetRegistryLoadingScreen
import milwaukee.init.InitialAssetLoader
import java.io.File


class MkeGame(paintboxSettings: PaintboxSettings)
    : PaintboxGame(paintboxSettings) {

    companion object {
        lateinit var instance: MkeGame
            private set
        
        fun createPaintboxSettings(launchArguments: List<String>, logger: Logger, logToFile: File?): PaintboxSettings =
                PaintboxSettings(launchArguments, logger, logToFile, Milwaukee.VERSION, Milwaukee.DEFAULT_SIZE,
                        ResizeAction.ANY_SIZE, Milwaukee.MINIMUM_SIZE)
    }

    private var lastWindowed: WindowSize = Milwaukee.DEFAULT_SIZE.copy()
    @Volatile
    var blockResolutionChanges: Boolean = false
    
    lateinit var preferences: Preferences
        private set
    
    val allLocalizations: List<ILocalization> 
        get() = this.reloadableLocalizationInstances

    override fun getTitle(): String = "${Milwaukee.TITLE} ${Milwaukee.VERSION}"

    override fun create() {
        super.create()
        MkeGame.instance = this
        this.reloadableLocalizationInstances = listOf(Localization)
        val windowHandle = (Gdx.graphics as Lwjgl3Graphics).window.windowHandle
        GLFW.glfwSetWindowAspectRatio(windowHandle, 16, 9)
        
        
        preferences = Gdx.app.getPreferences("milwaukee")

        (Gdx.graphics as Lwjgl3Graphics).window.setVisible(true)

        addFontsToCache(this.fontCache)
        
        AssetRegistry.addAssetLoader(InitialAssetLoader())
        
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
        
        if (Milwaukee.logMissingLocalizations) {
            this.reloadableLocalizationInstances.forEach { it.logMissingLocalizations(false) }
        }
    }
    

    override fun dispose() {
        super.dispose()
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

    override fun keyDown(keycode: Int): Boolean {
        val processed = super.keyDown(keycode)
        if (!processed && !blockResolutionChanges) {
            if (handleFullscreenKeybinds(keycode)) {
                return true
            }
        }
        return processed
    }

    private fun handleFullscreenKeybinds(keycode: Int): Boolean {
        // Fullscreen shortcuts:
        // F11 - Toggle fullscreen
        // Shift+F11 - Reset window to defaults (see PRMania constants)
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

    private data class FontFamily(val filenameSuffix: String, val idSuffix: String) {
        companion object {
            val REGULAR = FontFamily("Regular", "")
            val ITALIC = FontFamily("Italic", "ITALIC")
            val BOLD = FontFamily("Bold", "BOLD")
            val BOLD_ITALIC = FontFamily("BoldItalic", "BOLD_ITALIC")
            val LIGHT = FontFamily("Light", "LIGHT")
            val LIGHT_ITALIC = FontFamily("LightItalic", "LIGHT_ITALIC")
            
            val regularItalicBoldBoldItalic: List<FontFamily> = listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC)
            val regularItalicBoldBoldItalicLightLightItalic: List<FontFamily> = listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC, LIGHT, LIGHT_ITALIC)
        }
        
        fun toFullFilename(familyName: String, fileExt: String): String {
            return "$familyName-$filenameSuffix.$fileExt"
        }
        
        fun toID(fontIDPrefix: String, isBordered: Boolean): String {
            var id = fontIDPrefix
            if (idSuffix.isNotEmpty()) id += "_$idSuffix"
            if (isBordered) id += "_BORDERED"
            return id
        }
    }
    
    private fun addFontsToCache(cache: FontCache) {
        val emulatedSize = paintboxSettings.emulatedSize
        fun makeParam() = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            minFilter = Texture.TextureFilter.Linear
            magFilter = Texture.TextureFilter.Linear //Nearest
            genMipMaps = false
            incremental = true
            mono = false
            color = Color(1f, 1f, 1f, 1f)
            borderColor = Color(0f, 0f, 0f, 1f)
            characters = " a" // Needed to at least put one glyph in each font to prevent errors
            hinting = FreeTypeFontGenerator.Hinting.Medium
        }

        val defaultAfterLoad: PaintboxFontFreeType.(font: BitmapFont) -> Unit = { font ->
            font.setUseIntegerPositions(true) // Filtering doesn't kick in so badly, solves "wiggly" glyphs
            font.setFixedWidthGlyphs("0123456789")
        }
        val defaultScaledFontAfterLoad: PaintboxFontFreeType.(font: BitmapFont) -> Unit = { font ->
            font.setUseIntegerPositions(false) // Stops glyphs from being offset due to rounding
        }
        val defaultFontSize = 20

        fun addFontFamily(
                familyName: String, fontIDPrefix: String = familyName, fileExt: String = "ttf",
                fontFamilies: List<FontFamily> = FontFamily.regularItalicBoldBoldItalic,
                fontSize: Int = defaultFontSize, borderWidth: Float = 1.5f,
                folderName: String = familyName,
                hinting: FreeTypeFontGenerator.Hinting? = null, generateBordered: Boolean = true,
                scaleToReferenceSize: Boolean = false, referenceSize: WindowSize = WindowSize(1280, 720),
                afterLoadFunc: PaintboxFontFreeType.(BitmapFont) -> Unit = defaultAfterLoad,
        ) {
            fontFamilies.forEach { family ->
                val fileHandle = Gdx.files.internal("fonts/${folderName}/${family.toFullFilename(familyName, fileExt)}")
                cache[family.toID(fontIDPrefix, false)] = PaintboxFontFreeType(
                        PaintboxFontParams(fileHandle, 1, 1f, scaleToReferenceSize, referenceSize),
                        makeParam().apply {
                            if (hinting != null) {
                                this.hinting = hinting
                            }
                            this.size = fontSize
                            this.borderWidth = 0f
                        }).setAfterLoad(afterLoadFunc)
                if (generateBordered) {
                    cache[family.toID(fontIDPrefix, true)] = PaintboxFontFreeType(
                            PaintboxFontParams(fileHandle, 1, 1f, scaleToReferenceSize, referenceSize),
                            makeParam().apply {
                                if (hinting != null) {
                                    this.hinting = hinting
                                }
                                this.size = fontSize
                                this.borderWidth = borderWidth
                            }).setAfterLoad(afterLoadFunc)
                }
            }
        }

        addFontFamily(familyName = "OpenSans", hinting = FreeTypeFontGenerator.Hinting.Full, scaleToReferenceSize = true, generateBordered = false, afterLoadFunc = { font ->
            defaultAfterLoad.invoke(this, font)
            font.data.blankLineScale = 0.25f
        })
    }


    val fontOpenSans: PaintboxFont get() = fontCache["OpenSans"]
    val fontOpenSansBold: PaintboxFont get() = fontCache["OpenSans_BOLD"]
    val fontOpenSansItalic: PaintboxFont get() = fontCache["OpenSans_ITALIC"]
    val fontOpenSansBoldItalic: PaintboxFont get() = fontCache["OpenSans_BOLD_ITALIC"]
    
}