package com.example.demo.mapper;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDto(Usuario u) {
        if (u == null) return null;
        UsuarioDTO d = new UsuarioDTO();
        d.setId(u.getId());
        d.setCedula(u.getCedula());
        d.setNombreCompleto(u.getNombreCompleto());
        d.setCorreo(u.getCorreo());
        d.setProceso(u.getProceso());
        d.setCargo(u.getCargo());
        d.setTelefono(u.getTelefono());
        d.setRol(u.getRol() != null ? u.getRol().getNombre() : null);
        d.setActivo(u.isActivo());
        return d;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        // normalmente conviertes DTO->entity cuando recibes datos, pero aquí dejamos simple
        Usuario u = new Usuario();
        u.setId(dto.getId());
        u.setCedula(dto.getCedula());
        u.setNombreCompleto(dto.getNombreCompleto());
        u.setCorreo(dto.getCorreo());
        u.setProceso(dto.getProceso());
        u.setCargo(dto.getCargo());
        u.setTelefono(dto.getTelefono());
        u.setActivo(dto.isActivo());
        // rol debe buscarse por nombre y setearse vía servicio en el controller
        return u;
    }
}
