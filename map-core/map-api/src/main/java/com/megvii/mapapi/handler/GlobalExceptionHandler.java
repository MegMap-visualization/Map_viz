package com.megvii.mapapi.handler;


import com.megvii.exception.CommonException;
import com.megvii.exception.InterfaceIdempotentException;
import com.megvii.exception.NotAuthException;
import com.megvii.exception.StopException;
import com.megvii.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;


@ControllerAdvice
@ResponseBody
class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    R RuntimeException(Exception e) {
        return R.error(e.getMessage());
    }

//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    R MethodArgumentTypeMismatchException(Exception e) {
//        return R.error(e.getMessage());
//    }

//    @ExceptionHandler(SQLException.class)
//    R SQLException(Exception e) {
//        return R.error("参数缺失异常").put("data", e.getMessage());
//    }

    @ExceptionHandler(NotAuthException.class)
    R NotAuthException(Exception e) {
        return R.error(401, "无权限");
    }

    @ExceptionHandler(InterfaceIdempotentException.class)
    ResponseEntity<Object> InterfaceIdempotentException(Exception e) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CommonException.class)
    R CommonException(Exception e) {
        return R.error(500, e.getMessage());
    }

    @ExceptionHandler(StopException.class)
    ResponseEntity<Object> StopException(Exception e) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

}


