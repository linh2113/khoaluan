package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Rating parent;
    
    @OneToMany(mappedBy = "parent")
    private List<Rating> replies;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
