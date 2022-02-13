package threadlocal;

public class ThreadLocalTest {
    public static void main(String[] args) {
        ThreadLocal<String> localName = new ThreadLocal();
        localName.set("tom");
        String name = localName.get();
        Thread thread = Thread.currentThread();
    }
}
