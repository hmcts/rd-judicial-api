package uk.gov.hmcts.reform.judicialapi.controller;

import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.domain.Authorisation;
import uk.gov.hmcts.reform.judicialapi.domain.BaseLocationType;
import uk.gov.hmcts.reform.judicialapi.domain.ContractType;
import uk.gov.hmcts.reform.judicialapi.domain.RegionType;
import uk.gov.hmcts.reform.judicialapi.domain.RoleType;
import uk.gov.hmcts.reform.judicialapi.domain.UserProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class TestSupport {

    private static LocalDate date = LocalDate.now();
    private static LocalDateTime dateTime = LocalDateTime.now();

    private TestSupport() {

    }

    public static Appointment createAppointment() {
        RoleType roleType = createRoleType();
        ContractType contractType = createContractType();
        BaseLocationType baseLocationType = createBaseLocationType();
        RegionType regionType = createRegionType();

        Appointment appointment = new Appointment();
        appointment.setOfficeAppointmentId(1234567L);
        appointment.setRoleType(roleType);
        appointment.setContractType(contractType);
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

    public static UserProfile createUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setElinksId("1");
        userProfile.setPersonalCode("personalCode");
        userProfile.setTitle("title");
        userProfile.setKnownAs("knownAs");
        userProfile.setSurname("surname");
        userProfile.setFullName("name");
        userProfile.setPostNominals("postNominals");
        userProfile.setWorkPattern("workPattern");
        userProfile.setEmailId("emailId");
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
        baseLocationType.setBench("Bench");
        baseLocationType.setCircuit("Circuit");
        baseLocationType.setAreaOfExpertise("Area of Expertise");
        baseLocationType.setNationalCourtCode("National Court");

        return baseLocationType;
    }

    public static RoleType createRoleType() {
        RoleType roleType = new RoleType();
        roleType.setRoleId("0");
        roleType.setRoleDescEn("default");
        roleType.setRoleDescCy("default");
        roleType.setAppointments(Collections.singletonList(new Appointment()));

        return roleType;
    }

    public static ContractType createContractType() {
        ContractType contractType = new ContractType();
        contractType.setContractTypeId("0");
        contractType.setContractTypeDescEn("default");
        contractType.setContractTypeDescCy("default");
        contractType.setAppointments(Collections.singletonList(new Appointment()));

        return contractType;
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
