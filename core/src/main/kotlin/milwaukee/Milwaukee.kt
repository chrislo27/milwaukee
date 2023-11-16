package milwaukee

import paintbox.util.Version
import paintbox.util.WindowSize
import java.io.File


object Milwaukee {

    const val TITLE = "Milwaukee"
    val VERSION: Version = Version(0, 1, 0, "dev_20231115a")
    
    val DEFAULT_SIZE: WindowSize = WindowSize(1280, 720)
    val MINIMUM_SIZE: WindowSize = WindowSize(1152, 648)

    const val FOLDER_NAME = ".milwaukee-game"
    val MAIN_FOLDER: File by lazy {
        (if (MkeArguments.PortableModeFlags.portableMode) 
            File("${FOLDER_NAME}/") 
        else File(System.getProperty("user.home") + "/${FOLDER_NAME}/")).apply {
            mkdirs()
        }
    }

}
