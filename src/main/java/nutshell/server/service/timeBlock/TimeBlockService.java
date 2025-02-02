package nutshell.server.service.timeBlock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nutshell.server.domain.Task;
import nutshell.server.domain.TimeBlock;
import nutshell.server.domain.User;
import nutshell.server.dto.timeBlock.request.TimeBlockRequestDto;
import nutshell.server.dto.timeBlock.response.TimeBlockTasksDto;
import nutshell.server.dto.timeBlock.response.TimeBlocksDto;
import nutshell.server.exception.BusinessException;
import nutshell.server.exception.code.BusinessErrorCode;
import nutshell.server.service.task.TaskRetriever;
import nutshell.server.service.user.UserRetriever;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeBlockService {
    private final TimeBlockUpdater timeBlockEditor;
    private final TimeBlockRemover timeBlockRemover;
    private final TimeBlockRetriever timeBlockRetriever;
    private final TimeBlockSaver timeBlockSaver;
    private final TaskRetriever taskRetriever;
    private final UserRetriever userRetriever;

    @Transactional
    public TimeBlock create(
            final Long userId,
            final Long taskId,
            final TimeBlockRequestDto timeBlockRequestDto
    ) {
        checkValid(timeBlockRequestDto.startTime(), timeBlockRequestDto.endTime());
        User user = userRetriever.findByUserId(userId);
        Task task = taskRetriever.findByUserAndId(user, taskId);
        if (timeBlockRetriever.existsByTaskUserAndStartTimeBetweenAndEndTimeBetween(user, timeBlockRequestDto.startTime(), timeBlockRequestDto.endTime())) {
            throw new BusinessException(BusinessErrorCode.TIME_CONFLICT);
        }
        return timeBlockSaver.save(TimeBlock.builder()
                .task(task)
                .startTime(timeBlockRequestDto.startTime())
                .endTime(timeBlockRequestDto.endTime())
                .build()
        );
    }

    @Transactional
    public void update(
            final Long userId,
            final Long taskId,
            final Long timeBlockId,
            final TimeBlockRequestDto timeBlockRequestDto
    ){
        checkValid(timeBlockRequestDto.startTime(), timeBlockRequestDto.endTime());
        User user = userRetriever.findByUserId(userId);
        Task task = taskRetriever.findByUserAndId(user, taskId);
        if (timeBlockRetriever.existsByTaskAndStartTimeBetweenAndEndTimeBetweenAndIdNot(user, timeBlockId, timeBlockRequestDto.startTime(), timeBlockRequestDto.endTime())) {
            throw new BusinessException(BusinessErrorCode.TIME_CONFLICT);
        }
        TimeBlock timeBlock = timeBlockRetriever.findByTaskAndId(task, timeBlockId);
        timeBlockEditor.updateTime(timeBlock,timeBlockRequestDto.startTime(), timeBlockRequestDto.endTime());
    }

    @Transactional
    public void delete(
            final Long userId,
            final Long taskId,
            final Long timeBlockId
    ){
        User user = userRetriever.findByUserId(userId);
        Task task = taskRetriever.findByUserAndId(user, taskId);
        TimeBlock timeBlock = timeBlockRetriever.findByTaskAndId(task, timeBlockId);
        timeBlockRemover.remove(timeBlock);
    }

    public TimeBlockTasksDto getTimeBlocks(
            final Long userId,
            final LocalDate startDate,
            final Integer range
    ){
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = startDate.plusDays(range-1).atTime(23,59,59);
        User user = userRetriever.findByUserId(userId);
        List<TimeBlocksDto> tasks = taskRetriever.findAllByUserAndTimeBlocks(user, startTime, endTime)
                .stream().map(
                        task -> TimeBlocksDto.builder()
                                .id(task.getId())
                                .name(task.getName())
                                .status(task.getStatus().getContent())
                                .timeBlocks(timeBlockRetriever.findAllByTaskIdAndTimeRange(task, startTime, endTime))
                                .build()
                ).toList();
        return TimeBlockTasksDto.builder()
                .tasks(tasks)
                .build();
    }
  
    private void checkValid(
            final LocalDateTime startTime,
            final LocalDateTime endTime
    ) {
        if (startTime.isAfter(endTime)) {
            throw new BusinessException(BusinessErrorCode.TIME_CONFLICT);
        }
        if (!startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            throw new BusinessException(BusinessErrorCode.NOT_SAME_DATE_CONFLICT);
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new BusinessException(BusinessErrorCode.TIME_INVALID);
        }
    }
}
