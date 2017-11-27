package com.example.asus.pad3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ironman on 26.11.2017.
 */

public class BooksResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("error")
    @Expose
    private List<Object> error = null;
    @SerializedName("payload")
    @Expose
    private List<PayLoad> payload = null;
    @SerializedName("links")
    @Expose
    private List<Link> links = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

    public List<PayLoad> getPayload() {
        return payload;
    }

    public void setPayload(List<PayLoad> payload) {
        this.payload = payload;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
