package com.anitracker.app.api

object Queries {
    val TRENDING_ANIME = """
        query {
            Page(page: 1, perPage: 20) {
                media(type: ANIME, sort: TRENDING_DESC) {
                    id
                    title {
                        romaji
                        english
                    }
                    coverImage {
                        large
                        extraLarge
                    }
                    averageScore
                    episodes
                    status
                    format
                }
            }
        }
    """.trimIndent()

    val POPULAR_THIS_SEASON = """
        query (${'$'}season: MediaSeason, ${'$'}year: Int) {
            Page(page: 1, perPage: 20) {
                media(type: ANIME, season: ${'$'}season, seasonYear: ${'$'}year, sort: POPULARITY_DESC) {
                    id
                    title {
                        romaji
                        english
                    }
                    coverImage {
                        large
                        extraLarge
                    }
                    averageScore
                    episodes
                    status
                    format
                }
            }
        }
    """.trimIndent()

    val UPCOMING_ANIME = """
        query {
            Page(page: 1, perPage: 20) {
                media(type: ANIME, status: NOT_YET_RELEASED, sort: POPULARITY_DESC) {
                    id
                    title {
                        romaji
                        english
                    }
                    coverImage {
                        large
                        extraLarge
                    }
                    averageScore
                    episodes
                    status
                    format
                }
            }
        }
    """.trimIndent()

    val TOP_RATED = """
        query {
            Page(page: 1, perPage: 20) {
                media(type: ANIME, sort: SCORE_DESC) {
                    id
                    title {
                        romaji
                        english
                    }
                    coverImage {
                        large
                        extraLarge
                    }
                    averageScore
                    episodes
                    status
                    format
                }
            }
        }
    """.trimIndent()

    val SEARCH_MEDIA = """
        query (${'$'}search: String, ${'$'}type: MediaType) {
            Page(page: 1, perPage: 30) {
                media(search: ${'$'}search, type: ${'$'}type, sort: POPULARITY_DESC) {
                    id
                    title {
                        romaji
                        english
                    }
                    coverImage {
                        large
                        extraLarge
                    }
                    averageScore
                    episodes
                    chapters
                    status
                    format
                    type
                }
            }
        }
    """.trimIndent()

    val MEDIA_DETAIL = """
        query (${'$'}id: Int) {
            Media(id: ${'$'}id) {
                id
                title {
                    romaji
                    english
                    native
                }
                coverImage {
                    large
                    extraLarge
                }
                bannerImage
                description(asHtml: false)
                episodes
                chapters
                status
                format
                season
                seasonYear
                averageScore
                meanScore
                popularity
                genres
                source
                type
                studios(isMain: true) {
                    nodes {
                        id
                        name
                    }
                }
                characters(page: 1, perPage: 10, sort: ROLE) {
                    nodes {
                        id
                        name {
                            full
                        }
                        image {
                            medium
                            large
                        }
                    }
                }
                recommendations(page: 1, perPage: 10) {
                    nodes {
                        mediaRecommendation {
                            id
                            title {
                                romaji
                                english
                            }
                            coverImage {
                                large
                            }
                            averageScore
                            episodes
                            format
                        }
                    }
                }
            }
        }
    """.trimIndent()
}
