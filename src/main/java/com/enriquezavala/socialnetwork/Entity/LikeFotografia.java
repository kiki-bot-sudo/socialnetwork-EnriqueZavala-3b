package com.enriquezavala.socialnetwork.Entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "like_fotografia",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"usuario_id", "fotografia_id"}
                )
        }
)
public class LikeFotografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fotografia_id", nullable = false)
    private Fotografia fotografia;

    public LikeFotografia() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Fotografia getFotografia() {
        return fotografia;
    }

    public void setFotografia(Fotografia fotografia) {
        this.fotografia = fotografia;
    }
}