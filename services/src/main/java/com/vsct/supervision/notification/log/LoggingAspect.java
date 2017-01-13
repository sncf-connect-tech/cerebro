/*
 * This file is part of the Cerebro distribution.
 * (https://github.com/voyages-sncf-technologies/cerebro)
 * Copyright (C) 2017 VSCT.
 *
 * Cerebro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * Cerebro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.vsct.supervision.notification.log;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    @Around(value = "@within(com.vsct.supervision.notification.log.Loggable) || @annotation(com.vsct.supervision.notification.log.Loggable)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {


        final MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Method method = signature.getMethod();
        final Class clazz = signature.getClass();
        final Loggable loggableMethod = method.getAnnotation(Loggable.class);

        final Loggable loggableClass = proceedingJoinPoint.getTarget().getClass().getAnnotation(Loggable.class);

        //get current log level
        final LogLevel logLevel = loggableMethod != null ? loggableMethod.value() : loggableClass.value();

        final String service = StringUtils.isNotBlank(loggableClass.service()) ? loggableClass.service() :  clazz.getName();
        final String methodName = StringUtils.isNotBlank(loggableClass.method()) ? loggableClass.method() :  method.getName();

        final String star = "**********";
        //before
        LogWriter.write(proceedingJoinPoint.getTarget().getClass(), logLevel, star + service + "." + methodName + "() start execution" + star);


        //show traceParams
        final  boolean showParams = loggableMethod != null ? loggableMethod.traceParams() : loggableClass.traceParams();
        if (showParams) {

            if (proceedingJoinPoint.getArgs() != null && proceedingJoinPoint.getArgs().length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < proceedingJoinPoint.getArgs().length; i++) {
                    sb.append(method.getParameterTypes()[i].getName() + ":" + proceedingJoinPoint.getArgs()[i]);
                    if (i < proceedingJoinPoint.getArgs().length - 1)
                        sb.append(", ");
                }

                LogWriter.write(proceedingJoinPoint.getTarget().getClass(), logLevel, service + "." + methodName + "() args " + sb);
            }

        }

        final long startTime = System.currentTimeMillis();
        //start method execution
        final Object result = proceedingJoinPoint.proceed();

        final long endTime = System.currentTimeMillis();

        //show results
        if (result != null) {
            boolean showResults = loggableMethod != null ? loggableMethod.traceResult() : loggableClass.traceResult();
            if (showResults) {
                LogWriter.write(proceedingJoinPoint.getTarget().getClass(), logLevel, service + "." + methodName + "() Result : " + result);
            }
        }

        //show after
        LogWriter.write(proceedingJoinPoint.getTarget().getClass(), logLevel, star + service + "." + methodName + "() finished execution and takes "+(endTime-startTime)+" millis time to execute " + star);

        return result;
    }

}
