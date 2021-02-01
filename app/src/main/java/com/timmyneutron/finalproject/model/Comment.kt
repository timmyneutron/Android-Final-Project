package com.timmyneutron.finalproject.model

data class Comment (
        var commentor: String? = null,
        var body: String? = null,
        var voteScore: Int = 0,
        var placeID: String? = null,
        var id: String = "",
)