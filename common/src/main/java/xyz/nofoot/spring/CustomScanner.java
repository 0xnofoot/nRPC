package xyz.nofoot.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: CustomScanner
 * @author: NoFoot
 * @date: 4/18/2023 3:57 PM
 * @description: 自定义包扫描类
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {
    /**
     * @param registry:
     * @return: null
     * @author: NoFoot
     * @date: 4/18/2023 7:42 PM
     * @description:
     */
    public CustomScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * @param annotationType:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/2023 7:42 PM
     * @description: 添加一个扫描注解
     */
    public void addIncludeAnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
    }

    /**
     * @param annotationTypes:
     * @return: void
     * @author: NoFoot
     * @date: 4/18/2023 7:42 PM
     * @description: 添加多个扫描注解
     */
    public void addIncludeAnnotationTypeFilters(List<Class<? extends Annotation>> annotationTypes) {
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            addIncludeAnnotationTypeFilter(annotationType);
        }
    }

}
