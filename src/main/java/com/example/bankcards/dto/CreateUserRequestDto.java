package com.example.bankcards.dto;

import com.example.bankcards.entity.RoleEnum;

import java.util.Date;

public class CreateUserRequestDto {

    private String name;
    private String lastname;
    private Date birthDate;
    private RoleEnum role;
    private String email;
    private String password;

    public CreateUserRequestDto(String name, String lastname, Date birthDate, RoleEnum role, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public CreateUserRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
