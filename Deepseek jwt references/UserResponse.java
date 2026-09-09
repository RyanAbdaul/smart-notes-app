package com.luv2code.io.todos.response;


import com.luv2code.io.todos.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserResponse {
    private long id;

    private String fullName;

    private String email;

    private List<Authority> authorities;
}
