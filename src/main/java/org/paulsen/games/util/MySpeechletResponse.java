package org.paulsen.games.util;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value
public class MySpeechletResponse extends SpeechletResponse {
    private final OutputSpeech outputSpeech;
    private final Card card;
    private final Reprompt reprompt;
    private boolean shouldEndSession;

    public MySpeechletResponse(OutputSpeech speech, Reprompt prompt) {
        this(speech, prompt, null);
    }

    public MySpeechletResponse(OutputSpeech speech, Reprompt prompt, boolean end) {
        this(speech, prompt, null, end);
    }

    public MySpeechletResponse(OutputSpeech speech, Reprompt prompt, Card card) {
        this(speech, prompt, card, true);
    }

    public MySpeechletResponse(OutputSpeech speech, Reprompt prompt, Card card, boolean end) {
        this.outputSpeech = speech;
        this.card = card;
        this.reprompt = prompt;
        this.shouldEndSession = end;
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public boolean getShouldEndSession() {
        return this.shouldEndSession;
    }
}
