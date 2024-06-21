package com.waekaizo.storyapp_submission

import com.waekaizo.storyapp_submission.data.api.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "name + $i",
                photoUrl = "photo $i",
                description = "description $i",
                createdAt = "cretedAt $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}