public interface MyInterface<T> {
    default String getString() {
        return "共通処理";
    }
}
