package com.example.routes

import com.example.models.Film
import com.example.models.filmStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.filmRouting() {
    route("/films") {
        get {
            if (filmStorage.isNotEmpty()) call.respond(filmStorage)
            else call.respondText("List of films is empty", status = HttpStatusCode.OK)
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val film = filmStorage.find { it.id == id } ?: return@get call.respondText(
                "Film with id $id not found",
                status = HttpStatusCode.NotFound
            )
            call.respond(film)
        }
        post {
            val film = call.receive<Film>()
            filmStorage.add(film)
            call.respondText("Film added correctly", status = HttpStatusCode.Accepted)
        }
        put("{id?}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val filmUpdated = call.receive<Film>()
            for (film in filmStorage) {
                if (film.id == id) {
                    film.title = filmUpdated.title
                    film.genre = filmUpdated.genre
                    film.director = filmUpdated.director
                    film.year = filmUpdated.year
                    return@put call.respondText("Film updated", status = HttpStatusCode.Accepted)
                }
            }
            call.respondText(
                "Film with id $id not found",
                status = HttpStatusCode.NotFound
            )

        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            if (filmStorage.removeIf { it.id == id }) call.respondText("Film deleted", status = HttpStatusCode.Accepted)
            else call.respondText("Film with id $id not found", status = HttpStatusCode.NotFound)
        }

    }
}