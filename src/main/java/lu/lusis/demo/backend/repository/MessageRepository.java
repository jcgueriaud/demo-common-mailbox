package lu.lusis.demo.backend.repository;

import lu.lusis.demo.backend.data.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {

    /**
     *
     * @return
     */
    List<Message> findByDeletedFalse();

    /**
     *
     * @return
     */
    List<Message> findByDeletedTrue();
}
