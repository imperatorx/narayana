/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.compensations.internal;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.manager.BeanManagerLookupService;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

public class BeanManagerUtil {

    public static BeanManager getBeanManager() {
        return CDI.current().getBeanManager();
    }

    public static <T> T createBeanInstance(Class<T> clazz) {
        return createBeanInstance(clazz, getBeanManager());
    }

    @SuppressWarnings("unchecked")
    public static <T> T createBeanInstance(Class<T> clazz, BeanManager beanManager) {
        BeanManager classBeanManager = getClassBeanManager(clazz, beanManager);
        Bean<T> bean = (Bean<T>) classBeanManager.resolve(classBeanManager.getBeans(clazz));
        if (bean == null) {
            throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type " + clazz.getName());
        }
        CreationalContext<T> context = classBeanManager.createCreationalContext(bean);
        return (T) classBeanManager.getReference(bean, clazz, context);
    }

    private static <T> BeanManager getClassBeanManager(Class<T> clazz, BeanManager beanManager) {
        BeanManagerImpl unwrappedBeanManager = BeanManagerProxy.unwrap(beanManager);
        return BeanManagerLookupService.lookupBeanManager(clazz, unwrappedBeanManager);
    }

}
