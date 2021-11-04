package com.migueljteixeira.clipmobile.network

import android.content.Context
import kotlin.Throws
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import org.jsoup.Jsoup
import com.migueljteixeira.clipmobile.settings.ClipSettings
import org.jsoup.Connection
import org.jsoup.nodes.Document
import java.io.IOException
import java.lang.Exception

abstract class Request {
    companion object {
        private const val ID = "identificador"
        private const val PW = "senha"
        private const val INITIAL_REQUEST = "https://clip.unl.pt/utente/eu"
        const val COOKIE_NAME = "JServSessionIdroot1112"
        @JvmStatic
        @Throws(ServerUnavailableException::class)
        protected fun requestNewCookie(
            context: Context,
            username: String,
            password: String
        ): Document {
            return try {
                val response = Jsoup.connect(INITIAL_REQUEST)
                    .data(ID, username)
                    .data(PW, password)
                    .method(Connection.Method.POST)
                    .header(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
                    )
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en-US,en;q=0.8,pt-PT;q=0.6,pt;q=0.4")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Host", "clip.unl.pt")
                    .header("Origin", "https://clip.unl.pt")
                    .header("Referer", "https://clip.unl.pt/utente/eu")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
                    .timeout(30000)
                    .execute()

                // Save cookie
                ClipSettings.saveCookie(context, response.cookie(COOKIE_NAME))

                // Save login time
                ClipSettings.saveLoginTime(context)
                response.parse()
            } catch (e: IOException) {
                throw ServerUnavailableException()
            }
        }

        @JvmStatic
        @Throws(ServerUnavailableException::class)
        protected fun request(context: Context, url: String): Document {
            return try {
                val connection = Jsoup.connect(url)
                    .header(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
                    )
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en-US,en;q=0.8,pt-PT;q=0.6,pt;q=0.4")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Host", "clip.unl.pt")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
                    .timeout(30000)

                // If the cookie has expired, we need to request a new one
                if (ClipSettings.isTimeForANewCookie(context)) requestNewCookie(
                    context, ClipSettings.getLoggedInUserName(context)!!,
                    ClipSettings.getLoggedInUserPw(context)!!
                )
                val response: Connection.Response = sendRequestWithCookie(context, connection)

                //System.out.println("Request - url:" + url);
                response.parse()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }

        @Throws(IOException::class, ServerUnavailableException::class)
        private fun sendRequestWithCookie(
            context: Context,
            connection: Connection
        ): Connection.Response {
            connection.header("Cookie", COOKIE_NAME + "=" + ClipSettings.getCookie(context))

            // Execute the request
            val response: Connection.Response = connection.execute()
            //        response = connection.execute().bufferUp();

            // If clip website returns, for some reason,
            // the login page, request new cookie
            val inputs = response.parse() //        Elements inputs = response.parse()
                .body().getElementsByTag("input")
            for (input in inputs) if (input.attr("name") == ID || input.attr("name") == PW) {
//                Crashlytics.log("Request - Requesting with user data");
                println("Request - Requesting with user data")
                requestNewCookie(
                    context, ClipSettings.getLoggedInUserName(context)!!,
                    ClipSettings.getLoggedInUserPw(context)!!
                )
                return sendRequestWithCookie(context, connection)
            }
            return response
        }
    }
}