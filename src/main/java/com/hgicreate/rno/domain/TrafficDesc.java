package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "RNO_LTE_TRAFFIC_DESC")
public class TrafficDesc {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade=CascadeType.ALL)//级联保存、更新、删除、刷新;
    @JoinColumn(name="desc_id")//在表增加一个外键列来实现一对多的单向关联
    private Set<TrafficData> trafficData = new HashSet<TrafficData>();

    private String filename;
    private String dataType;
    private Integer recordCount;
    private String createdUser;
    private Date createdDate;
    private Date beginTime;
    private Date endTime;
    private String vendor;
    private Long originFileId;
    private Long jobId;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

}
