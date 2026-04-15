package com.jr.todo.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.jr.todo.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usr_id", unique = true)
  private Long id;

  @Column(name = "usr_name", nullable = false)
  private String name;

  @Column(name = "usr_apellido", nullable = false)
  private String lastName;

  @Column(name = "usr_email", nullable = false, unique = true)
  private String email;

  @Column(name = "usr_username", nullable = false, unique = true)
  private String username;

  @Column(name = "usr_password", nullable = false)
  private String password;

  @Column(name = "usr_activo")
  private boolean enable;

  @Column(name = "usr_role")
  private Role role;

  @Column(name = "usr_fecha_registro")
  private LocalDate registrationDte;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority((role.name())));
  }
}
