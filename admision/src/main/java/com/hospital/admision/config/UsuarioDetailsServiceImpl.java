package com.hospital.admision.config;

import com.hospital.admision.model.Usuario;
import com.hospital.admision.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security usa este servicio para buscar al usuario en la base de datos
 * durante el inicio de sesión y obtener su rol.
 */
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // El usuario solo puede ingresar si él y su rol están activos
        boolean habilitado = usuario.getActivo() && usuario.getRol().getActivo();

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre()) // Se convierte en ROLE_ADMINISTRADOR, ROLE_MEDICO...
                .disabled(!habilitado)
                .build();
    }
}
