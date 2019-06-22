package com.statemachine.contactengine;

import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ContactEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactEngineApplication.class, args);
    }

}

enum ContactEvents {
    SEND_INTEREST,
    ACCEPT_INTEREST,
    DECLINE_INTEREST,
    CANCEL_INTEREST,
    CANCEL_BEFORE_ACCEPTANCE,
    ACCEPT_INTEREST_AGAIN
}

enum ContactStates {
    NO_CONTACT_STATE,
    INTEREST_SENT,
    INTEREST_ACCEPTED,
    INTEREST_DECLINED,
    INTEREST_CANCELLED,
    INTEREST_CANCELLED_BEFORE_ACCEPTANCE

}

@Log
@Component
class Runner implements ApplicationRunner{

    private final StateMachineFactory<ContactStates, ContactEvents> factory;

    Runner(StateMachineFactory<ContactStates, ContactEvents> factory){
        this.factory = factory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
       StateMachine<ContactStates, ContactEvents> stateMachine = this.factory.getStateMachine("MyStateMachine");
       stateMachine.start();
       log.info("Initial State : "+stateMachine.getState().getId().name());
       stateMachine.sendEvent(ContactEvents.SEND_INTEREST);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.SEND_INTEREST.name());
       stateMachine.sendEvent(ContactEvents.ACCEPT_INTEREST);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.ACCEPT_INTEREST.name());
       stateMachine.sendEvent(ContactEvents.DECLINE_INTEREST);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.DECLINE_INTEREST.name());
       stateMachine.sendEvent(ContactEvents.ACCEPT_INTEREST_AGAIN);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.ACCEPT_INTEREST_AGAIN.name());
       stateMachine.sendEvent(ContactEvents.SEND_INTEREST);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.SEND_INTEREST.name());
       stateMachine.sendEvent(ContactEvents.CANCEL_INTEREST);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.CANCEL_INTEREST.name());
       stateMachine.sendEvent(ContactEvents.CANCEL_BEFORE_ACCEPTANCE);
       log.info("Current State : "+stateMachine.getState().getId().name()+" on Event : "+ContactEvents.CANCEL_BEFORE_ACCEPTANCE.name());
    }
}

@Log
@Configuration
@EnableStateMachineFactory
class ContactEngineStateMachineConfiguration extends StateMachineConfigurerAdapter<ContactStates, ContactEvents> {

    @Override
    public void configure(StateMachineTransitionConfigurer<ContactStates, ContactEvents> transitions) throws Exception {
        transitions
                .withExternal().source(ContactStates.NO_CONTACT_STATE).target(ContactStates.INTEREST_SENT).event(ContactEvents.SEND_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_SENT).target(ContactStates.INTEREST_ACCEPTED).event(ContactEvents.ACCEPT_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_SENT).target(ContactStates.INTEREST_CANCELLED_BEFORE_ACCEPTANCE).event(ContactEvents.CANCEL_BEFORE_ACCEPTANCE)
                .and()
                .withExternal().source(ContactStates.INTEREST_SENT).target(ContactStates.INTEREST_DECLINED).event(ContactEvents.DECLINE_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_CANCELLED_BEFORE_ACCEPTANCE).target(ContactStates.INTEREST_SENT).event(ContactEvents.SEND_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_ACCEPTED).target(ContactStates.INTEREST_DECLINED).event(ContactEvents.DECLINE_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_ACCEPTED).target(ContactStates.INTEREST_CANCELLED).event(ContactEvents.CANCEL_INTEREST)
                .and()
                .withExternal().source(ContactStates.INTEREST_DECLINED).target(ContactStates.INTEREST_ACCEPTED).event(ContactEvents.ACCEPT_INTEREST_AGAIN);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ContactStates, ContactEvents> states) throws Exception {
        states
                .withStates()
                .initial(ContactStates.NO_CONTACT_STATE)
                .state(ContactStates.INTEREST_SENT)
                .stateEntry(ContactStates.INTEREST_SENT, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Checks to be performed before event : "+ContactEvents.SEND_INTEREST);
                    }
                })
                .stateExit(ContactStates.INTEREST_SENT, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Actions to be performed after event : "+ContactEvents.SEND_INTEREST);
                    }
                })
                .state(ContactStates.INTEREST_ACCEPTED)
                .stateEntry(ContactStates.INTEREST_ACCEPTED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Checks to be performed before event : "+ContactEvents.ACCEPT_INTEREST);
                    }
                })
                .stateExit(ContactStates.INTEREST_ACCEPTED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Actions to be performed after event : "+ContactEvents.ACCEPT_INTEREST);
                    }
                })
                .state(ContactStates.INTEREST_DECLINED)
                .stateEntry(ContactStates.INTEREST_DECLINED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Checks to be performed before event : "+ContactEvents.DECLINE_INTEREST);
                    }
                })
                .stateExit(ContactStates.INTEREST_DECLINED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Actions to be performed after event : "+ContactEvents.DECLINE_INTEREST);
                    }
                })
                .state(ContactStates.INTEREST_CANCELLED)
                .stateEntry(ContactStates.INTEREST_CANCELLED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Checks to be performed before event : "+ContactEvents.CANCEL_INTEREST);
                    }
                })
                .stateExit(ContactStates.INTEREST_CANCELLED, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Actions to be performed after event : "+ContactEvents.CANCEL_INTEREST);
                    }
                })
                .state(ContactStates.INTEREST_CANCELLED_BEFORE_ACCEPTANCE)
                .stateEntry(ContactStates.INTEREST_CANCELLED_BEFORE_ACCEPTANCE, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Checks to be performed before event : "+ContactEvents.CANCEL_BEFORE_ACCEPTANCE);
                    }
                })
                .stateExit(ContactStates.INTEREST_CANCELLED_BEFORE_ACCEPTANCE, new Action<ContactStates, ContactEvents>() {
                    @Override
                    public void execute(StateContext<ContactStates, ContactEvents> context) {
                        log.info("Actions to be performed after event : "+ContactEvents.CANCEL_BEFORE_ACCEPTANCE);
                    }
                });
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ContactStates, ContactEvents> config) throws Exception {
        StateMachineListenerAdapter<ContactStates, ContactEvents> adapter = new StateMachineListenerAdapter<ContactStates, ContactEvents>() {
            @Override
            public void eventNotAccepted(Message<ContactEvents> event) {
                log.info("Event not accepted : "+event.getPayload());
            }
            @Override
            public void stateChanged(State<ContactStates, ContactEvents> from, State<ContactStates, ContactEvents> to) {
                if(from!=null)
                    log.info("State Changed from : "+from.toString()+" to :"+to.toString());
                else
                    log.info("Contact State Initialized to :"+to.toString());
            }
        };
        config.withConfiguration()
                .autoStartup(false)
                .listener(adapter);
    }
}