---
page_type: sample
languages:
- powershell
- java
products:
- azure-active-directory
description: "Java console application letting users sign-in with username/password to call Microsoft Graph API"
urlFragment: active-directory-java-native-headless-v2
---

# Java console application letting users sign-in with username/password to call Microsoft Graph API

## About this sample

### Overview

This sample demonstrates how to use Microsoft Authentication Library for Java to:

- Authenticate the user silently using username and password.
- Call to a web API (in this case, the [Microsoft Graph](https://graph.microsoft.com))

![Topology](./ReadmeFiles/Java-Native-Diagram.png)

### Scenario

The application obtains a token through username and password, and then calls the Microsoft Graph to get information about the signed-in user.

Note that Username/Password is needed in some cases (for instance devops scenarios) but it's not recommended because:

- This requires having credentials in the application, which does not happen with the other flows.
- The credentials should only be used when there is a high degree of trust between the resource owner and the client and when other authorization grant types are not available (such as an authorization code).
- Do note that this attempts to authenticate and obtain tokens for users using this flow will often fail with applications registered with Azure AD. Some of the situations and scenarios that will cause the failure are listed below  
  - When the user needs to consent to permissions that this application is requesting.
  - When a conditional access policy enforcing multi-factor authentication is in force.
  - Azure AD Identity Protection can block authentication attempts if this user account is compromised.
  - The user's password is expired and requires a reset.

While this flow seems simpler than the others, applications using these flows often encounter more problems as compared to other flows.

The modern authentication protocols (SAML, WS-Fed, OAuth and OpenID), in principal, discourage apps from handling user credentials themselves. The aim is to decouple the authentication method from an app. Azure AD controls the login experience to avoid exposing secrets (like passwords) to a website or an app.

This enables IdPs like Azure AD to provide seamless single sign-on experiences, enable users to authenticate using factors other than passwords (phone, face, biometrics) and Azure AD can block or elevate authentication attempts if it discerns that the user’s account is compromised or the user is trying to access an app from an untrusted location and such.

## How to run this sample

To run this sample, you'll need:

- Working installation of Java and Maven.
- An Internet connection.
- An Azure Active Directory (Azure AD) tenant. For more information on how to get an Azure AD tenant, see [How to get an Azure AD tenant](https://azure.microsoft.com/en-us/documentation/articles/active-directory-howto-tenant/).
- A user account in your Azure AD tenant. This sample will not work with a Microsoft account (formerly Windows Live account). Therefore, if you signed in to the [Azure portal](https://portal.azure.com) with a Microsoft account and have never created a user account in your directory before, you need to do that now.

## Quick Start

Getting started with the sample is easy. It is configured to run out of the box with minimal setup.

### Step 1: Download Java (8 and above) for your platform

To successfully use this sample, you need a working installation of [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Maven](https://maven.apache.org/).

### Step 2:  Clone or download this repository

From your shell or command line:

```shell
`git clone https://github.com/Azure-Samples/active-directory-java-native-headless-v2.git `
```

### Step 3:  Register the sample with your Azure Active Directory tenant

To register the project, you can:

- either follow the steps in the paragraphs below
- or use PowerShell scripts that:
  - **automatically** create for you the Azure AD applications and related objects (passwords, permissions, dependencies)
  - modify the projects' configuration files.

If you want to use this automation, read the instructions in [App Creation Scripts](./AppCreationScripts/AppCreationScripts.md)

### First step: choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com).
1. On the top bar, click on your account, and then on **Switch Directory**.
1. Once the *Directory + subscription* pane opens, choose the Active Directory tenant where you wish to register your application, from the *Favorites* or *All Directories* list.
1. In the portal menu, click on **All services**, and choose **Azure Active Directory**.

> In the next steps, you might need the tenant name (or directory name) or the tenant ID (or directory ID). These are presented in the **Properties** of the Azure Active Directory window respectively as *Name* and *Directory ID*

#### Register the app app (Java-Console-Application)

1. In **App registrations** page, select **New registration**.
1. When the **Register an application page** appears, enter your application's registration information:
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `Java-Console-Application`.
   - In the **Supported account types** section, select **Accounts in any organizational directory**.
1. Select **Register** to create the application.
1. On the app **Overview** page, find the **Application (client) ID** value and record it for later. You'll need it to configure the APP_ID value in PublicClient.Java later.
1. In the Application menu blade, select **Manifest**, and:
   - In the manifest editor, set the `allowPublicClient` property to **true**
   - Select **Save** in the bar above the manifest editor.
1. In the Application menu blade, select **API permissions**
   - Ensure that the **User.Read** permission is listed in the permissions list (which is automatically added when you register your application).

1. At this stage permissions are assigned correctly but the client app does not allow interaction.
   Therefore no consent can be presented via a UI and accepted to use the service app.
   Click the **Grant/revoke admin consent for {tenant}** button, and then select **Yes** when you are asked if you want to grant consent for the requested permissions for all account in the tenant.
   You need to be an Azure AD tenant admin to do this.

### Step 4:  Configure the sample to use your Azure AD tenant

In the steps below, ClientID is the same as Application ID or AppId.

#### Configure the app project

1. Open the `src\main\java\PublicClient.java` file.
1. Find the line `private final static String APP_ID` and replace the existing value with the **Application ID (clientId)** of the `Java-Console-Application` application copied from the Azure portal.

### Step 5: Run the sample

From your shell or command line:

- `$ mvn package`

This will generate a `public-client-msal4j-sample-jar-with-dependencies.jar` file in your /targets directory. Run this using your Java executable like below:

- `$ java -jar public-client-msal4j-sample-jar-with-dependencies.jar`

### You're done

Your command line interface should prompt you for the username and password and then access the Microsoft Graph API to retrieve your user information.

### About the code

The code to acquire a token is located entirely in the `src\main\java\PublicClient.Java` file. The public client application is created using the **MSAL build pattern**, by passing the Application Id and the Authority.

```java
            PublicClientApplication pca = PublicClientApplication.builder(
                    APP_ID).
                    authority(AUTHORITY).build();

```

A call to acquire the token is made using the public client application, by creating an `UserNamePasswordParameters` object. The builder takes in scope (in this case `User.Read`), and the username and password of the user.

```java

            String scopes = "User.Read";
            UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(
                    Collections.singleton(scopes),
                    userName,
                    password.toCharArray()).build();
```

The result is passed back to the main() function, where then the access token is extracted and passed to the function making the call to Microsoft Graph me endpoint ("https://graph.microsoft.com/v1.0/me")

The access token is then used as a bearer token to call the Microsoft Graph API (line 68)

`conn.setRequestProperty("Authorization", "Bearer " + accessToken);`

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/adal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`msal` `Java`].

If you find a bug in the sample, please raise the issue on [GitHub Issues](https://github.com/Azure-Samples/active-directory-java-native-headless-v2/issues).

To provide a recommendation, visit the following [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contributing

If you'd like to contribute to this sample, see CONTRIBUTING.md

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## More information

For more information, see MSAL4J [conceptual documentation](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki).

For more information on how add additional permissions to use Microsoft Graph notifications, see [API permissions](https://docs.microsoft.com/en-us/graph/notifications-integration-app-registration#api-permissions)

For more information about how OAuth 2.0 protocols work in this scenario and other scenarios, see [Authentication Scenarios for Azure AD](http://go.microsoft.com/fwlink/?LinkId=394414).
