import java.util.ArrayList;
import java.util.List;

public class Simplifier {

    private void removeEpsilon(Grammar cfg) {
        List<State> nullables = new ArrayList<State>();
        ArrayList<State> statesInGrammar = (ArrayList<State>) cfg.getStates();

        boolean isNullable;

        for (State state: statesInGrammar) {
            isNullable = false;
            for (String derivation: state.getDerivations()) {
                if (derivation.equals("$")) {
                    isNullable = true;
                }
            }
            if (isNullable) {
                nullables.add(state);
            }
        }

        boolean newNullableAdded;

        ArrayList<State> nonNullableStates = (ArrayList<State>) ((ArrayList<State>)cfg.getStates()).clone();
        nonNullableStates.removeAll(nullables);

        do {
            newNullableAdded = false;
            int nullableVariableCount;

            for (State state: nonNullableStates) {
                for (String derivation: state.getDerivations()) {
                    nullableVariableCount = 0;

                    char[] splitDerivation = derivation.toCharArray();
                    for (char variable: splitDerivation) {

                        for (State nullableState: nullables) {
                            if (nullableState.getNonTerminal() == variable) {
                                nullableVariableCount++;
                            }
                        }
                    }
                    if (nullableVariableCount == derivation.length()) {
                        nullables.add(state);
                        nonNullableStates.remove(state);
                        newNullableAdded = true;
                    }
                }
            }
        } while(newNullableAdded);


        ArrayList<String> derivations;
        for (State state: statesInGrammar) {
            derivations = (ArrayList<String>) state.getDerivations();
            ArrayList<String> derivationsToAdd = new ArrayList<String>();
            ArrayList<String> derivationsToRemove = new ArrayList<String>();

            for (String derivation: derivations) {
                for (State nullableState: nullables) {
                    if (derivation.contains(((Character)nullableState.getNonTerminal()).toString())
                            && derivation.length() != 1) {

                        char[] derivationToChar = derivation.toCharArray();
                        char[] newDerivationToAdd = new char[derivation.length()-1];
                        int index=0;
                        for(char c : derivationToChar){
                            if(c!=nullableState.getNonTerminal()){
                                newDerivationToAdd[index]=c;
                                index++;

                            }
                        }
                        String newDerivation = new String(newDerivationToAdd);
                        derivationsToAdd.add(newDerivation);
                    }
                    else if (derivation.equals("$")) {
                        derivationsToRemove.add(derivation);
                    }
                }
            }
            derivations.addAll(derivationsToAdd);
            derivations.removeAll(derivationsToRemove);
        }
    }
    private void removeUnitProduction(Grammar cfg) {
        ArrayList<String> totalRemovedUnitProductions = new ArrayList<String>();
        ArrayList<String> removedUnitProductionsPerState;
        ArrayList<State> states = (ArrayList<State>) cfg.getStates();

        for (State state: states) {
            removedUnitProductionsPerState = new ArrayList<String>();
            for (String derivation: state.getDerivations()) {
                if (derivation.length() == 1 && Character.isUpperCase(derivation.toCharArray()[0])) {
                    totalRemovedUnitProductions.add(derivation);
                    removedUnitProductionsPerState.add(derivation);
                }
            }

            if (removedUnitProductionsPerState.size() > 0) {
                for (String toRemove: removedUnitProductionsPerState) {
                    state.removeDerivation(toRemove);
                    for (String derivation: cfg.getStateWithName(toRemove.toCharArray()[0])
                            .getDerivations()) {

                        if (!derivation.equals(toRemove)){
                            state.addDerivation(derivation);
                        }
                    }
                }
            }
        }

        cfg.removeEmptyStates();
    }

    private void removeUselessStates(Grammar cfg){
        //Initialize lists for productive/unproductive symbols
        ArrayList<Character> unproductive = new ArrayList<Character>();
        ArrayList<Character> productive = new ArrayList<Character>();

        for(State state : cfg.getStates()){
            unproductive.add(state.getNonTerminal());
        }

        for(Character term : cfg.getTerminals()){
            productive.add(term);
        }


        int q=0;
        boolean placeHolder = true;

        while(placeHolder&&q<10){
            placeHolder = false;
            for(State state1 : cfg.getStates()){

                for(String deriv : state1.getDerivations()){

                    char[] derived = deriv.toCharArray();
                    int safeVerify = 0;

                    for(char i : derived){
                        if(productive.contains(i)){
                            safeVerify++;
                        }
                    }
                    if(safeVerify==deriv.length())
                    {
                        if(!productive.contains(state1.getNonTerminal())){
                            productive.add(state1.getNonTerminal());
                            int index=unproductive.indexOf(state1.getNonTerminal());
                            unproductive.remove(index);
                            placeHolder=true;

                        }
                    }
                }
            }
            q++;
        }

        boolean toDelete = false;
        for(State ridDeriv : cfg.getStates()){//
            ArrayList<String> toRemove = new ArrayList<String>();

            for(String d : ridDeriv.getDerivations()){//
                char[] derivation = d.toCharArray();
                toDelete=false;
                for(char c : derivation){
                    if(unproductive.contains(c))
                        toDelete=true;
                }
                if(toDelete)
                    toRemove.add(d);
            }
            ridDeriv.getDerivations().removeAll(toRemove);

        }

        ArrayList<State> newStates = new ArrayList<State>();

        for(char productiveState : productive){
            if(Character.isUpperCase(productiveState))

                 for(State check : cfg.getStates()){
                    if(check.getNonTerminal()==productiveState){
                        newStates.add(check);
                    }

                }
        }

        cfg.setStates(newStates);

        ArrayList<State> reachableStates = new ArrayList<State>();
        reachableStates.add(cfg.getStartState());

        for (State currentState: cfg.getStates()) {
            if (reachableStates.contains(currentState)) {
                for (String currentDerivation: currentState.getDerivations()) {
                    char[] splitDerivation = currentDerivation.toCharArray();
                    for (char currentChar: splitDerivation) {
                        if (Character.isUpperCase(currentChar)) {
                            State reachableState = cfg.getStateWithName(currentChar);
                            if (!(reachableStates.contains(reachableState))) {
                                reachableStates.add(reachableState);
                            }
                        }
                    }
                }
            }
        }

        cfg.setStates(reachableStates);

        ArrayList<Character> newTerminals = new ArrayList<Character>();
        for(State s : cfg.getStates()){
            for(String deriv : s.getDerivations()){

                char[] derived = deriv.toCharArray();
                for(char i : derived){
                    if(Character.isLowerCase(i) && !newTerminals.contains(i)){
                        newTerminals.add(i);

                    }
                }
            }
        }
        cfg.setTerminals(newTerminals);
    }


    public Grammar simplify(Grammar cfg) {
        removeEpsilon(cfg);
        removeUnitProduction(cfg);
        removeUselessStates(cfg);
        return cfg;
    }
}
