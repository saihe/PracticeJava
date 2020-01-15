public class UseWildCard {
    public static UseWildCard getInstance() {
        return new UseWildCard();
    }
    public void run() {
        try {
            execute("MyInterfaceImplを呼び出したい。", MyInterfaceImpl.class);
            execute("違う実装クラスを呼び出したい。", OtherInterfaceImpl.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void execute(String name, Class<? extends MyInterface> myInterface) throws InstantiationException, IllegalAccessException {
        System.out.println(name);
        System.out.println(getInstance(myInterface).getString());
    }

    public MyInterface getInstance(Class<? extends MyInterface> myInterfaceClass) throws IllegalAccessException, InstantiationException {
        return myInterfaceClass.newInstance();
    }

}
