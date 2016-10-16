package adams.common;

@FunctionalInterface
public interface BiThrowableConsumer<T, U> {

    void accept(T t, U u) throws Exception;
}
