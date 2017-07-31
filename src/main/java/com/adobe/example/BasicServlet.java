package com.adobe.example;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


public class BasicServlet extends javax.servlet.http.HttpServlet {

    private static final String CLIENT_ID = "231133919996-btnm5ne6dtd0n7fo78v3ajqp0soh270q.apps.googleusercontent.com";

    private static final String CLIENT_SECRET = "PzhN7kLe9pYCtlHN81WGtBt4";

    private static final String REDIRECT_URI = "http://localhost:8080/callBack";

    private static final Collection<String> SCOPE = Arrays.asList(("https://www.googleapis.com/auth/youtube;" +
            "https://www.googleapis.com/auth/youtube.readonly;https://www.googleapis.com/auth/userinfo.profile").split(";"));

    private AuthorizationCodeFlow flow;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String url = new GoogleAuthorizationCodeRequestUrl(
                CLIENT_ID, REDIRECT_URI,SCOPE).setApprovalPrompt("force").setAccessType("offline").setState("1234321").build();
        System.out.print(url);
        response.sendRedirect(url);
    }
}
