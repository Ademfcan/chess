package chessengine;

import chessserver.FrontendClient;
import chessserver.INTENT;
import chessserver.InputMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class jsontests {
    @Test
    void ClassToJsonToClass(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputMessage input = new InputMessage(new FrontendClient("Adem",100), INTENT.MAKEMOVE,"extra");
            String message = objectMapper.writeValueAsString(input);
            InputMessage fromjson = objectMapper.readValue(message, InputMessage.class);
            Assertions.assertEquals(input.getClient().getElo(),fromjson.getClient().getElo());
            Assertions.assertEquals(input.getClient().getName(),fromjson.getClient().getName());
            Assertions.assertEquals(input.getIntent(),fromjson.getIntent());
            Assertions.assertEquals(input.getExtraInformation(),fromjson.getExtraInformation());
            System.out.println("Sucess!");
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }

    }
}
