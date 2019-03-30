package com.nesstech.metube.model;

import java.io.Serializable;

public class SectionDataModel implements Serializable {

    private String headerTitle;
    private String headerId;

    public SectionDataModel() {
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
}

