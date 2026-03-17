package app.entities;

import app.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstname;
    private String lastname;

    @Column(unique = true)
    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_responsibility",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "responsibility_id")
    )
    private Set<Responsibility> responsibilities = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Holiday> holidays = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Shift> shifts =  new HashSet<>();;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Announcement> announcements = new HashSet<>();


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public User() {}

    // ________________________________________________________

    public User(String name, String lastname, Set<Role> role, String username, String password) {
        this.firstname = name;
        this.lastname = lastname;
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

