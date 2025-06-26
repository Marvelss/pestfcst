package cn.edu.hdu.pestfcst.modelbuildingservice.exception;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("文件大小超过限制"));
    }

    /**
     * 处理文件访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("访问被拒绝"));
    }

    /**
     * 处理文件IO异常
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<String>> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("文件操作失败: " + e.getMessage()));
    }

    /**
     * 处理数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<String>> handleNumberFormatException(NumberFormatException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数格式错误"));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("参数错误: " + e.getMessage()));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
    }
} 