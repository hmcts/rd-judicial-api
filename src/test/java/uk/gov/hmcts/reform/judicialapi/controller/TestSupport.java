package uk.gov.hmcts.reform.judicialapi.controller;

import uk.gov.hmcts.reform.judicialapi.controller.response.UserSearchResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class TestSupport {

    private static final LocalDate date = LocalDate.now();
    private static final LocalDateTime dateTime = LocalDateTime.now();

    private TestSupport() {

    }

    public static Appointment createAppointment() {
        BaseLocationType baseLocationType = createBaseLocationType();
        RegionType regionType = createRegionType();

        Appointment appointment = new Appointment();
        appointment.setOfficeAppointmentId(1234567L);
        appointment.setBaseLocationType(baseLocationType);
        appointment.setRegionType(regionType);
        appointment.setIsPrincipleAppointment(Boolean.TRUE);
        appointment.setStartDate(date);
        appointment.setActiveFlag(Boolean.TRUE);
        appointment.setStartDate(date);
        appointment.setEndDate(date);
        appointment.setExtractedDate(dateTime);
        appointment.setCreatedDate(dateTime);
        appointment.setLastLoadedDate(dateTime);
        appointment.setUserProfile(new UserProfile());

        return appointment;
    }

    public static Authorisation createAuthorisation() {
        Authorisation authorisation = new Authorisation();
        authorisation.setOfficeAuthId(2L);
        authorisation.setJurisdiction("Languages");
        authorisation.setTicketId(233432L);
        authorisation.setStartDate(dateTime);
        authorisation.setEndDate(dateTime);
        authorisation.setCreatedDate(dateTime);
        authorisation.setLastUpdated(dateTime);
        authorisation.setLowerLevel("Welsh");
        authorisation.setUserProfile(new UserProfile());

        return authorisation;
    }

    public static UserSearchResponse createUserSearchResponse() {
        UserSearchResponse userSearchResponse = new UserSearchResponse();
        userSearchResponse.setPersonalCode("personalCode");
        userSearchResponse.setKnownAs("knownAs");
        userSearchResponse.setSurname("surname");
        userSearchResponse.setFullName("name");
        userSearchResponse.setTitle("postNominals");
        userSearchResponse.setEmailId("emailId");
        userSearchResponse.setIdamId("sidamId");

        return userSearchResponse;
    }

    public static UserProfile createUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setPerId("1");
        userProfile.setPersonalCode("personalCode");
        userProfile.setKnownAs("knownAs");
        userProfile.setSurname("surname");
        userProfile.setFullName("name");
        userProfile.setPostNominals("postNominals");
        userProfile.setWorkPattern("workPattern");
        userProfile.setEjudiciaryEmailId("emailId");
        userProfile.setActiveFlag(Boolean.TRUE);
        userProfile.setJoiningDate(date);
        userProfile.setLastWorkingDate(date);
        userProfile.setLastLoadedDate(dateTime);
        userProfile.setExtractedDate(dateTime);
        userProfile.setCreatedDate(dateTime);
        userProfile.setObjectId("objectId");
        userProfile.setSidamId("sidamId");
        userProfile.setAppointments(Collections.singletonList(createAppointment()));
        userProfile.setAuthorisations(Collections.singletonList(createAuthorisation()));

        return userProfile;
    }

    public static BaseLocationType createBaseLocationType() {
        BaseLocationType baseLocationType = new BaseLocationType();
        baseLocationType.setBaseLocationId("0");
        baseLocationType.setCourtName("Court Name");
        baseLocationType.setCourtType("Court Type");
        baseLocationType.setAppointments(Collections.singletonList(new Appointment()));
        baseLocationType.setCircuit("Circuit");
        baseLocationType.setAreaOfExpertise("Area of Expertise");
        return baseLocationType;
    }


    public static RegionType createRegionType() {
        RegionType regionType = new RegionType();
        regionType.setRegionId("0");
        regionType.setRegionDescEn("default");
        regionType.setRegionDescCy("default");
        regionType.setAppointments(Collections.singletonList(new Appointment()));

        return regionType;
    }

}
