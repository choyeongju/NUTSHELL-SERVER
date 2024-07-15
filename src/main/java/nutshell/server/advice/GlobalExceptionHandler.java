package nutshell.server.advice;


import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import nutshell.server.exception.BusinessException;
import nutshell.server.exception.NotFoundException;
import nutshell.server.exception.UnAuthorizedException;
import nutshell.server.exception.code.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 요청은 정상이나 비즈니스 중 실패가 있는 경우
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessErrorCode> handleBusinessException(BusinessException e) {
        log.error("GlobalExceptionHandler catch BusinessException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode());
    }

    // DB에서 데이터를 찾지 못한 경우
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<NotFoundErrorCode> handleNotFoundException(NotFoundException e){
        log.error("GlobalExceptionHandler catch NotFoundException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode());
    }

    // 존재하지 않는 요청에 대한 예외
    @ExceptionHandler(value={NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<BusinessErrorCode> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        return ResponseEntity
                .status(BusinessErrorCode.WRONG_ENTRY_POINT.getHttpStatus())
                .body(BusinessErrorCode.WRONG_ENTRY_POINT);
    }

    // 기본 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerErrorCode> handleException(Exception e) {
        log.error("handleException() in GlobalExceptionHandler throw Exception : {}", e.getMessage());
        return ResponseEntity
                .status(InternalServerErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(InternalServerErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 유효하지 않은 인자가 들어온 경우 (@Valid 사용 시 수행)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IllegalArgumentErrorCode> handleException(MethodArgumentNotValidException e) {
        log.error("handleException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());
        return ResponseEntity
                .status(IllegalArgumentErrorCode.INVALID_ARGUMENTS.getHttpStatus())
                .body(IllegalArgumentErrorCode.INVALID_ARGUMENTS);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<IllegalArgumentErrorCode> handleException(MissingServletRequestParameterException e) {
        log.error("handleException() in GlobalExceptionHandler throw MissingServletRequestParameterException : {}", e.getMessage());
        return ResponseEntity
                .status(IllegalArgumentErrorCode.INVALID_ARGUMENTS.getHttpStatus())
                .body(IllegalArgumentErrorCode.INVALID_ARGUMENTS);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<UnAuthorizedErrorCode> handleException(UnAuthorizedException e) {
        log.error("handleException() in GlobalExceptionHandler throw UnAuthorizedException : {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode());
    }

    //@JsonFormat 오류
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<IllegalArgumentErrorCode> handleException(MethodArgumentTypeMismatchException e) {
        log.error("handleException() in GlobalExceptionHandler throw MethodArgumentTypeMismatchException : {}", e.getMessage());
        return ResponseEntity
                .status(IllegalArgumentErrorCode.INVALID_DATE_FORMAT.getHttpStatus())
                .body(IllegalArgumentErrorCode.INVALID_DATE_FORMAT);
    }

    //FeignClient 오류
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BusinessErrorCode> handleException(FeignException e) {
        log.error("handleException() in GlobalExceptionHandler throw FeignException : {}", e.getMessage());
        return ResponseEntity
                .status(BusinessErrorCode.GOOGLE_SERVER_ERROR.getHttpStatus())
                .body(BusinessErrorCode.GOOGLE_SERVER_ERROR);
    }
}