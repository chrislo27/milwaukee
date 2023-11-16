package milwaukee.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import milwaukee.MkeGame
import paintbox.font.FontCache
import paintbox.font.PaintboxFont
import paintbox.font.PaintboxFontFreeType
import paintbox.font.PaintboxFontParams
import paintbox.util.WindowSize


class MkeFonts(private val main: MkeGame) : IMkeComponent {

    private data class FontFamily(val filenameSuffix: String, val idSuffix: String) {
        companion object {

            val REGULAR = FontFamily("Regular", "")
            val ITALIC = FontFamily("Italic", "ITALIC")
            val BOLD = FontFamily("Bold", "BOLD")
            val BOLD_ITALIC = FontFamily("BoldItalic", "BOLD_ITALIC")
            val LIGHT = FontFamily("Light", "LIGHT")
            val LIGHT_ITALIC = FontFamily("LightItalic", "LIGHT_ITALIC")

            val regularItalicBoldBoldItalic: List<FontFamily> = listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC)
            val regularItalicBoldBoldItalicLightLightItalic: List<FontFamily> =
                listOf(REGULAR, ITALIC, BOLD, BOLD_ITALIC, LIGHT, LIGHT_ITALIC)
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

    private val fontCache: FontCache = main.fontCache

    val fontOpenSans: PaintboxFont get() = fontCache["OpenSans"]
    val fontOpenSansBold: PaintboxFont get() = fontCache["OpenSans_BOLD"]
    val fontOpenSansItalic: PaintboxFont get() = fontCache["OpenSans_ITALIC"]
    val fontOpenSansBoldItalic: PaintboxFont get() = fontCache["OpenSans_BOLD_ITALIC"]

    fun addFontsToCache() {
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
                fontCache[family.toID(fontIDPrefix, false)] = PaintboxFontFreeType(
                    PaintboxFontParams(fileHandle, 1, 1f, scaleToReferenceSize, referenceSize),
                    makeParam().apply {
                        if (hinting != null) {
                            this.hinting = hinting
                        }
                        this.size = fontSize
                        this.borderWidth = 0f
                    }).setAfterLoad(afterLoadFunc)
                if (generateBordered) {
                    fontCache[family.toID(fontIDPrefix, true)] = PaintboxFontFreeType(
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

        addFontFamily(
            familyName = "OpenSans",
            hinting = FreeTypeFontGenerator.Hinting.Full,
            scaleToReferenceSize = true,
            generateBordered = false,
            afterLoadFunc = { font ->
                defaultAfterLoad.invoke(this, font)
                font.data.blankLineScale = 0.25f
            }
        )
    }

    override fun dispose() {
    }
}
