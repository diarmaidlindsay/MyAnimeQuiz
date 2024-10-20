package com.github.diarmaidlindsay.myanimequiz.data.repository

import com.github.diarmaidlindsay.myanimequiz.data.model.ApiResponse
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.UserAnimeList
import com.github.diarmaidlindsay.myanimequiz.data.model.media.ListStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.media.MediaSort
import com.github.diarmaidlindsay.myanimequiz.data.service.ApiService
import com.github.diarmaidlindsay.myanimequiz.domain.usecase.AuthUseCase
import com.github.diarmaidlindsay.myanimequiz.scopes.DatastoreScope
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val apiService: ApiService,
    authUseCase: AuthUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    datastoreScope: DatastoreScope
) : BaseRepository(authUseCase, userPreferencesRepository, datastoreScope) {

    companion object {
        const val TODAY_FIELDS =
            "alternative_titles{en,ja},broadcast,mean,start_season,status"
        const val CALENDAR_FIELDS =
            "alternative_titles{en,ja},broadcast,mean,start_season,status,media_type,num_episodes"
        const val SEASONAL_FIELDS =
            "alternative_titles{en,ja},start_season,broadcast,num_episodes,media_type,mean,num_list_users"
        private const val RECOMMENDED_FIELDS = "alternative_titles{en,ja},mean"
        private const val LIST_STATUS_FIELDS =
            "start_date,finish_date,num_times_rewatched,is_rewatching,rewatch_value,priority,tags,comments"
        private const val ANIME_DETAILS_FIELDS =
            "id,title,main_picture,pictures,alternative_titles,start_date,end_date," +
                    "synopsis,mean,rank,popularity,num_list_users,num_scoring_users,media_type,status,genres," +
                    "my_list_status{$LIST_STATUS_FIELDS},num_episodes,start_season,broadcast,source," +
                    "average_episode_duration,studios,opening_themes,ending_themes," +
                    "related_anime{media_type,alternative_titles{en,ja}}," +
                    "related_manga{media_type,alternative_titles{en,ja}}," +
                    "recommendations{alternative_titles{en,ja}},background,statistics"
        private const val USER_ANIME_LIST_FIELDS =
            "alternative_titles{en,ja},list_status{$LIST_STATUS_FIELDS},num_episodes,media_type,status,broadcast"
        private const val SEARCH_FIELDS =
            "id,title,alternative_titles{en,ja},main_picture,mean,media_type,num_episodes,start_season"
        const val RANKING_FIELDS =
            "alternative_titles{en,ja},mean,media_type,num_episodes,num_list_users"
        private const val CHARACTERS_FIELDS =
            "id,first_name,last_name,alternative_name,main_picture"
    }

    suspend fun getUserAnimeList(
        status: ListStatus,
        sort: MediaSort,
        page: String? = null
    ): ApiResponse<List<UserAnimeList>> {
        return try {
            val result = if (page == null) apiService.getUserAnimeList(
                status = status.value,
                sort = sort.value,
                limit = 1000,
                nsfw = userPreferencesRepository.nsfwInt(),
                fields = USER_ANIME_LIST_FIELDS
            )
            else apiService.getUserAnimeList(page)
            result.error?.let { handleResponseError(it) }
            return result
        } catch (e: Exception) {
            ApiResponse(message = e.message)
        }
    }
}