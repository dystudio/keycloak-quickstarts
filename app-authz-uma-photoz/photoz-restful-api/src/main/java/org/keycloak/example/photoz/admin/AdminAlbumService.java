/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.example.photoz.admin;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
/*import java.util.Base64;*/
import java.util.Date;
import java.util.Vector;
import java.util.Map;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.keycloak.example.photoz.entity.Album;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@Path("/admin/album")
public class AdminAlbumService {

    @Inject
    private EntityManager entityManager;

    @GET
    @Produces("application/json")
    public Response findAll() throws IOException, GeneralSecurityException {
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
                return Response.ok("0" + jws.getBody()).build();
            }else{
                return Response.ok("1" + publicKeyFromPEM + "\\r\\n" + ex.getMessage()).build();
            }

        }


        HashMap<String, List<Album>> albums = new HashMap<String, List<Album>>();
        List<Album> result = this.entityManager.createQuery("from Album").getResultList();

        for (Album album : result) {
            List<Album> userAlbums = albums.get(album.getUserId());

            if (userAlbums == null) {
                userAlbums = new ArrayList<Album>();
                albums.put(album.getUserId(), userAlbums);
            }
            album.setClaims(jws.getBody().toString());
            userAlbums.add(album);
        }

        return Response.ok(albums).build();
    }
}
