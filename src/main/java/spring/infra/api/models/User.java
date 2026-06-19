package spring.infra.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String passwd;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;



    public UUID getId() {return id;}
    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {return email;}
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {return passwd;}
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getName() {return name;}
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {return phone;}
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Role> getRoles() {return roles;}
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}