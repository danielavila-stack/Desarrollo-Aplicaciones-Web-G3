package com.hospital.admision.repository;

import com.hospital.admision.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Usado por Spring Security para el inicio de sesión
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    // Cantidad de usuarios que tienen asignado un rol
    long countByRolId(Long rolId);
}
