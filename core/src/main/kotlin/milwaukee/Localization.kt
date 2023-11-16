package milwaukee

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import paintbox.i18n.LocalePickerBase
import paintbox.i18n.LocalizationBase
import paintbox.i18n.LocalizationGroup


object MkeLocalePicker : LocalePickerBase(Gdx.files.internal("localization/langs.json"))


abstract class MkeLocalizationBase(baseHandle: FileHandle) : LocalizationBase(baseHandle, MkeLocalePicker)


private object BaseLocalization
    : MkeLocalizationBase(Gdx.files.internal("localization/default"))

object Localization : LocalizationGroup(
    MkeLocalePicker,
    listOf(
        BaseLocalization,
    )
)
