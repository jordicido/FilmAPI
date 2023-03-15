package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Comment(val id: String, val filmId: String, val comment: String, val dateOfCreation: String)

