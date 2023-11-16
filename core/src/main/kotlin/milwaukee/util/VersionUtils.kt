package milwaukee.util

import paintbox.util.Version


val Version.isDevVersion: Boolean
    get() = this.suffix.startsWith("dev")

val Version.isPrereleaseVersion: Boolean
    get() = listOf("beta", "alpha", "rc").any { this.suffix.startsWith(it, ignoreCase = true) }
