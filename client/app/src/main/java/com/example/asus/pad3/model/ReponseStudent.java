package com.example.asus.pad3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ironman on 26.11.2017.
 */

public class ReponseStudent {

        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("error")
        @Expose
        private List<String> error = null;
        @SerializedName("payload")
        @Expose
        private List<Student> response;
        @SerializedName("links")
        @Expose
        private List<Link> links = null;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public List<String> getError() {
            return error;
        }

        public void setError(List<String> error) {
            this.error = error;
        }

        public List<Student> getResponse() {
            return response;
        }

        public void setResponse(List<Student> response) {
            this.response = response;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }
}
