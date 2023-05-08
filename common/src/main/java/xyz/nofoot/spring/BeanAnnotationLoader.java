package xyz.nofoot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static xyz.nofoot.constants.SpringConstants.BEAN_BASE_PACKAGE;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.spring
 * @className: CustomLoader
 * @author: NoFoot
 * @date: 4/18/2023 5:22 PM
 * @description: 通过配置文件获取要加载 bean 的注解（就是一个类上面有这个注解就加载这个bean）
 */
@Slf4j
public class BeanAnnotationLoader {

    /**
     * @return: List<Class < ?>>
     * @author: NoFoot
     * @date: 4/18/2023 6:16 PM
     * @description: 根据文件名加载注解类对象，文件在 resources/META-INF/spring/
     */
    private static List<Class<? extends Annotation>> loadAnnotationClasses(String fileName) {
        ClassLoader classLoader = BeanAnnotationLoader.class.getClassLoader();
        URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/spring/" + fileName);
        ArrayList<Class<? extends Annotation>> classes = new ArrayList<>();

        if (url == null) {
            log.warn("未读取到文件:[{}]", fileName);
            return classes;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        if (line.startsWith(BEAN_BASE_PACKAGE)) {
                            Class<?> clazz = classLoader.loadClass(line);
                            if (clazz.isAnnotation()) {
                                // TODO 判断这个注解是否能加到类上
                                classes.add((Class<? extends Annotation>) clazz);
                            } else {
                                log.warn("非注解类型不会被加载, 请检查文件:[{}]", fileName);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("类加载错误, 请检查文件:[{}]", fileName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("文件加载失败:[{}]", fileName);
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * @return: List<Class < Annotation>>
     * @author: NoFoot
     * @date: 4/18/2023 6:45 PM
     * @description: 获取 beanAnnotation 文件中的所有注解类对象
     */
    public static List<Class<? extends Annotation>> loadSpringBeanAnnotationClasses() {
        List<Class<? extends Annotation>> classes = loadAnnotationClasses("beanAnnotation");
        // 默认添加 Component.class
        // 不添加也没事，Spring 会自己添加
        classes.add(Component.class);
        return classes;
    }

    /**
     * @return: List<Class < Annotation>>
     * @author: NoFoot
     * @date: 4/18/2023 6:45 PM
     * @description: 获取 rpcBeanAnnotation 文件中的所有注解类对象
     */
    public static List<Class<? extends Annotation>> loadRpcBeanAnnotationClasses() {
        return loadAnnotationClasses("rpcBeanAnnotation");
    }

    /**
     * @param fileName: 自定义文件名
     * @return: List<Class < Annotation>>
     * @author: NoFoot
     * @date: 4/18/2023 6:45 PM
     * @description: 获取 自定义 文件中的所有注解类对象
     */
    public static List<Class<? extends Annotation>> loadCustomBeanAnnotationClasses(String fileName) {
        return loadAnnotationClasses(fileName);
    }

}
