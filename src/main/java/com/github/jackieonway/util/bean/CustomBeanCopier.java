package com.github.jackieonway.util.bean;

/**
 * @author : xinghaowen
 * @className : CustomBeanCopier
 * @description :
 * @date: 2021-01-12 16:53
 */

import net.sf.cglib.asm.$ClassVisitor;
import net.sf.cglib.asm.$Type;
import net.sf.cglib.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class CustomBeanCopier {
    private static final String SET_PREFIX = "set";


    private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
    private static final $Type BEAN_COPIER = TypeUtils.parseType(CustomBeanCopier.class.getName());
    private static final Signature COPY = new Signature("copy", $Type.VOID_TYPE, new $Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT});

    interface BeanCopierKey {
        Object newInstance(String source, String target, boolean useConverter);
    }

    public static CustomBeanCopier create(Class source, Class target, boolean useConverter) {
        Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        return gen.create();
    }



    abstract public void copy(Object from, Object to);

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(CustomBeanCopier.class.getName());
        private Class source;
        private Class<?> target;
        private boolean useConverter;

        public Generator() {
            super(SOURCE);
        }

        public void setSource(Class source) {
            if (!Modifier.isPublic(source.getModifiers())) {
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        public void setTarget(Class target) {
            if (!Modifier.isPublic(target.getModifiers())) {
                setNamePrefix(target.getName());
            }
            this.target = target;
        }

        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            return source.getClassLoader();
        }

        public CustomBeanCopier create() {
            Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), useConverter);
            return (CustomBeanCopier) super.create(key);
        }

        @Override
        public void generateClass($ClassVisitor v) throws IntrospectionException, ClassNotFoundException {
            $Type sourceType = $Type.getType(source);
            $Type targetType = $Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    BEAN_COPIER,
                    null,
                    Constants.SOURCE_FILE);
            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);

            List<PropertyDescriptor> setterList=new ArrayList<>();
            Method[] result = target.getMethods();
            //查找set方法
            for (int i = 0; i < result.length; i++) {
                Method method = result[i];
                if (!method.getDeclaringClass().equals(target) || Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                String name = method.getName();
                Class<?>[] argTypes = method.getParameterTypes();
                int argCount = argTypes.length;
                if (argCount==1&&name.startsWith(SET_PREFIX)) {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(Introspector.decapitalize(name.substring(3)),null, method);
                    setterList.add( propertyDescriptor);
                }
            }
            PropertyDescriptor[]  setters=setterList.toArray(new PropertyDescriptor[setterList.size()]);
            Map<String, PropertyDescriptor> names = new HashMap(16);
            for (int i = 0; i < getters.length; i++) {
                names.put(getters[i].getName(), getters[i]);
            }
            //将Object类型强转为要转换的类型
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            e.load_arg(1);
            e.checkcast(targetType);
            e.store_local(targetLocal);
            e.load_arg(0);
            e.checkcast(sourceType);
            e.store_local(sourceLocal);
            //生成每个setter和getter方法
            for (int i = 0; i < setters.length; i++) {
                PropertyDescriptor setter = setters[i];
                PropertyDescriptor getter = names.get(setter.getName());
                if (getter != null && setter != null) {
                    Method readMethod = getter.getReadMethod();
                    Method writeMethod = setter.getWriteMethod();
                    //set的写方法没找到,或者set的写方法没有参数
                    if (writeMethod == null || readMethod == null) {
                        continue;
                    }
                    MethodInfo read = ReflectUtils.getMethodInfo(readMethod);
                    MethodInfo write = ReflectUtils.getMethodInfo(writeMethod);
                    if (compatible(getter, setter)) {
                        e.load_local(targetLocal);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.invoke(write);
                        if (!writeMethod.getReturnType().equals(void.class)){
                            e.pop();
                        }
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        @Override
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        @Override
        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}

