package com.voda.blog.oauth2;

import com.voda.blog.user.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(SiteUser user) {
        this.name = user.getNickname();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }

    public SessionUser() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}