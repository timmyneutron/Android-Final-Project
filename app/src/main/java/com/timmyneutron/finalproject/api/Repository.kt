package com.timmyneutron.finalproject.api

class Repository(private val api: NYTimesAPI) {
    // XXX Write me.
    suspend fun fetchHeadlines(q: String) : List<NYTimesAPI.urlHeadline> {
        val urlsAndHeadlines = mutableListOf<NYTimesAPI.urlHeadline>()
        api.fetchHeadlines(q).response.docs.forEach {
            urlsAndHeadlines.add(NYTimesAPI.urlHeadline(it.web_url, it.headline.main))
        }
        return urlsAndHeadlines
    }
}