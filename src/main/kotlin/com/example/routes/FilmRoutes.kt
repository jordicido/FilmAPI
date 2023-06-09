package com.example.routes

import com.example.models.Film
import com.example.models.filmStorage
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

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
        get("{id?}/cover") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val film = filmStorage.find { it.id == id } ?: return@get call.respondText(
                "Film with id $id not found",
                status = HttpStatusCode.NotFound
            )
            call.respondFile(File(film.cover))
        }
        post {
            val multipartData = call.receiveMultipart()
            val film = Film("","","","","","", mutableListOf())
            multipartData.forEachPart {
                when(it) {
                    is PartData.FormItem -> {
                        when(it.name) {
                            "id" -> film.id = it.value
                            "title"  -> film.title = it.value
                            "year"  -> film.year = it.value
                            "genre"  -> film.genre = it.value
                            "director"  -> film.director = it.value
                        }
                    }
                    is PartData.FileItem -> {
                        val pathToImg = "uploads/"
                        if (it.originalFileName == "") {
                            film.cover = "default-movie.jpg"
                        } else {
                            film.cover =  it.originalFileName as String
                            var fileBytes = it.streamProvider().readBytes()
                            File("uploads/${film.cover}").writeBytes(fileBytes)
                            film.cover = pathToImg + film.cover
                        }
                    }
                    else -> {}
                }
            }
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