package com.runsidekick.agent.logpoint;

import java.util.ArrayList;
import java.util.List;

public class CollectionTest {

    public static void listTest() {
        List<Item> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item t = new Item();
            testList.add(t);
        }
        System.out.println(testList.size());
    }


}
