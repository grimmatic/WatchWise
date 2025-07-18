package com.vakifbank.WatchWise.utils

class Constant {
    object ApiConstants {
        const val TMDB_API_KEY = "bf8dfeb60d4fcde2769766fc2ed459d0"
        const val TMBD_API_BASE_URL = "https://api.themoviedb.org/3/"
        const val PARAM_API_KEY = "api_key"
        const val PARAM_PAGE = "page"
        const val PARAM_LANGUAGE = "language"
        const val PARAM_QUERY = "query"
        const val LANGUAGE_TR = "tr-TR"
        const val LANGUAGE_EN = "en-US"
        const val DEFAULT_PAGE = 1
    }

    object TimeOutConstants {
        const val READ_TIMEOUT = 300L
        const val WRITE_TIMEOUT = 300L
    }

    object MovieConstants {
        const val MOVIE_ID = "id"
        const val MOVIE_TITLE = "title"
        const val MOVIE_POSTER = "poster"
        const val MOVIE_DESCRIPTION = "description"
        const val MOVIE_RATING = "rating"
        const val MOVIE_TAGLINE = "tagline"
        const val MOVIE_ADDED_AT = "addedAt"
    }

    object ReviewConstants {
        const val REVIEW_ID = "reviewId"
        const val REVIEWS = "reviews"
        const val RATING = "rating"
        const val COMMENT = "comment"
        const val TIMESTAMP = "timestamp"
        const val MOVIE_ID = "movieId"
        const val USER_ID = "userId"
        const val USER_EMAIL = "userEmail"
        const val AVERAGE_RATING = "averageRating"
        const val TOTAL_REVIEWS = "totalReviews"
        const val RATING_DISTRIBUTION = "ratingDistribution"
    }
}