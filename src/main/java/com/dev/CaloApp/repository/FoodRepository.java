package com.dev.CaloApp.repository;

import com.dev.CaloApp.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByName(String name);
    // ✅ Dùng LIKE CONCAT('%', :name, '%') khi bạn muốn query linh hoạt, tránh phải xử lý chuỗi trong code Java.
    //✅ Dùng LIKE :name khi bạn muốn toàn quyền kiểm soát tham số truyền vào (ví dụ: muốn tìm theo prefix apple% hoặc suffix %apple).
    @Query(value = "SELECT * FROM food WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))", nativeQuery = true)
    List<Food> searchFoodName(@Param("name") String name);

    @Query(value = "SELECT * FROM food ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Food> findRandomFoods();
    List<Food> findFoodByCaloriesLessThanEqual(float calories);
    Optional<Food> findByName(String name);
}
