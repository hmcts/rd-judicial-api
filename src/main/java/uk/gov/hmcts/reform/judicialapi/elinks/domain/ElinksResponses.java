package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity(name = "elinks_responses")
@Table(name = "elinks_responses", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "elinks_responses_sequence",
        sequenceName = "elinks_responses_sequence",  schema = "dbjudicialdata", allocationSize = 1)
public class ElinksResponses {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elinks_responses_sequence")
    private long id;

    @Column(name = "api_name")
    @NotNull
    @Size(max = 64)
    private String apiName;

    @Type(JsonBinaryType.class)
    @Column(name = "elinks_data", columnDefinition = "jsonb")
    @NotNull
    private JsonNode elinksData;

    @Column(name = "created_date")
    @NotNull
    private LocalDateTime createdDate;

}
