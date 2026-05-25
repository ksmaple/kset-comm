package com.kset.boot.web.handler;

import com.kset.core.exception.BusinessException;
import com.kset.boot.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        int code = ex.getErrorCode() != null ? parseErrorCode(ex.getErrorCode()) : -1;
        return ApiResponse.fail(code, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(Exception ex) {
        String message = "参数校验失败";
        if (ex instanceof MethodArgumentNotValidException manv) {
            if (manv.getBindingResult().getFieldError() != null) {
                message = manv.getBindingResult().getFieldError().getDefaultMessage();
            }
        } else if (ex instanceof BindException be) {
            if (be.getBindingResult().getFieldError() != null) {
                message = be.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.fail("系统异常，请稍后重试");
    }

    private int parseErrorCode(String errorCode) {
        try {
            return Integer.parseInt(errorCode);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
