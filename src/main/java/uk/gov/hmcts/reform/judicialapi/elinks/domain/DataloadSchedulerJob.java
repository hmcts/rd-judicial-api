package uk.gov.hmcts.reform.judicialapi.elinks.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "DataloadSchedularJob")
@Table(name = "dataload_schedular_job", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "elink_dataload_schedular_job_id_sequence",
        sequenceName = "elink_dataload_schedular_job_id_sequence",  schema = "dbjudicialdata", allocationSize = 1)
public class DataloadSchedulerJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elink_dataload_schedular_job_id_sequence")
    @Column(name = "id")
    private int id;

    @Column(name = "job_start_time")
    private LocalDateTime jobStartTime;

    @Column(name = "job_end_time")
    private LocalDateTime jobEndTime;

    @Column(name = "publishing_status")
    private String publishingStatus;


}
