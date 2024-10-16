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

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity(name = "dataloadExceptionRecords")
@Table(name = "dataload_exception_records", schema = "dbjudicialdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "elink_exception_records_id_sequence",
        sequenceName = "elink_exception_records_id_sequence",  schema = "dbjudicialdata", allocationSize = 1)
public class ElinkDataExceptionRecords implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elink_exception_records_id_sequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "scheduler_name")
    private String schedulerName;

    @Column(name = "scheduler_start_time")
    private LocalDateTime schedulerStartTime;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "key")
    private String key;

    @Column(name = "field_in_error")
    private String fieldInError;

    @Column(name = "error_description")
    private String errorDescription;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimeStamp;

    @Column(name = "row_id")
    private String rowId;

    @Column(name = "page_id")
    private Integer pageId;

    @Column(name = "error_message")
    private String errorMessage;

}
