package io.symphonia;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"--stack", "-s"})
    String stackName = "serverless-weather";

    @Parameter(names = {"--limit", "-l"})
    int limit = 50;

    @Parameter(names = {"--invalid"})
    boolean invalid = false;

    @Parameter(names = {"--test"})
    boolean test = false;
}
