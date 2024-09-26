package io.mocha.duty.data.net

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mocha.duty.data.getCurrentLocalDate
import io.mocha.duty.data.model.Duty
import io.mocha.duty.data.model.Student
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

object WebReq {
    private val client by lazy {
        HttpClient {
            defaultRequest {
                url(BASEURL)

            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }
    }

    suspend fun reqDuty(date: String? = null): Duty {
        val mDate = date ?: getCurrentLocalDate()
        return client.get("/duty/$mDate").body()
    }

    suspend fun reqAllDuty(): List<Student> {
        return client.get("/duty/all").body()
    }

    suspend fun postTodayDuty(name: String): Message {
        val response = client.post("/duty/today") {
            contentType(ContentType.Application.Json)
            setBody(PostStudent(name))
        }
        return response.body()
    }

    suspend fun postEditDuty(
        password: String,
        date: String? = null,
        name: String? = null,
        isDutyNeeded: Boolean = !name.isNullOrBlank()
    ): Message {
        val mDate = date ?: getCurrentLocalDate()
        val response = client.post("/duty/set") {
            contentType(ContentType.Application.Json)
            setBody(PostEdit(mDate, name, isDutyNeeded, password))
        }
        return response.body()
    }





    private const val BASEURL = ""
}