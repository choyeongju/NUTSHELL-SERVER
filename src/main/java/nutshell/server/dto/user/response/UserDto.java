package nutshell.server.dto.user.response;

import lombok.Builder;
import nutshell.server.dto.googleCalender.response.GoogleEmailDto;

import java.util.List;

@Builder
public record UserDto(
    String givenName,
    String familyName,
    String image,
    String email,
    List<GoogleEmailDto> googleCalenders
) {
}
