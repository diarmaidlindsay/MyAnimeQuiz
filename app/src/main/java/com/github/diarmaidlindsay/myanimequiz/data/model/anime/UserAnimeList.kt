package com.github.diarmaidlindsay.myanimequiz.data.model.anime

import com.github.diarmaidlindsay.myanimequiz.data.model.media.BaseUserMediaList
import com.github.diarmaidlindsay.myanimequiz.data.model.media.ListStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaFormat
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.media.WeekDay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAnimeList(
    @SerialName("node")
    override val node: AnimeNode,
    @SerialName("list_status")
    override val listStatus: MyAnimeListStatus? = null,
) : BaseUserMediaList<AnimeNode>()

val exampleUserAnimeList = UserAnimeList(
    node = AnimeNode(
        id = 0,
        title = "This is a large anime or manga title",
        broadcast = Broadcast(WeekDay.SUNDAY, "12:00"),
        numEpisodes = 12,
        mediaFormat = MediaFormat.TV,
        status = MediaStatus.AIRING,
        mean = 8f
    ),
    listStatus = MyAnimeListStatus(
        status = ListStatus.WATCHING,
        score = 6,
        progress = 6,
        repeatCount = 2,
        tags = listOf("good", "wtf"),
        comments = "this is a comment"
    )
)