package com.sleazyweasel.eboshi

import com.google.gson.Gson
import spark.Request
import spark.Response
import spark.Spark
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * A wrapper around Spark static methods that provides json handling.
 */
class SparkGson @Inject constructor(private val gson: Gson) {
    val gsonTransformer: (Any) -> String = { gson.toJson(it) }

    val jsonApiType = "application/vnd.api+json"

    fun <T : Any, R> post(path: String, requestBodyClass: KClass<T>, handler: (input: T, res: Response) -> R) {
        Spark.post(path, "application/json", { request, response ->
            response.type(jsonApiType)
            val t = gson.fromJson(request.body(), requestBodyClass)
            handler(t, response)
        }, gsonTransformer)
    }

    fun <R> get(path: String, handler: () -> R) {
        Spark.get(path, { request, response ->
            response.type(jsonApiType)
            handler()
        }, gsonTransformer)
    }

    fun <R> get(path: String, route: (request: Request, response: Response) -> R) {
        Spark.get(path, { request, response ->
            response.type(jsonApiType)
            route(request, response)
        }, gsonTransformer)
    }

    fun delete(path: String, route: (request: Request, response: Response) -> Any?) = Spark.delete(path, route)
    fun port(port: Int) = Spark.port(port)
    fun getPlain(path: String, route: (request: Request, response: Response) -> Any?) = Spark.get(path, route)

}

