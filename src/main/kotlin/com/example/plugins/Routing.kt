package com.example.plugins

import com.example.routes.commentsRouting
import com.example.routes.filmRouting
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        filmRouting()
        commentsRouting()
    }
}
