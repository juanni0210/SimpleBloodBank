package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";

    private LogicFactory() {
    }

    //TODO this code is not complete, it is just here for sake of programe working. need to be changed ocmpletely
    public static <T> T getFor(String entityName) {
        try {
            Class<T> type = (Class<T>)Class.forName(PACKAGE + entityName + SUFFIX);
            T newInstance = getFor(type);
            return newInstance;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //this castinzg wont be needed.
        //return (T)new AccountLogic();
        return null;
    }
    
    public static <R> R getFor(Class<R> type) {
        try {
            Constructor<R> declaredConstructor = type.getDeclaredConstructor();
            R newInstance = declaredConstructor.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
