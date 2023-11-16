package milwaukee.desktop

import com.beust.jcommander.Parameter
import paintbox.desktop.PaintboxArguments
import milwaukee.Milwaukee

class MkeArguments : PaintboxArguments() {

    @Parameter(
        names = ["--log-missing-localizations"],
        description = "Logs any missing localizations. Other locales are checked against the default properties file."
    )
    var logMissingLocalizations: Boolean = false
    
    @Parameter(
        names = ["--portable-mode"],
        description = "The ${Milwaukee.FOLDER_NAME}/ directory will be local to the game executable instead of the user home."
    )
    var portableMode: Boolean = false


}