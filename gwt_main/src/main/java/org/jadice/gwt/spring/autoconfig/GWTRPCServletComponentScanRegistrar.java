/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jadice.gwt.spring.autoconfig;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * {@link ImportBeanDefinitionRegistrar} used by {@link ServletComponentScan}.
 *
 * This class ist in large parts a copy of {@link ServletComponentScanRegistrar} which isn't
 * extensible, unfortunately.
 */
class GWTRPCServletComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

  private static final String BEAN_NAME = "gwtServiceRegisteringPostProcessor";

  @Override
  public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata,
      final BeanDefinitionRegistry registry) {
    Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
    if (registry.containsBeanDefinition(BEAN_NAME)) {
      updatePostProcessor(registry, packagesToScan);
    } else {
      addPostProcessor(registry, packagesToScan);
    }
  }

  private void updatePostProcessor(final BeanDefinitionRegistry registry, final Set<String> packagesToScan) {
    BeanDefinition definition = registry.getBeanDefinition(BEAN_NAME);
    ValueHolder constructorArguments = definition.getConstructorArgumentValues().getGenericArgumentValue(Set.class);
    @SuppressWarnings("unchecked")
    Set<String> mergedPackages = (Set<String>) constructorArguments.getValue();
    mergedPackages.addAll(packagesToScan);
    constructorArguments.setValue(mergedPackages);
  }

  private void addPostProcessor(final BeanDefinitionRegistry registry, final Set<String> packagesToScan) {
    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(GWTRPCServletComponentRegisteringPostProcessor.class);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(packagesToScan);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
  }

  private Set<String> getPackagesToScan(final AnnotationMetadata metadata) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        metadata.getAnnotationAttributes(GWTRPCService.class.getName()));
    // String[] basePackages = attributes.getStringArray("basePackages");
    // Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
    Set<String> packagesToScan = new LinkedHashSet<String>();
    // packagesToScan.addAll(Arrays.asList(basePackages));
    // for (Class<?> basePackageClass : basePackageClasses) {
    // packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
    // }
    // if (packagesToScan.isEmpty()) {
    return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
    // }
    // return packagesToScan;
  }

}
