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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.example.photoz.entity.Album;
import org.keycloak.representations.idm.authorization.Permission;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@Path("/admin/album")
public class AdminAlbumService {
    @Context
    private HttpServletRequest request;
    @Inject
    private EntityManager entityManager;

    @GET
    @Produces("application/json")
    public Response findAll() {
        HashMap<String, List<Album>> albums = new HashMap<String, List<Album>>();
        List<Album> result = this.entityManager.createQuery("from Album").getResultList();


        AuthzClient authzClient = this.getAuthzClient();
        String rpt = request.getHeader("Authorization").replace("Bearer ", "");
        System.out.println("Trpt " + rpt);
           /*AuthorizationResponse response = authzClient.authorization("alice", "alice").authorize();
           String rpt = response.getToken();*/
        // introspect the token
        TokenIntrospectionResponse requestingPartyToken = authzClient.protection().introspectRequestingPartyToken(rpt);
        Boolean active = requestingPartyToken.getActive();
        ArrayList<String> collection = new ArrayList<String>();
        for (Permission granted : requestingPartyToken.getPermissions()) {
             collection.add(granted.toString());
        }
        for (Album album : result) {
            List<Album> userAlbums = albums.get(album.getUserId());

            if (userAlbums == null) {
                userAlbums = new ArrayList<Album>();
                album.setPermissions(collection);
                albums.put(album.getUserId(), userAlbums);
            }

            userAlbums.add(album);
        }



        return Response.ok(albums).build();
    }

    private AuthzClient getAuthzClient() {
        return getAuthorizationContext().getClient();
    }

    private ClientAuthorizationContext getAuthorizationContext() {
        return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext().getAuthorizationContext());
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return KeycloakSecurityContext.class.cast(request.getAttribute(KeycloakSecurityContext.class.getName()));
    }
}
