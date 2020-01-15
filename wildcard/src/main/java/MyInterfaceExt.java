public interface MyInterfaceExt<T> extends MyInterface<T> {
    default String getString() {
        return "共通処理";
    }
}
