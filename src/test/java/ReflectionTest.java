import java.lang.reflect.Method;

/**
 * @author Him188 @ MyMap Project
 */
public class ReflectionTest {
    public static void main(String[] args) throws Exception {
        Method method = ReflectionTest.class.getMethod("test");

        long time;
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            method.invoke(null);
        }
        System.out.println(System.currentTimeMillis() - time);//109

        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            test();
        }
        System.out.println(System.currentTimeMillis() - time);//4
    }

    public static void test() {
        int i = 1 + 2;
    }
}
