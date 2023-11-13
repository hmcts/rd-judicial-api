package uk.gov.hmcts.reform.judicialapi.elinks.service.impl;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.ResultsRequest;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.service.ELinksPeopleDeleteService;

@Slf4j
@Service
@Setter
public class ElinksPeopleDeleteServiceimpl implements ELinksPeopleDeleteService {

    @Autowired
    private JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    ProfileRepository profileRepository;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAuth(ResultsRequest resultsRequest) {
        authorisationsRepository.deleteByPersonalCode(resultsRequest.getPersonalCode());
        appointmentsRepository.deleteByPersonalCode(resultsRequest.getPersonalCode());
        judicialRoleTypeRepository.deleteByPersonalCode(resultsRequest.getPersonalCode());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePeople(String personalCode) {
        log.info("entering into deleteAuth : ");
        authorisationsRepository.deleteByPersonalCode(personalCode);
        appointmentsRepository.deleteByPersonalCode(personalCode);
        judicialRoleTypeRepository.deleteByPersonalCode(personalCode);
        profileRepository.deleteById(personalCode);

    }
}
