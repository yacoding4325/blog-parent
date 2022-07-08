package com.mszlu.blog.handler;

import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//对加了@ControllerAdvice注解的方法进行拦截处理  AOP的实现
@ControllerAdvice
public class AllExceptionHandler {

    //进行异常处理，处理Exception.class 的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody//返回json数据 不然会在前端返回页面
    public Result doException(Exception e) {
        e.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}