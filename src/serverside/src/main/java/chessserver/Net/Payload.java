package chessserver.Net;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface Payload {
    // "primitives (and str)"
    record Empty() implements Payload {}
    record StringPayload(String payload) implements Payload {}
    record BooleanPayload(boolean payload) implements Payload {}
    record IntegerPayload(int payload) implements Payload {}
    record DoublePayload(double payload) implements Payload {}

    // more advanced but still generic payloads
    record StringListPayload(List<String> payload) implements Payload {}
    record IntegerListPayload(List<Integer> payload) implements Payload {}

}

