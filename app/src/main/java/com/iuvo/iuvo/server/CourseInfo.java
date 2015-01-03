package com.iuvo.iuvo.server;

/**
 * Packaging object for IuvoServer
 */
public class CourseInfo {
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String subject;
    private String code;
}
