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
        T newInstance = null;
        try {
            Class<T> type = (Class<T>)Class.forName(PACKAGE + entityName + SUFFIX);
            newInstance = getFor(type);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //this castinzg wont be needed.
        //return (T)new AccountLogic();
        return newInstance;
    }
    
    public static <R> R getFor(Class<R> type) {
        R newInstance = null;
        try {
            Constructor<R> declaredConstructor = type.getDeclaredConstructor();
            newInstance = declaredConstructor.newInstance();
            
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }
        return newInstance;
    }
}
