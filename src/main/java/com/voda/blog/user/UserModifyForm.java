package com.voda.blog.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModifyForm {
    private String rName;
    private Integer age;
    private String pNum;
    private String intro;
    private String address;
    private String job;
}
