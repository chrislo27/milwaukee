package milwaukee

import com.badlogic.gdx.Preferences
import paintbox.prefs.KeyValue
import paintbox.prefs.NewIndicator
import paintbox.prefs.PaintboxPreferences


class MkePreferences(main: MkeGame, prefs: Preferences) : PaintboxPreferences<MkeGame>(main, prefs) {

    private val _allKeyValues: MutableList<KeyValue<*>> = mutableListOf()
    private val _allNewIndicators: MutableList<NewIndicator> = mutableListOf()

    override val allKeyValues: List<KeyValue<*>> get() = _allKeyValues
    override val allNewIndicators: List<NewIndicator> get() = _allNewIndicators

    private fun <T> newKeyValue(keyValue: KeyValue<T>): KeyValue<T> {
        _allKeyValues.add(keyValue)
        return keyValue
    }

    private fun newNewIndicator(newIndicator: NewIndicator): NewIndicator {
        _allNewIndicators.add(newIndicator)
        return newIndicator
    }

    override fun getLastVersionKey(): String = PreferenceKeys.LAST_VERSION
}

object PreferenceKeys {

    const val LAST_VERSION: String = "lastVersion"
}
