
import org.msgpack.core.MessagePack;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.out.println("hello World");
        List<Integer> list = new ArrayList<>();
        list.add(1);

        int c = list.stream().reduce((a,b) -> a > b ? a: b).get();
        System.out.println(c);
    }

}
