package com.anitracker.app.model

data class MediaResponse(
    val data: MediaData
)

data class MediaData(
    val Page: PageInfo? = null,
    val Media: Media? = null
)

data class PageInfo(
    val pageInfo: PageDetails? = null,
    val media: List<Media>? = null
)

data class PageDetails(
    val total: Int = 0,
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val hasNextPage: Boolean = false
)

data class Media(
    val id: Int,
    val title: MediaTitle,
    val coverImage: CoverImage,
    val bannerImage: String? = null,
    val description: String? = null,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val status: String? = null,
    val format: String? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val averageScore: Int? = null,
    val meanScore: Int? = null,
    val popularity: Int? = null,
    val genres: List<String>? = null,
    val studios: Studios? = null,
    val type: String? = null,
    val source: String? = null,
    val characters: Characters? = null,
    val recommendations: Recommendations? = null
)

data class MediaTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null
) {
    fun preferred(): String {
        return english ?: romaji ?: native ?: "Unknown"
    }
}

data class CoverImage(
    val large: String? = null,
    val medium: String? = null,
    val extraLarge: String? = null
) {
    fun best(): String? = extraLarge ?: large ?: medium
}

data class Studios(
    val nodes: List<Studio>? = null
)

data class Studio(
    val id: Int,
    val name: String
)

data class Characters(
    val nodes: List<Character>? = null
)

data class Character(
    val id: Int,
    val name: CharacterName,
    val image: CharacterImage? = null
)

data class CharacterName(
    val full: String? = null
)

data class CharacterImage(
    val medium: String? = null,
    val large: String? = null
)

data class Recommendations(
    val nodes: List<RecommendationNode>? = null
)

data class RecommendationNode(
    val mediaRecommendation: Media? = null
)
