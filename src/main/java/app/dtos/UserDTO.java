package app.dtos;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    private int id;
    private String name;
    private String username;
    private String lastName;

}

