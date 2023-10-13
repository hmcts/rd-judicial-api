package uk.gov.hmcts.reform.judicialapi.elinks.domain;


import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name = "elinks_responses")
@Table(name = "elinks_responses", schema = "dbjudicialdata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "elinks_responses_sequence",
        sequenceName = "elinks_responses_sequence",  schema = "dbjudicialdata", allocationSize = 1)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
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

    @Type(type = "jsonb")
    @Column(name = "elinks_data", columnDefinition = "jsonb")
    @NotNull
    private JsonNode elinksData;

    @Column(name = "created_date")
    @NotNull
    private LocalDateTime createdDate;

}
