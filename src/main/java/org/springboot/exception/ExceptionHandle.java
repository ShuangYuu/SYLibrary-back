package org.springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springboot.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = ServiceException.class)
    public Result serviceExceptionError(Exception e) {
        log.error("业务错误: {}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionError(Exception e){
        log.error("系统错误", e);
        return Result.error("系统错误");
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result unauthorizedException(Exception e){
        log.error(e.getMessage(), e);
        return Result.error(401, e.getMessage());
    }

    @ExceptionHandler(value = ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result forbiddenException(Exception e){
        log.error(e.getMessage(), e);
        return Result.error(403, e.getMessage());
    }

    @ExceptionHandler(value = InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result invalidRequestException(Exception e){
        log.error(e.getMessage(), e);
        return Result.error(400, e.getMessage());
    }
}
