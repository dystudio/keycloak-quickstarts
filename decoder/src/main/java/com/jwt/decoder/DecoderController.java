package com.jwt.decoder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class DecoderController {

    @GetMapping("/try")
    public String decode() throws IOException, GeneralSecurityException {
        String token ="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiQ015QnRqZEk4ak1nYjlFdmlZLTdxNmxESktWd25ta2FWdHp2eURjdWNnIn0.eyJqdGkiOiIyYzYyOTU3Ni02NzIzLTQ2ZTctYjIzNi00ZjUyMTQ0MDEzMzAiLCJleHAiOjE1ODM5MzI5ODcsIm5iZiI6MCwiaWF0IjoxNTgzODk2OTkwLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODEvYXV0aC9yZWFsbXMvcGhvdG96IiwiYXVkIjoicGhvdG96LXJlc3RmdWwtYXBpIiwic3ViIjoiYmYxOGI4N2UtY2I2Yi00OWNkLTg3YjUtNWUwYjg2Nzc2ZWNiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGhvdG96LWh0bWw1LWNsaWVudCIsImF1dGhfdGltZSI6MTU4Mzg5Njk4Nywic2Vzc2lvbl9zdGF0ZSI6IjA2OTZkZGMyLTljNzUtNGNjYy1hMDhmLWVjMTc3ZTg0ODBhNiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgxIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJhZG1pbiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJwaG90b3otcmVzdGZ1bC1hcGkiOnsicm9sZXMiOlsibWFuYWdlLWFsYnVtcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIl19fSwiYXV0aG9yaXphdGlvbiI6eyJwZXJtaXNzaW9ucyI6W3sic2NvcGVzIjpbImFkbWluOm1hbmFnZSJdLCJyc2lkIjoiZDVkMzg4YTEtMTkxMC00MGZlLTlmY2YtMTI5ZWQ4NWI3NGY1IiwicnNuYW1lIjoiQWRtaW4gUmVzb3VyY2VzIn0seyJzY29wZXMiOlsicHJvZmlsZTp2aWV3Il0sInJzaWQiOiIxMjVhMDMwYy1mOTA5LTRlYTUtOTUxMy1jYTk5ODdjMDg2ZGUiLCJyc25hbWUiOiJVc2VyIFByb2ZpbGUgUmVzb3VyY2UifSx7InNjb3BlcyI6WyJhbGJ1bTp2aWV3IiwiYWxidW06ZGVsZXRlIl0sInJzaWQiOiI2NmEwNzMxMS1lMjkzLTQyNDctYjdjYy02MzcxZGM2YmQxZTMiLCJyc25hbWUiOiJBbGJ1bSBSZXNvdXJjZSJ9XX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFkbWluIElzdHJhdG9yIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiQWRtaW4iLCJmYW1pbHlfbmFtZSI6IklzdHJhdG9yIiwiZW1haWwiOiJhZG1pbkBhZG1pbi5jb20ifQ.liXiU8LFyzZeXxbRsJZY8AGYQEE94If6fnXXT-lyjwjIG7WIOSo60ofnUbI14PZge3ecEizWc5HMQmv8f5ZqVHHuwJKpVQG9YA4MufGC_Sh_4SmdFXn63LrRaOjtf6rFlIem2wwnjAaV9h6ccGp7hVh4efrj17-CUvbw5VJwg6dPIZeouqbdaQ6OrWskT7i3ZdDSIQDeLIgtwfsVODbE43dZmcOvo7fghZ6qk98cAf-roovCF5-WYjk09PowuLqvAZtgVYQIjZLLI3qQbRomcuzMtg5p4fx3fOg8tD2GPZnsDPpA6Rl17hWaj4C6F4ts8_9eLkUmL9-cvhg9FLyiIg";
        Jws<Claims> jws =  null;

        PublicKey publicKeyFromPEM = RSA.getPublicKey("E:\\YanFeng\\keycloak\\public.pem");

        try {
            jws = Jwts.parserBuilder()  // (1)
                    .setSigningKey(publicKeyFromPEM)         // (2)
                    .build()// (3)
                    .parseClaimsJws(token); // (4)

            // we can safely trust the JWT
        }
        catch (JwtException ex) {       // (5)

            // we *cannot* use the JWT as intended by its creator
            if (jws != null) {
                return "0" + jws.getBody();
            }else{
                return "1" + publicKeyFromPEM + "\\r\\n" + ex.getMessage();
            }

        }


        return jws.getBody().toString();
    }
}
