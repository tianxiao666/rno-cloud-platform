package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tao.xj
 */
@Data
@Entity
@Table(name = "RNO_GSM_NETWORK_COVERAGE_JOB")
public class GsmNetworkCoverageJob {
    @Id
    @GeneratedValue(generator = "NetworkCoverageJobSeq")
    @SequenceGenerator(name = "NetworkCoverageJobSeq", sequenceName = "SEQ_GSM_NETWORK_COVERAGE_JOB", allocationSize = 1)
    private Long id;

    private String name;
    private Integer priority;
    private Date begMeaTime;
    private Date endMeaTime;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String createdUser;
    private Date createdDate;
    private Integer fileNumber;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;
}
