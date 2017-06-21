package io.symphonia;

public class HelloWorldLambda {

    public String handler(String input) {
        System.out.println("input = " + input);
        return input;
    }
}
