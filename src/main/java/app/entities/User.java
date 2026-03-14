package app.entities;

import app.enums.Role;
import app.enums.ShiftStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_responsibility",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "responsibility_id")
    )
    private List<Responsibility> responsibilities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Holiday> holidays = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shift> shifts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Announcement> announcements = new ArrayList<>();


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public User() {}

    // ________________________________________________________

    public User(String name, Set<Role> role, String username, String password) {
        this.name = name;
        this.roles = role;
        this.username = username;
        this.password = password;
    }

    // ________________________________________________________

    public void addRole(Role role) {
        roles.add(role);
    }

    // ________________________________________________________

    public void removeRole(Role role) {
        roles.remove(role);
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

