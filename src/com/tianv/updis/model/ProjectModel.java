package com.tianv.updis.model;

import java.io.Serializable;

/**
 * Created by lm3515 on 14-1-22.
 */
public class ProjectModel implements Serializable{
    //{"projectId":3489,"projectNumber":"2345","projectName":"其实地方规划局考虑",
    // "partyAName":"shen深圳市龙岗政府采购中心","designDepartment":"院部",
    // "projectLeaders":["贝思琪","丁年","丁淑芳"],"projectScale":"sss"}
    private String projectId;
    private String projectNumber;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPartyAName() {
        return partyAName;
    }

    public void setPartyAName(String partyAName) {
        this.partyAName = partyAName;
    }

    public String getDesignDepartment() {
        return designDepartment;
    }

    public void setDesignDepartment(String designDepartment) {
        this.designDepartment = designDepartment;
    }

    public String getProjectLeaders() {
        return projectLeaders;
    }

    public void setProjectLeaders(String projectLeaders) {
        this.projectLeaders = projectLeaders;
    }

    public String getProjectScale() {
        return projectScale;
    }

    public void setProjectScale(String projectScale) {
        this.projectScale = projectScale;
    }

    private String projectName;
    private String partyAName;
    private String designDepartment;
    private String projectLeaders;
    private String projectScale;
}
