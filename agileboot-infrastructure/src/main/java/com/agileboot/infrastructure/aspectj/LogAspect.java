package com.agileboot.infrastructure.aspectj;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.enums.BusinessStatus;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.common.utils.ServletHolderUtil;
import com.agileboot.infrastructure.manager.AsyncManager;
import com.agileboot.infrastructure.manager.factory.AsyncFactory;
import com.agileboot.orm.po.SysOperationLogXEntity;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Aspect
@Component
@Slf4j
public class LogAspect {


    /**
     * TODO 优化这个类 乱七八糟的
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            // 获取当前的用户
            LoginUser loginUser = AuthenticationUtils.getLoginUser();

            // *========数据库日志=========*//
            SysOperationLogXEntity operLog = new SysOperationLogXEntity();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址

            String ip = ServletUtil.getClientIP(ServletHolderUtil.getRequest());
            operLog.setOperatorIp(ip);
            operLog.setRequestUrl(ServletHolderUtil.getRequest().getRequestURI());
            if (loginUser != null) {
                operLog.setUserName(loginUser.getUsername());
            }

            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());

                operLog.setErrorStack(StrUtil.sub(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setCalledMethod(className + "." + methodName + "()");
            // 设置请求方式
            //
            operLog.setRequestMethod(getHttpMethodIntValue(ServletHolderUtil.getRequest().getMethod()));
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            operLog.setOperationTime(DateUtil.date());
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordOperationLog(operLog));
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param operLog 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperationLogXEntity operLog, Object jsonResult)
        throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setRequestModule(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && jsonResult != null) {
            operLog.setOperationResult(StrUtil.sub(JSONUtil.toJsonStr(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperationLogXEntity operLog) throws Exception {

        int requestMethod = operLog.getRequestMethod();
        if (1 == requestMethod || 2 == requestMethod) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperationParam(StrUtil.sub(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletHolderUtil.getRequest()
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperationParam(StrUtil.sub(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (o != null && !isFilterObject(o)) {
                    try {
                        Object jsonObj = JSONUtil.parseObj(o);
                        params.append(jsonObj).append(" ");
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
            || o instanceof BindingResult;
    }


    private int getHttpMethodIntValue(String httpMethod) {

        int intValue = 0;

        switch (httpMethod) {

            case "GET" : intValue = 1; break;
            case "POST": intValue = 2; break;
            case "PUT":  intValue = 3; break;
            default: intValue = -1;
            break;

        }
        return intValue;
    }

}
