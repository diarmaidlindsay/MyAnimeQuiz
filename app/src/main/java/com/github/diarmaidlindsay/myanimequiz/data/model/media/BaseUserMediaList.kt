package com.github.diarmaidlindsay.myanimequiz.data.model.media

import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeNode
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.UserAnimeList
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.UserMangaList

abstract class BaseUserMediaList<T : BaseMediaNode> {
    abstract val node: T
    abstract val listStatus: BaseMyListStatus?

    fun userProgress() = when (this) {
        is UserAnimeList -> listStatus?.progress
        is UserMangaList -> {
            val status = listStatus
            if (status?.isUsingVolumeProgress() == true) {
                status.numVolumesRead
            } else {
                status?.progress
            }
        }

        else -> null
    }

    fun totalProgress() = when (this) {
        is UserAnimeList -> node.numEpisodes
        is UserMangaList -> {
            if (listStatus?.isUsingVolumeProgress() == true) {
                node.numVolumes
            } else {
                node.numChapters
            }
        }

        else -> null
    }

    fun calculateProgressBarValue(): Float {
        val total = totalProgress() ?: 0
        return if (total == 0) 0f
        else (userProgress() ?: 0).div(total.toFloat())
    }

    val isAiring
        get() = node.status == MediaStatus.AIRING
                || ((node as? AnimeNode)?.broadcast != null && node.status == MediaStatus.AIRING)
}