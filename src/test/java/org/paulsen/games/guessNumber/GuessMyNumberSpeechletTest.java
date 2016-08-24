package org.paulsen.games.guessNumber;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.paulsen.games.util.MySpeechletResponse;


/**
 * Created by paulsen on 8/14/16.
 */
public class GuessMyNumberSpeechletTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void jsonShouldContainShouldEndSessionFalse() throws Exception {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Doesn't matter.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        //SpeechletResponse resp = SpeechletResponse.newAskResponse(speech, reprompt);
        SpeechletResponse resp = new MySpeechletResponse(speech, reprompt, false);
        //resp.setShouldEndSession(true);

        System.out.println("###########");
        System.out.println("###########");
        System.out.println("###########");
        System.out.println(mapper.writeValueAsString(resp));
        System.out.println("###########");
        System.out.println("###########");
        System.out.println("###########");
    }
}
