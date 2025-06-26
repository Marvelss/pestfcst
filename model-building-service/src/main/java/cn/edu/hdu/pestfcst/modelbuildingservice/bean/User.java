package cn.edu.hdu.pestfcst.modelbuildingservice.bean;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
} 