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

    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_responsibility",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "responsibility_id")
    )
    private List<Responsibility> responsibilities = new ArrayList<>();

    @OneToMany(mappedBy = "userID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Holiday> holidays = new ArrayList<>();

    @OneToMany(mappedBy = "ownerID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shift> shifts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements = new ArrayList<>();



    public User() {}

    // ________________________________________________________

    public User(String name, Role role, String username, String password) {
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    // ________________________________________________________

    public void addResponsibility(Responsibility r) {
        responsibilities.add(r);
    }

    // ________________________________________________________

    public void removeResponsibility(Responsibility r) {
        responsibilities.remove(r);
    }
}

