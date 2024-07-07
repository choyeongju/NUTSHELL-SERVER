package nutshell.server.repository;

import nutshell.server.domain.Task;
import nutshell.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByUserAndId(final User user, final Long aLong);

    @Query(value="select t from Task t where t.user = :user " +
            "and exists (select tb from TimeBlock tb " +
            "where tb.task = t and tb.startTime between :startTime and :endTime " +
            "and tb.endTime between :startTime and :endTime)")
    List<Task> findAllByUserAndTimeBlocks(final User user, LocalDateTime startTime, LocalDateTime endTime);

}
