package cn.edu.hdu.pestfcst.modelbuildingservice.dao;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
} 