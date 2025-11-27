package com.example.demo.security;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String roleName = u.getRol() != null ? u.getRol().getNombre() : "USER";
        // ensure Spring Security role prefix
        String role = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return new User(u.getCorreo(), u.getContrasena(), Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
