package com.hgicreate.rno.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "RNO_SYS_MENU")
public class Menu implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;
    @JsonProperty("pid")
    private Long parentId;
    private String name;
    private String url;
    @Column(name = "INDEX_OF_BROTHER")
    @JsonProperty("index_of_brother")
    private Integer indexOfBrother;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    @OrderBy("indexOfBrother")
    private List<Menu> children;
}
