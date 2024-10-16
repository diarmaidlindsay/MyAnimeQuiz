package com.github.diarmaidlindsay.myanimequiz.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.diarmaidlindsay.myanimequiz.R
import com.github.diarmaidlindsay.myanimequiz.data.model.base.Localizable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
) : Localizable {
    private val genreMap = mapOf(
        "Action" to R.string.genre_action,
        "Adventure" to R.string.genre_adventure,
        "Avant Garde" to R.string.genre_avant_garde,
        "Award Winning" to R.string.genre_award_winning,
        "Adult Cast" to R.string.genre_adult_cast,
        "Anthropomorphic" to R.string.genre_anthropomorphic,
        "Cars" to R.string.genre_cars,
        "Comedy" to R.string.genre_comedy,
        "CGDCT" to R.string.genre_cgdct,
        "Childcare" to R.string.genre_childcare,
        "Combat Sports" to R.string.genre_combat_sports,
        "Crossdressing" to R.string.genre_crossdressing,
        "Dementia" to R.string.genre_dementia,
        "Demons" to R.string.genre_demons,
        "Drama" to R.string.genre_drama,
        "Delinquents" to R.string.genre_delinquents,
        "Detective" to R.string.genre_detective,
        "Ecchi" to R.string.genre_ecchi,
        "Erotica" to R.string.genre_erotica,
        "Educational" to R.string.genre_educational,
        "Fantasy" to R.string.genre_fantasy,
        "Game" to R.string.genre_game,
        "Gourmet" to R.string.genre_gourmet,
        "Gag Humor" to R.string.genre_gag_humor,
        "Gore" to R.string.genre_gore,
        "Horror" to R.string.genre_horror,
        "Hentai" to R.string.genre_hentai,
        "Harem" to R.string.genre_harem,
        "High Stakes Game" to R.string.genre_high_stakes_game,
        "Historical" to R.string.genre_historical,
        "Idols (Female)" to R.string.genre_idols_female,
        "Idols (Male)" to R.string.genre_idols_male,
        "Isekai" to R.string.genre_isekai,
        "Iyashikei" to R.string.genre_iyashikei,
        "Josei" to R.string.genre_josei,
        "Kids" to R.string.genre_kids,
        "Love Polygon" to R.string.genre_love_polygon,
        "Mystery" to R.string.genre_mystery,
        "Magic" to R.string.genre_magic,
        "Magical Sex Shift" to R.string.genre_magical_sex_shift,
        "Mahou Shoujo" to R.string.genre_mahou_shoujo,
        "Martial Arts" to R.string.genre_martial_arts,
        "Mecha" to R.string.genre_mecha,
        "Medical" to R.string.genre_medical,
        "Memoir" to R.string.genre_memoir,
        "Military" to R.string.genre_military,
        "Music" to R.string.genre_music,
        "Mythology" to R.string.genre_mythology,
        "Organized Crime" to R.string.genre_organized_crime,
        "Otaku Culture" to R.string.genre_otaku_culture,
        "Parody" to R.string.genre_parody,
        "Performing Arts" to R.string.genre_performing_arts,
        "Pets" to R.string.genre_pets,
        "Police" to R.string.genre_police,
        "Psychological" to R.string.genre_psychological,
        "Romance" to R.string.genre_romance,
        "Racing" to R.string.genre_racing,
        "Reincarnation" to R.string.genre_reincarnation,
        "Reverse Harem" to R.string.genre_reverse_harem,
        "Romantic Subtext" to R.string.genre_romantic_subtext,
        "Samurai" to R.string.genre_samurai,
        "Sci-Fi" to R.string.genre_sci_fi,
        "Slice of Life" to R.string.genre_slice_of_life,
        "Sports" to R.string.genre_sports,
        "Supernatural" to R.string.genre_supernatural,
        "Suspense" to R.string.genre_suspense,
        "School" to R.string.genre_school,
        "Showbiz" to R.string.genre_showbiz,
        "Space" to R.string.genre_space,
        "Strategy Game" to R.string.genre_strategy_game,
        "Super Power" to R.string.genre_superpower,
        "Survival" to R.string.genre_survival,
        "Shoujo Ai" to R.string.genre_shoujo_ai,
        "Girls Love" to R.string.genre_shoujo_ai,
        "Shounen Ai" to R.string.genre_shounen_ai,
        "Boys Love" to R.string.genre_shounen_ai,
        "Seinen" to R.string.genre_seinen,
        "Shoujo" to R.string.genre_shoujo,
        "Shounen" to R.string.genre_shounen,
        "Team Sports" to R.string.genre_team_sports,
        "Time Travel" to R.string.genre_time_travel,
        "Thriller" to R.string.genre_thriller,
        "Vampire" to R.string.genre_vampire,
        "Video Game" to R.string.genre_video_game,
        "Villainess" to R.string.genre_villainess,
        "Visual Arts" to R.string.genre_visual_arts,
        "Workplace" to R.string.genre_workplace,
        "Yaoi" to R.string.genre_yaoi,
        "Yuri" to R.string.genre_yuri
    )

    @Composable
    override fun localized(): String {
        val resourceId = genreMap[name] ?: return name
        return stringResource(resourceId)
    }
}