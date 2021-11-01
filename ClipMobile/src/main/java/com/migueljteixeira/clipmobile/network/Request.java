package com.migueljteixeira.clipmobile.network;

import android.content.Context;

//import com.crashlytics.android.Crashlytics;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public abstract class Request {
    private static final String ID = "identificador";
    private static final String PW = "senha";
    private static final String INITIAL_REQUEST = "https://clip.unl.pt/utente/eu";
    protected static final String COOKIE_NAME = "JServSessionIdroot1112";

    protected static Document requestNewCookie(Context context, String username, String password)
            throws ServerUnavailableException {

        try {
            Connection.Response response = Jsoup.connect(INITIAL_REQUEST)
                    .data(ID, username)
                    .data(PW, password)
                    .method(Connection.Method.POST)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
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
                    .execute();

            // Save cookie
            ClipSettings.saveCookie(context, response.cookie(COOKIE_NAME));

            // Save login time
            ClipSettings.saveLoginTime(context);

            return response.parse();

        } catch (IOException e) {
            throw new ServerUnavailableException();
        }

    }

    protected static Document request(Context context, String url) throws ServerUnavailableException {

        try {
            Connection connection = Jsoup.connect(url)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en-US,en;q=0.8,pt-PT;q=0.6,pt;q=0.4")
                    .header("Cache-Control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Host", "clip.unl.pt")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
                    .timeout(30000);

            // If the cookie has expired, we need to request a new one
            Connection.Response response;
            if( ClipSettings.isTimeForANewCookie(context) )
                requestNewCookie(context, ClipSettings.getLoggedInUserName(context),
                        ClipSettings.getLoggedInUserPw(context));
            
            response = sendRequestWithCookie(context, connection);

            //System.out.println("Request - url:" + url);

            return response.parse();
        } catch (Exception e) {
            throw new ServerUnavailableException();
        }

    }

    private static Connection.Response sendRequestWithCookie(Context context, Connection connection) throws IOException, ServerUnavailableException {
        connection.header("Cookie", COOKIE_NAME + "=" + ClipSettings.getCookie(context));

        // Execute the request
        Connection.Response response;
        response = connection.execute();

        // If clip website returns, for some reason,
        // the login page, request new cookie
        Elements inputs = response.parse()
                .body().getElementsByTag("input");
        
        for(Element input : inputs)
            if(input.attr("name").equals(ID) || input.attr("name").equals(PW)) {
//                Crashlytics.log("Request - Requesting with user data");
                System.out.println("Request - Requesting with user data");

                requestNewCookie(context, ClipSettings.getLoggedInUserName(context),
                        ClipSettings.getLoggedInUserPw(context));
                
                return sendRequestWithCookie(context, connection);
            }
        
        return response;
    }

}
