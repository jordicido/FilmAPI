package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Film(val id: String,
                var title: String,
                var year: String,
                var genre: String,
                var director: String,
                var comments: MutableList<Comment>?)

val filmStorage = mutableListOf<Film>()