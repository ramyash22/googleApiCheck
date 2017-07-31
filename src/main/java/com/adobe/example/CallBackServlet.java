package com.adobe.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CallBackServlet extends javax.servlet.http.HttpServlet {

    private static final String CLIENT_ID = "231133919996-btnm5ne6dtd0n7fo78v3ajqp0soh270q.apps.googleusercontent.com";

    private static final String CLIENT_SECRET = "PzhN7kLe9pYCtlHN81WGtBt4";

    private static final String REDIRECT_URI = "http://localhost:8080/callBack";

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpTransport netTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleTokenResponse googleTokenResponse =
                new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),CLIENT_ID,CLIENT_SECRET, request.getParameter("code"), REDIRECT_URI)
                        .execute();
        out.println("======================= Token Details ======================================");
        out.println("Access token: " + googleTokenResponse.getAccessToken());
        out.println("Refresh Access token: " + googleTokenResponse.getRefreshToken());
        out.println("Expired in: " + googleTokenResponse.getExpiresInSeconds());
        out.println("Token Type : " + googleTokenResponse.getTokenType());
        //Credential credential = new Credential().setAccessToken(googleTokenResponse.getAccessToken())
        GoogleCredential credential = new GoogleCredential().setAccessToken(googleTokenResponse.getAccessToken());
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("engagedev").build();
        credential.refreshToken();
        Userinfoplus userinfo = oauth2.userinfo().get().execute();
        out.println("======================= User Details ======================================");
        out.println(userinfo.toPrettyString());

        HttpTransport transport = null;
        try {
            transport = new MyOwnTransport(getHttpClient());
        }catch (Exception e){
            e.printStackTrace();
        }
        //Setting our httpclient



        YouTube youTube = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName("engagedev").build();

        YouTube.Channels.List channelRequest = youTube.channels().list("id,contentDetails,snippet");
        channelRequest.setMine(true);
        channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
        ChannelListResponse channelResult = channelRequest.execute();
        List<Channel> channelsList = channelResult.getItems();
        if (channelsList != null) {
            for (Channel channel:channelsList) {
                // The user's default channel is the first item in the list.
                // Extract the playlist ID for the channel's videos from the
                // API response.
                out.println("======================= Channel Details ======================================");
                out.println(channel.toPrettyString());
                String uploadPlaylistId = channel.getContentDetails().getRelatedPlaylists().getUploads();

                // Define a list to store items in the list of uploaded videos.
                List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

                // Retrieve the playlist of the channel's uploaded videos.
                YouTube.PlaylistItems.List playlistItemRequest =
                        youTube.playlistItems().list("id,contentDetails,snippet");
                playlistItemRequest.setPlaylistId(uploadPlaylistId);

                // Only retrieve data used in this application, thereby making
                // the application more efficient. See:
                // https://developers.google.com/youtube/v3/getting-started#partial
                playlistItemRequest.setFields(
                        "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");

                String nextToken = "";

                // Call the API one or more times to retrieve all items in the
                // list. As long as the API response returns a nextPageToken,
                // there are still more items to retrieve.
                do {
                    playlistItemRequest.setPageToken(nextToken);
                    PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                    playlistItemList.addAll(playlistItemResult.getItems());

                    nextToken = playlistItemResult.getNextPageToken();
                } while (nextToken != null);

                // Prints information about the results.
                prettyPrint(playlistItemList.size(), playlistItemList.iterator(),out);

            }
        } else {
            System.out.println("No channels are assigned to this user.");
        }
    }

    private static void prettyPrint(int size, Iterator<PlaylistItem> playlistEntries,PrintWriter out) {
        out.println("=============================================================");
        out.println("\t\tTotal Videos Uploaded: " + size);
        out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {
            PlaylistItem playlistItem = playlistEntries.next();
            out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());
            out.println("\n-------------------------------------------------------------\n");
        }
    }

    public HttpClient getHttpClient() {
        // TODO add implementation of circuit breaker http client
        int timeoutInMills = 10000;

        CloseableHttpClient httpClient = HttpClientBuilder.create().disableAutomaticRetries()
                .setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT)
                        .setSocketTimeout(timeoutInMills).setConnectTimeout(timeoutInMills)
                        .setConnectionRequestTimeout(timeoutInMills).build())
                .build();

        return httpClient;
    }
}
