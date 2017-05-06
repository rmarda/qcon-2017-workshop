package io.symphonia;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Stack;

import java.util.List;
import java.util.stream.Collectors;

public class ApiUrlFinder {

    private static String OUTPUT_KEY = "ApiUrl";

    private final AmazonCloudFormation cfnClient = AmazonCloudFormationClientBuilder.defaultClient();

    public String find(String stackName) {
        DescribeStacksRequest describeStacksRequest =
                new DescribeStacksRequest().withStackName(stackName);

        DescribeStacksResult describeStacksResult = cfnClient.describeStacks(describeStacksRequest);

        return parseResult(describeStacksResult);
    }

    private String parseResult(DescribeStacksResult result) {
        if (result.getStacks() != null && result.getStacks().size() == 1) {
            Stack stack = result.getStacks().get(0);
            if (stack.getOutputs() != null && !stack.getOutputs().isEmpty()) {
                List<Output> outputs = stack.getOutputs().stream()
                        .filter(output -> output.getOutputKey().equals(OUTPUT_KEY))
                        .collect(Collectors.toList());
                if (!outputs.isEmpty()) {
                    return outputs.get(0).getOutputValue();
                }
            }
        }
        return null;
    }

}
