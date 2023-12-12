package org.microg.gms.checkin

import android.content.Context
import org.microg.gms.settings.SettingsContract
import org.microg.gms.settings.SettingsContract.CheckIn

object SpoofingPreferences {
    @JvmStatic
    fun isSpoofingEnabled(context: Context): Boolean {
        val projection = arrayOf(CheckIn.BRAND_SPOOF)
        return SettingsContract.getSettings(context, CheckIn.getContentUri(context), projection) { c ->
            c.getInt(0) != 0
        }
    }

    @JvmStatic
    fun setSpoofingEnabled(context: Context, enabled: Boolean) {
        SettingsContract.setSettings(context, CheckIn.getContentUri(context)) {
            put(CheckIn.BRAND_SPOOF, enabled)
        }
    }
}