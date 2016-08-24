/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.paulsen.games.guessNumber;

import com.amazon.speech.ui.OutputSpeech;
import org.paulsen.games.util.MySpeechletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

import java.util.Random;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class GuessMyNumberSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(GuessMyNumberSpeechlet.class);
    private static final Random rand = new Random();
    private static final String THE_NUMBER = "theNumber";
    private static final String TRY_COUNT = "tries";
    private static final String TITLE = "Guess My Number";

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        int number = rand.nextInt(100) + 1;
        log.info("onSessionStarted number={}, requestId={}, sessionId={}, new={}",
                number,
                request.getRequestId(),
                session.getSessionId(),
                session.isNew());
        session.setAttribute(THE_NUMBER, number);
        session.setAttribute(TRY_COUNT, 0);
    }

    /**
     * Asked when "open ..." is said. Or when "ask ..." w/o anything after is said.
     */
    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        try {
            if ("GuessNumberIntent".equals(intentName)) {
                log.info("onIntent GuessNumberIntent!");
                return getGuessNumberResponse(request, session);
            } else if ("AMAZON.HelpIntent".equals(intentName)) {
                log.info("onIntent HELPIntent!");
                return getHelpResponse();
            } else if ("AMAZON.CancelIntent".equals(intentName)) {
                log.info("onIntent CancelIntent!");
                return getStopResponse();
            } else if ("AMAZON.StopIntent".equals(intentName)) {
                log.info("onIntent StopIntent!");
                return getStopResponse();
            } else {
                throw new SpeechletException("Invalid Intent: " + intentName);
            }
        } catch (Throwable ex) {
            log.info("Error: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}",
                request.getRequestId(),
                session.getSessionId());
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the number guessing game. I've picked a number between one "
                + "and one hundred, guess it!";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(TITLE);
        card.setContent(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(getSpeech("Say a number between one and one hundred."));

        return new MySpeechletResponse(getSpeech(speechText), reprompt, card, false);
        //return SpeechletResponse.newAskResponse(getSpeech(speechText), reprompt, card);
    }

    private SpeechletResponse getGuessNumberResponse(final IntentRequest request, final Session session) {
        // Get the answer
        int answer = (Integer) session.getAttribute(THE_NUMBER);
        int count = (Integer) session.getAttribute(TRY_COUNT);
        session.setAttribute(TRY_COUNT, ++count);

        // Get the guess
        String guessStr = request.getIntent().getSlot("guess").getValue();
        int guess = 0;
        if (guessStr != null) {
            guess = Integer.valueOf(guessStr);
        }

        SpeechletResponse resp;
        if (guess == answer) {
            String speechText = (count == 1) ? ("Are you telepathic? You guessed it on the first try! "
                    + "Amazing!") : ("Congratulations!  You guessed it in " + count + " tries!");

            // Create the Simple card content.
            SimpleCard card = new SimpleCard();
            card.setTitle(TITLE);
            card.setContent(speechText);

            //resp = SpeechletResponse.newTellResponse(getSpeech(speechText), card);
            resp = new MySpeechletResponse(getSpeech(speechText), null, card, true);
        } else {
            OutputSpeech errorMessage;
            Reprompt prompt = new Reprompt();
            prompt.setOutputSpeech(getSpeech("Guess again."));
            if (guess < answer) {
                errorMessage = getSpeech("Sorry... too low!");
            } else {
                errorMessage = getSpeech("Sorry... too high!");
            }
            //resp = SpeechletResponse.newAskResponse(errorMessage, prompt);
            resp = new MySpeechletResponse(errorMessage, prompt, false);
        }
        log.info("NUMBER GUESS: {} was guessed; {} was the answer; should end? {}",
                guess, answer, resp.getShouldEndSession());

        return resp;
    }

    private OutputSpeech getSpeech(String str) {
        PlainTextOutputSpeech out = new PlainTextOutputSpeech();
        out.setText(str);
        return out;
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "Say a number between one and one hundred, I will tell you if it is "
                + "too high, too low, or just right.";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(TITLE);
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        //return SpeechletResponse.newAskResponse(speech, reprompt, card);
        return new MySpeechletResponse(speech, reprompt, card, false);
    }

    /**
     * Creates a {@code SpeechletResponse} for the stop and cancel intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getStopResponse() {
        String speechText = "Thank you for playing Guess My Number.";

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return new MySpeechletResponse(speech, reprompt);
    }
}
