// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class UsernamePasswordFlow {

    public static void main(String args[]) throws Exception {

        IAuthenticationResult result = acquireTokenUsernamePassword();
        System.out.println("Access token: " + result.accessToken());
        System.out.println("Id token: " + result.idToken());
        System.out.println("Account username: " + result.account().username());
    }

    private static IAuthenticationResult acquireTokenUsernamePassword() throws Exception {
        //Load properties file and set properties used throughout the sample
        Properties properties = new Properties();
        properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "application.properties"));
        String authority = properties.getProperty("AUTHORITY");
        String accounts = properties.getProperty("ACCOUNT");
        String clientId = properties.getProperty("CLIENT_ID");
        String userName = properties.getProperty("USER_NAME");
        String password = properties.getProperty("USER_PASSWORD");
        Set<String> scope = Collections.singleton("");

        TokenCacheAspect tokenCacheAspect = new TokenCacheAspect(accounts);

        PublicClientApplication pca = PublicClientApplication.builder(clientId)
                .authority(authority)
                .setTokenCacheAccessAspect(tokenCacheAspect)
                .build();

        Set<IAccount> accountsInCache = pca.getAccounts().join();
        // Take first account in the cache. In a production application, you would filter
        // accountsInCache to get the right account for the user authenticating.
        IAccount account = accountsInCache.iterator().next();

        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scope, account)
                            .build();
            // try to acquire token silently. This call will fail since the token cache
            // does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {

                UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, userName, password.toCharArray())
                                .build();
                // Try to acquire a token via username/password. If successful, you should see
                // the token and account information printed out to console
                result = pca.acquireToken(parameters).join();
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }
        return result;
    }
}