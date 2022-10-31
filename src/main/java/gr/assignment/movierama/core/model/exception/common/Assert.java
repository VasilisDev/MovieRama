package gr.assignment.movierama.core.model.exception.common;


import gr.assignment.movierama.core.model.exception.ApplicationException;

import java.util.Map;

public interface Assert {

    ApplicationException newException();
    ApplicationException newException(Map<String, Object> data);
    ApplicationException newException(Throwable t, Map<String, Object> data);

    default void isInstanceOf(Class<?> type, Object obj, Map<String, Object> data) {
        //TODO: Nullability check
        if (!type.isInstance(obj)) {
            throw newException(data);
        }
    }

    default void isTrue(boolean condition) {
        if (!condition) {
            throw newException();
        }
    }
    default void isTrue(boolean condition, Map<String, Object> data) {
        if (!condition) {
            throw newException(data);
        }
    }

    default void notNull(Object obj, Map<String, Object> data) {
        if (obj == null) {
            throw newException(data);
        }
    }
}