package com.github.diarmaidlindsay.myanimequiz.data.service

import com.github.diarmaidlindsay.myanimequiz.data.model.ApiResponse
import com.github.diarmaidlindsay.myanimequiz.data.model.User
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeDetails
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeList
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeRanking
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.AnimeSeasonal
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.MyAnimeListStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.anime.UserAnimeList
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.MangaDetails
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.MangaList
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.MangaRanking
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.MyMangaListStatus
import com.github.diarmaidlindsay.myanimequiz.data.model.manga.UserMangaList
import com.github.diarmaidlindsay.myanimequiz.utils.Constants.MAL_API_URL
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface ApiService {

    // Anime
    @GET("${MAL_API_URL}anime")
    suspend fun getAnimeList(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int?,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<AnimeList>>

    @GET
    suspend fun getAnimeList(@Url url: String): ApiResponse<List<AnimeList>>

    @GET("${MAL_API_URL}anime/season/{year}/{season}")
    suspend fun getSeasonalAnime(
        @Path("year") year: Int,
        @Path("season") season: String,
        @Query("sort") sort: String,
        @Query("limit") limit: Int,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<AnimeSeasonal>>

    @GET
    suspend fun getSeasonalAnime(@Url url: String): ApiResponse<List<AnimeSeasonal>>

    @GET("${MAL_API_URL}anime/ranking")
    suspend fun getAnimeRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit") limit: Int,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<AnimeRanking>>

    @GET
    suspend fun getAnimeRanking(@Url url: String): ApiResponse<List<AnimeRanking>>

    @GET("${MAL_API_URL}anime/suggestions")
    suspend fun getAnimeRecommendations(
        @Query("limit") limit: Int,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<AnimeList>>

    @GET
    suspend fun getAnimeRecommendations(@Url url: String): ApiResponse<List<AnimeList>>

    @GET("${MAL_API_URL}users/@me/animelist")
    suspend fun getUserAnimeList(
        @Query("status") status: String,
        @Query("sort") sort: String,
        @Query("limit") limit: Int?,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<UserAnimeList>>

    @GET
    suspend fun getUserAnimeList(@Url url: String): ApiResponse<List<UserAnimeList>>

    @FormUrlEncoded
    @PATCH("${MAL_API_URL}anime/{animeId}/my_list_status")
    suspend fun updateUserAnimeList(
        @Path("animeId") animeId: Int,
        @Field("status") status: String?,
        @Field("score") score: Int?,
        @Field("num_watched_episodes") watchedEpisodes: Int?,
        @Field("start_date") startDate: String?,
        @Field("finish_date") endDate: String?,
        @Field("is_rewatching") isRewatching: Boolean?,
        @Field("num_times_rewatched") numRewatches: Int?,
        @Field("rewatch_value") rewatchValue: Int?,
        @Field("priority") priority: Int?,
        @Field("tags") tags: String?,
        @Field("comments") comments: String?
    ): MyAnimeListStatus

    @DELETE("anime/{animeId}/my_list_status")
    suspend fun deleteAnimeEntry(@Path("animeId") animeId: Int): ApiResponse<Unit>

    @GET("${MAL_API_URL}anime/{animeId}")
    suspend fun getAnimeDetails(
        @Path("animeId") animeId: Int,
        @Query("fields") fields: String?
    ): AnimeDetails

    @GET("${MAL_API_URL}anime/{animeId}/characters")
    suspend fun getAnimeCharacters(
        @Path("animeId") animeId: Int,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("fields") fields: String?
    ): ApiResponse<List<Character>>

    @GET
    suspend fun getAnimeCharacters(@Url url: String): ApiResponse<List<Character>>

    // Manga
    @GET("${MAL_API_URL}manga")
    suspend fun getMangaList(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int?,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<MangaList>>

    @GET
    suspend fun getMangaList(@Url url: String): ApiResponse<List<MangaList>>

    @GET("${MAL_API_URL}users/@me/mangalist")
    suspend fun getUserMangaList(
        @Query("status") status: String,
        @Query("sort") sort: String,
        @Query("limit") limit: Int?,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<UserMangaList>>

    @GET
    suspend fun getUserMangaList(@Url url: String): ApiResponse<List<UserMangaList>>

    @GET("${MAL_API_URL}manga/ranking")
    suspend fun getMangaRanking(
        @Query("ranking_type") rankingType: String,
        @Query("limit") limit: Int,
        @Query("nsfw") nsfw: Int,
        @Query("fields") fields: String?
    ): ApiResponse<List<MangaRanking>>

    @GET
    suspend fun getMangaRanking(@Url url: String): ApiResponse<List<MangaRanking>>

    @FormUrlEncoded
    @PATCH("${MAL_API_URL}manga/{mangaId}/my_list_status")
    suspend fun updateUserMangaList(
        @Path("mangaId") mangaId: Int,
        @Field("status") status: String?,
        @Field("score") score: Int?,
        @Field("num_chapters_read") chaptersRead: Int?,
        @Field("num_volumes_read") volumesRead: Int?,
        @Field("start_date") startDate: String?,
        @Field("finish_date") endDate: String?,
        @Field("is_rereading") isRereading: Boolean?,
        @Field("num_times_reread") numRereads: Int?,
        @Field("reread_value") rereadValue: Int?,
        @Field("priority") priority: Int?,
        @Field("tags") tags: String?,
        @Field("comments") comments: String?
    ): MyMangaListStatus

    @DELETE("manga/{mangaId}/my_list_status")
    suspend fun deleteMangaEntry(@Path("mangaId") mangaId: Int): ApiResponse<Unit>

    @GET("${MAL_API_URL}manga/{mangaId}")
    suspend fun getMangaDetails(
        @Path("mangaId") mangaId: Int,
        @Query("fields") fields: String?
    ): MangaDetails

    // User
    @GET("${MAL_API_URL}users/@me")
    suspend fun getUser(@Query("fields") fields: String?): User
}