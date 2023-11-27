package com.example.dao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
public class GoogleAuth {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Insira manualmente as informações de credenciais abaixo
    private static final String CLIENT_ID = "407870711761-5rnvngceb376369bc545a7i16vkaom2k.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                        .setClientId(CLIENT_ID)
                        .setClientSecret(CLIENT_SECRET));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singleton("https://www.googleapis.com/auth/userinfo.email"))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static String[] getCredentials() throws IOException, GeneralSecurityException {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = getCredentials(HTTP_TRANSPORT);

        // Fazendo uma requisição para obter informações do usuário autenticado
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
        GenericUrl url = new GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo"); // Endpoint para informações do usuário
        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse response = request.execute();

        // Lendo a resposta JSON e obtendo e-mail e ID do usuário
        String jsonResponse = response.parseAsString();

        // Parsing do JSON usando Gson
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String userEmail = jsonObject.get("email").getAsString();
        String userName = null;
        String userId = jsonObject.get("id").getAsString();

        int index = userEmail.indexOf('@');

        if (index != -1) { // Verifica se o caractere '@' foi encontrado
            userName = userEmail.substring(0, index); // Obtém a parte antes do '@'
        } else {
            System.out.println("Endereço de e-mail inválido.");
        }

        response.disconnect();

        return new String[]{userName, userEmail, userId};
    }
}