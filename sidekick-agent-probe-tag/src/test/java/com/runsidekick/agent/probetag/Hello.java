package com.runsidekick.agent.probetag;

import java.util.Arrays;
import java.util.List;

public class Hello {

    private final List<String> helloMessages = Arrays.asList("merhaba", "hi", "hola");
    private final HelloMessageProvider helloMessageProvider = new HelloMessageProvider();

    public String sayHello(String name) {
        StringBuilder sb = new StringBuilder();
        int[] ia = new int[] {1, 2, 3};
        for (int i = 0; i < helloMessages.size(); i++) {
            String helloMsg = helloMessageProvider.getHelloMessage(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(helloMsg).append(" ").append(name);
        }
        return sb.toString();
    }

    public class HelloMessageProvider {

        private String getHelloMessage(int idx) {
            return helloMessages.get(idx);
        }

    }

}
