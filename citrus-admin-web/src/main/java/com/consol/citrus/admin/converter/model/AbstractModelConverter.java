package com.consol.citrus.admin.converter.model;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractModelConverter<T, S> implements ModelConverter<T, S> {

    protected Pattern invalidMethodNamePattern = Pattern.compile("[\\s\\.-]");

    private final Class<S> sourceModelType;
    private final Class<T> targetModelType;

    /**
     * Default constructor using source and target model types.
     * @param targetModelType
     * @param sourceModelType
     */
    protected AbstractModelConverter(Class<T> targetModelType, Class<S> sourceModelType) {
        this.targetModelType = targetModelType;
        this.sourceModelType = sourceModelType;
    }

    @Override
    public T convert(String id, S model) {
        return convert(model);
    }

    @Override
    public T convert(S model) {
        try {
            T targetModel = getTargetModelClass().newInstance();

            ReflectionUtils.doWithMethods(getSourceModelClass(), method -> {
                Method setter = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(getTargetModelClass()))
                        .filter(m -> m.getName().equals(getSetterMethod(method.getName())))
                        .findFirst()
                        .orElse(null);
                try {
                    if (setter != null && setter.getParameterCount() == 1) {
                        if (checkTypes(setter.getParameterTypes()[0], method.getReturnType())) {
                            Object object = method.invoke(model);
                            if (object != null) {
                                setter.invoke(targetModel, object);
                            }
                        } else if (setter.getParameterTypes()[0].equals(String.class) && method.getReturnType().isPrimitive()) {
                            Object object = method.invoke(model);
                            if (object != null) {
                                setter.invoke(targetModel, object.toString());
                            }
                        }
                    }
                } catch (InvocationTargetException e) {
                    throw new ApplicationRuntimeException("Failed to get property from source model type", e);
                }
            }, method -> (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterCount() == 0);

            return targetModel;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationRuntimeException("Failed to instantiate target model type", e);
        }
    }

    /**
     * Build Java code snippet from given model and identifier.
     * @param model
     * @param id
     * @return
     */
    public String getJavaConfig(T model, String id) {
        StringBuilder builder = new StringBuilder();

        String methodName;
        Matcher matcher = invalidMethodNamePattern.matcher(id);
        if (matcher.find()) {
            methodName = StringUtils.trimAllWhitespace(id.replaceAll("\\.", "").replaceAll("-", ""));
            builder.append(String.format("\t@Bean(\"%s\")%n", id));
        } else {
            methodName = id;
            builder.append(String.format("\t@Bean%n"));
        }

        builder.append(String.format("\tpublic %s %s() {%n", getTargetModelClass().getSimpleName(), methodName));

        builder.append(String.format("\t\t%s %s = new %s();%n", getTargetModelClass().getSimpleName(), methodName, getTargetModelClass().getSimpleName()));

        ReflectionUtils.doWithMethods(model.getClass(), method -> {
            try {
                Object object = method.invoke(model);
                if (object != null) {
                    if (object instanceof String) {
                        builder.append(String.format("\t\t\t%s.%s(\"%s\");%n", methodName, getSetterMethod(method.getName()), object));
                    } else {
                        builder.append(String.format("\t\t\t%s.%s(%s);%n", methodName, getSetterMethod(method.getName()), object));
                    }
                }
            } catch (InvocationTargetException e) {
                throw new ApplicationRuntimeException("Failed to access target model property", e);
            }
        }, method -> (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterCount() == 0);

        builder.append(String.format("\t\t\treturn %s;%n", methodName));
        builder.append(String.format("\t}%n"));

        return builder.toString();
    }

    /**
     * Create Java bean setter method name from getter.
     * @param methodName
     * @return
     */
    private String getSetterMethod(String methodName) {
        if (methodName.startsWith("get")) {
            return "set" + methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            return "set" + methodName.substring(2);
        }

        return methodName;
    }

    /**
     * Check type equality including auto boxing of primitive types.
     * @param parameterType
     * @param returnType
     * @return
     */
    private boolean checkTypes(Class<?> parameterType, Class<?> returnType) {
        if (parameterType.equals(returnType)) {
            return true;
        }

        if ((parameterType.equals(Boolean.class) && returnType.equals(boolean.class)) ||
                (parameterType.equals(boolean.class) && returnType.equals(Boolean.class))) {
            return true;
        }

        if ((parameterType.equals(Integer.class) && returnType.equals(int.class)) ||
                (parameterType.equals(int.class) && returnType.equals(Integer.class))) {
            return true;
        }

        if ((parameterType.equals(Long.class) && returnType.equals(long.class)) ||
                (parameterType.equals(long.class) && returnType.equals(Long.class))) {
            return true;
        }

        if ((parameterType.equals(Short.class) && returnType.equals(short.class)) ||
                (parameterType.equals(short.class) && returnType.equals(Short.class))) {
            return true;
        }

        if ((parameterType.equals(Double.class) && returnType.equals(double.class)) ||
                (parameterType.equals(double.class) && returnType.equals(Double.class))) {
            return true;
        }

        if ((parameterType.equals(Float.class) && returnType.equals(float.class)) ||
                (parameterType.equals(float.class) && returnType.equals(Float.class))) {
            return true;
        }

        if ((parameterType.equals(Byte.class) && returnType.equals(byte.class)) ||
                (parameterType.equals(byte.class) && returnType.equals(Byte.class))) {
            return true;
        }

        return false;
    }

    @Override
    public Class<S> getSourceModelClass() {
        return sourceModelType;
    }

    @Override
    public Class<T> getTargetModelClass() {
        return targetModelType;
    }
}
