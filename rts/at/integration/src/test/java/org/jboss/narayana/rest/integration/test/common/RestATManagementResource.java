/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.rest.integration.test.common;

import org.jboss.narayana.rest.integration.ParticipantInformation;
import org.jboss.narayana.rest.integration.ParticipantsContainer;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.Map;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@Path(RestATManagementResource.BASE_URL_SEGMENT)
public class RestATManagementResource {

    public static final String BASE_URL_SEGMENT = "rest-at-management-resource";

    public static final String PARTICIPANTS_URL_SEGMENT = "participants";

    @GET
    @Path(PARTICIPANTS_URL_SEGMENT)
    public String getAllParticipantsInformation() {
        final Map<String, ParticipantInformation> participantsInformation =
                ParticipantsContainer.getInstance().getAllParticipantsInformation();

        return participantsInformationToJSON(participantsInformation).toString();
    }

    private JsonArray participantsInformationToJSON(final Map<String, ParticipantInformation> participantsInformation) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (final ParticipantInformation participantInformation : participantsInformation.values()) {
            final JsonObject value = participantInformationToJSON(participantInformation);
            if (value != null) {
                arrayBuilder.add(value);
            }
        }

        return arrayBuilder.build();
    }

    private JsonObject participantInformationToJSON(final ParticipantInformation participantInformation) {
        if (participantInformation == null) {
            return null;
        }

        return Json.createObjectBuilder()
            .add("id", participantInformation.getId())
            .add("applicationId", participantInformation.getApplicationId())
            .add("recoveryURL", participantInformation.getRecoveryURL())
            .add("status", participantInformation.getStatus())
            .build();
    }

}
