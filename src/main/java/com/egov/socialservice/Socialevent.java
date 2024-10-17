package com.egov.socialservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "socialevents")
public class Socialevent {
    @Id
    @Column(name = "socialeventid", nullable = false)
    private UUID id;

    @Column(name = "citizenid")
    private UUID citizenid;

    @Size(max = 50)
    @Column(name = "socialeventtype", length = 50)
    private String socialeventtype;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCitizenid() {
        return citizenid;
    }

    public void setCitizenid(UUID citizenid) {
        this.citizenid = citizenid;
    }

    public String getSocialeventtype() {
        return socialeventtype;
    }

    public void setSocialeventtype(String socialeventtype) {
        this.socialeventtype = socialeventtype;
    }

    @Override
    public String toString() {
        return "Socialevent{" +
                "id=" + id +
                ", citizenid=" + citizenid +
                ", socialeventtype='" + socialeventtype + '\'' +
                '}';
    }
}