package br.com.datasinapse.ecommerce.dto;

public class UserDto {


    private String role;

    public UserDto(String role) {

        this.role = role;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}