// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Future;

public class UsernamePasswordFlow {

    private final static String APP_ID = "Enter_the_Application_Id_here";
    private final static String AUTHORITY = "https://login.microsoftonline.com/organizations";

    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("Enter username: ");
            String userName = br.readLine();
            System.out.print("Enter password: ");
            String password = br.readLine();

            // Request access token from AAD
            IAuthenticationResult result = getAccessToken(userName, password);

            // Get user info from Microsoft Graph
            String userInfo = getUserInfoFromGraph(result.accessToken());
            System.out.print(userInfo);
        } catch(Exception ex){

            System.out.println("Oops! We have an exception of type - " + ex.getClass());
            System.out.println("Exception message - " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static IAuthenticationResult getAccessToken(String userName, String password) throws Exception {

            PublicClientApplication app =
                    PublicClientApplication
                            .builder(APP_ID)
                            .authority(AUTHORITY)
                            .build();

            Set<String> scopes = Collections.singleton("User.Read");
            UserNamePasswordParameters parameters =
                    UserNamePasswordParameters
                            .builder(scopes, userName, password.toCharArray())
                            .build();

            Future<IAuthenticationResult> result = app.acquireToken(parameters);
            return result.get();
        }

    private static String getUserInfoFromGraph(String accessToken) throws IOException{
        URL url = new URL("https://graph.microsoft.com/v1.0/me");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept", "application/json");

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == 200) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                String inputLine;
                response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }
}
