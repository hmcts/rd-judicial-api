package uk.gov.hmcts.reform.judicialapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.controller.response.OrmResponse;
import uk.gov.hmcts.reform.judicialapi.domain.Appointment;
import uk.gov.hmcts.reform.judicialapi.repository.AppointmentRepository;
import uk.gov.hmcts.reform.judicialapi.repository.AuthorisationRepository;
import uk.gov.hmcts.reform.judicialapi.service.JudicialUserService;

import java.util.List;

public class JudicialUserServiceImpl implements JudicialUserService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AuthorisationRepository authorisationRepository;


    @Override
    public OrmResponse fetchJudicialUsers(Integer size, Integer page) {

        OrmResponse ormResponse = new OrmResponse();
        List<Appointment> appointments = appointmentRepository.findAll();

        return ormResponse;
    }
}
