package milwaukee

import paintbox.util.Version
import paintbox.util.WindowSize
import java.io.File


object Milwaukee {

    const val TITLE = "Milwaukee"
    const val GITHUB = "https://github.com/chrislo27/Milwaukee"
    val VERSION: Version = Version(0, 1, 0, "dev_20231115a")
    
    val DEFAULT_SIZE: WindowSize = WindowSize(1280, 720)
    val MINIMUM_SIZE: WindowSize = WindowSize(1152, 648)

    var portableMode: Boolean = false
    var possiblyNewPortableMode: Boolean = false

    const val FOLDER_NAME = ".milwaukee-game"
    val MAIN_FOLDER: File by lazy {
        (if (portableMode) File("${FOLDER_NAME}/") else File(System.getProperty("user.home") + "/${FOLDER_NAME}/")).apply {
            mkdirs()
        }
    }

    val isDevVersion: Boolean = VERSION.suffix.startsWith("dev")
    val isPrereleaseVersion: Boolean =
        listOf("beta", "alpha", "rc").any { VERSION.suffix.startsWith(it, ignoreCase = true) }

    // Command line arguments
    var logMissingLocalizations: Boolean = false // TODO make this not live in this class

}
