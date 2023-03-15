package com.example.routes

import com.example.models.Comment
import com.example.models.filmStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.commentsRouting() {
    route("/films/{id?}/comments") {
        get {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val film = filmStorage.find { it.id == id } ?: return@get call.respondText(
                "Film with id $id not found",
                status = HttpStatusCode.NotFound
            )
            if (film.comments.isNullOrEmpty()) call.respondText("No comments on this film", status = HttpStatusCode.OK)
            else call.respond(film.comments!!)
        }
        post {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val film = filmStorage.find { it.id == id } ?: return@post call.respondText(
                "Film with id $id not found",
                status = HttpStatusCode.NotFound
            )
            val comment = call.receive<Comment>()
            if (film.comments.isNullOrEmpty()) {
                film.comments = mutableListOf(comment)
            } else film.comments!!.add(comment)
            call.respondText("Comment added correctly", status = HttpStatusCode.Accepted)
        }
    }
}