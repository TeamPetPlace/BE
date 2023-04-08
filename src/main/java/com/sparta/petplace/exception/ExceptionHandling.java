package com.sparta.petplace.exception;

import com.sparta.petplace.common.ApiResponseDto;
import com.sparta.petplace.common.ErrorResponse;
import com.sparta.petplace.common.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<ExceptionDto> handleApiRequestException(CustomException ex) {

        log.error("Exception : "+ex.getError().getMessage());
        return ResponseEntity.badRequest().body(new ExceptionDto(false, null, ex.getError()));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponseDto<ErrorResponse> methodValidException(MethodArgumentNotValidException e) {
        ErrorResponse error = errorResponse(e.getMessage());
        log.error("MethodArgumentNotValidException : "+e.getMessage());
        return ResponseUtils.error(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponseDto<ErrorResponse> commonException(RuntimeException e) {
        ErrorResponse error = errorResponse(e.getMessage());
        log.error("RuntimeException : "+e.getMessage());
        return ResponseUtils.error(error);
    }

    private ErrorResponse errorResponse(BindingResult bindingResult) {
        String message = "";

        if (bindingResult.hasErrors()) {
            message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        }

        return ErrorResponse.of(Error.BAD_REQUEST.getStatus(), message);
    }

    private ErrorResponse errorResponse(String message) {
        return ErrorResponse.of(Error.BAD_REQUEST.getStatus(), message);
    }
}