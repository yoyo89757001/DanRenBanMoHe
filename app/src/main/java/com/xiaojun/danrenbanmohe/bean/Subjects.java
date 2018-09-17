package com.xiaojun.danrenbanmohe.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Subjects {

    @Id
    private long id;
    private String sex;
    private int age;
    private float  faceScore;
    private byte[] faceToken;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Subjects(byte[] faceToken) {
        this.faceToken = faceToken;
    }

    public Subjects() {
    }

    public Subjects(String sex, int age, float faceScore, byte[] faceToken) {
        this.sex = sex;
        this.age = age;
        this.faceScore = faceScore;
        this.faceToken = faceToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getFaceScore() {
        return faceScore;
    }

    public void setFaceScore(float faceScore) {
        this.faceScore = faceScore;
    }

    public byte[] getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(byte[] faceToken) {
        this.faceToken = faceToken;
    }
}
