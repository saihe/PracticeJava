public class MyInterfaceImpl implements MyInterfaceExt<MyInterfaceImpl> {
    @Override
    public String getString() {
        return "実装クラスだよ。";
    }
}
