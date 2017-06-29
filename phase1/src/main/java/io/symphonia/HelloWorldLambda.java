package io.symphonia;

public class HelloWorldLambda {

    public String handler(String input) {
        System.out.println("input = " + input);
        String output = String.format("Hello, %s!", input);
        return output;
    }
}
