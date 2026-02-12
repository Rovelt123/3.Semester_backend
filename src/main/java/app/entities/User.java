package app.entities;

import app.enums.Role;
import app.enums.ShiftStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_responsibility",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "responsibility_id")
    )
    private List<Responsibility> responsibilities = new ArrayList<>();

    protected User() {}

    public User(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public void addResponsibility(Responsibility r) {
        responsibilities.add(r);
    }

    public void removeResponsibility(Responsibility r) {
        responsibilities.remove(r);
    }
}

