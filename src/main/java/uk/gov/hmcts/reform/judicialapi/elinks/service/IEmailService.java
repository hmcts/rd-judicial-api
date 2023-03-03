package uk.gov.hmcts.reform.judicialapi.elinks.service;

import uk.gov.hmcts.reform.judicialapi.elinks.service.dto.Email;

public interface IEmailService {

    /**
     * Triggers failure mails with reason of failure.
     *
     * @param emailDto The dto object that holds all the details required to send an email.
     */
    int sendEmail(Email emailDto);
}
