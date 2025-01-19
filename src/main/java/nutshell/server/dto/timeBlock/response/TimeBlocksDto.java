package nutshell.server.dto.timeBlock.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TimeBlocksDto(
        Long id,
        String name,
        String status,
        List<TimeBlockDto> timeBlocks
) {
}
