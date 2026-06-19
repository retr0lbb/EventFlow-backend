package spring.infra.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    private String name;

    public Long getRoleId() {return roleId;}
    public void setRoleId(Long roleId) {this.roleId = roleId;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}


}
