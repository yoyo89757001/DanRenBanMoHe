package com.xiaojun.danrenbanmohe.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
@Entity
public class MingXinBean {
    @Id
    private long id;
    private byte[] facetoken;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getFacetoken() {
        return facetoken;
    }

    public void setFacetoken(byte[] facetoken) {
        this.facetoken = facetoken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
