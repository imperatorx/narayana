/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.narayana.compensations.internal;

import org.jboss.narayana.compensations.api.Compensatable;
import org.jboss.narayana.compensations.api.CompensationManager;

import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class CompensationInterceptorBase {

    @Inject
    CompensationManager compensationManager;

    @Inject
    jakarta.enterprise.inject.spi.BeanManager beanManager;

    protected Object invokeInOurTx(InvocationContext ic) throws Exception {

        BAController baController;
        Compensatable compensatable = getCompensatable(ic);
        if (compensatable.distributed()) {
            baController = BAControllerFactory.getRemoteInstance();
        } else {
            baController = BAControllerFactory.getLocalInstance();
        }
        baController.beginBusinessActivity();

        Object result = null;
        boolean isException = false;

        try {
            result = ic.proceed();
        } catch (Exception e) {
            isException = true;
            handleException(ic, e, true);
        } finally {
            baController.completeBusinessActivity(isException);
        }

        return result;
    }

    protected Object invokeInCallerTx(InvocationContext ic) throws Exception {

        Object result = null;

        try {
            result = ic.proceed();
        } catch (Exception e) {
            handleException(ic, e, false);
        }

        return result;
    }

    protected Object invokeInNoTx(InvocationContext ic) throws Exception {

        return ic.proceed();
    }


    private void handleException(final InvocationContext ic, final Exception exception, final boolean started) throws Exception {

        final Compensatable compensatable = getCompensatable(ic);

        if (isDontCancelOn(compensatable, exception)) {
            throw exception;
        }

        if (isCancelOn(compensatable, exception) || exception instanceof RuntimeException) {
            compensationManager.setCompensateOnly();
        }

        throw exception;
    }

    private boolean isDontCancelOn(final Compensatable compensatable, final Exception exception) {

        for (Class dontCancelOnClass : compensatable.dontCancelOn()) {
            if (dontCancelOnClass.isAssignableFrom(exception.getClass())) {
                return true;
            }
        }

        return false;
    }

    private boolean isCancelOn(final Compensatable compensatable, final Exception exception) {

        for (Class cancelOnClass : compensatable.cancelOn()) {
            if (cancelOnClass.isAssignableFrom(exception.getClass())) {
                return true;
            }
        }

        return false;
    }

    private Compensatable getCompensatable(InvocationContext ic) {

        Compensatable compensatable = ic.getMethod().getAnnotation(Compensatable.class);
        if (compensatable != null) {
            return compensatable;
        }

        Class<?> targetClass = ic.getTarget().getClass();
        compensatable = targetClass.getAnnotation(Compensatable.class);
        if (compensatable != null) {
            return compensatable;
        }

        for (Annotation annotation : ic.getMethod().getDeclaringClass().getAnnotations()) {
            if (beanManager.isStereotype(annotation.annotationType())) {
                for (Annotation stereotyped : beanManager.getStereotypeDefinition(annotation.annotationType())) {
                    if (stereotyped.annotationType().equals(Compensatable.class)) {
                        return (Compensatable) stereotyped;
                    }
                }
            }
        }

        throw new RuntimeException("Expected an @Compensatable annotation at class and/or method level");
    }

}