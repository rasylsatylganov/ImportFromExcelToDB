package kg.home.demo.repository;

import kg.home.demo.entity.MultipleThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleThreadRepository extends JpaRepository<MultipleThread, Long> {

    @Query(value = """
        SELECT *
        FROM multiple_thread
        WHERE status = 'ACTIVE'
        AND ROWNUM <= :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<MultipleThread> findActiveForProcessing(@Param("limit") int limit);
}