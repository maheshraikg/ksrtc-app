package com.example.ksrtc.ui.components

import com.example.ksrtc.data.model.AppLanguage
import com.example.ksrtc.data.model.BusTimetableEntity
import com.example.ksrtc.data.model.StationEntity

object LanguageUtils {

    fun getStationName(station: StationEntity, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA && station.nameKn.isNotBlank()) {
            station.nameKn
        } else {
            station.name
        }
    }

    fun getFromStationName(item: BusTimetableEntity, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA && item.fromStationKn.isNotBlank()) {
            item.fromStationKn
        } else {
            item.fromStation
        }
    }

    fun getToStationName(item: BusTimetableEntity, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA && item.toStationKn.isNotBlank()) {
            item.toStationKn
        } else {
            item.toStation
        }
    }

    fun getViaStops(item: BusTimetableEntity, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA && item.viaStopsKn.isNotBlank()) {
            item.viaStopsKn
        } else {
            item.viaStops
        }
    }

    fun getNotes(item: BusTimetableEntity, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA && item.notesKn.isNotBlank()) {
            item.notesKn
        } else {
            item.notes
        }
    }

    fun getString(en: String, kn: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.KANNADA) kn else en
    }
}
