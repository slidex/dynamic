import java.util.*;

public class Hanoi {
    
    public static void main(String[] args) {
        Path result = new Hanoi().solve(new State(0, 0, 0), new State(2, 2, 2));
        System.out.println(result);
    }

    public static void testNextStates(String[] args) {
        State state = new State(0, 1, 2);
        state = new State(0, 0, 0);
        System.out.println(state.nextStates());
    }
    
    public Path solve(State from, State to) {
        Path result = null;
        
        Deque<Path> deque = new ArrayDeque<Path>();
        Path initialPath = new Path();
        initialPath.addState(from);
        deque.addLast(initialPath);

        while (!deque.isEmpty()) {
            Path path = deque.removeFirst();
            
            // found
            if (path.endsInState(to)) {
                result = path;
                break;
            }
            
            // to drop
            if (path.endsInLoop()) {
                continue;
            }
            
            // move forward
            List<Path> nextPaths = path.nextPaths();
            for (Path nextPath : nextPaths) {
                deque.addLast(nextPath);
            }
        }
        return result;
    }
}

class State {
    private int[] sticks = new int[3];
    
    public State(int stickL, int stickM, int stickS) {
        if (stickL < 0 || stickL > 2 || stickM < 0 || stickM > 2 || stickS < 0 || stickS > 2) {
            throw new IllegalStateException("Invalid input data.");
        }
        sticks[0] = stickL;
        sticks[1] = stickM;
        sticks[2] = stickS;
    }
    
    public State(State other) {
        sticks[0] = other.sticks[0];
        sticks[1] = other.sticks[1];
        sticks[2] = other.sticks[2];
    }
    
    public void set(int discId, int stickId) {
        if (discId < 0 || discId > 2 || stickId < 0 || stickId > 2) {
            throw new IllegalStateException("Invalid input data: " + discId + ", " + stickId);
        }
        sticks[discId] = stickId;
    }
    
    public List<State> nextStates() {
        List<State> next = new ArrayList<State>();
        
        for (int discId = 0; discId < 3; discId++) {
            for (int stickShift = 1; stickShift < 3; stickShift++) {
                int newStickId = (sticks[discId] + stickShift) % 3;
                if (isAllowedMove(discId, newStickId)) {
                    State state = new State(this);
                    state.set(discId, newStickId);
                    next.add(state);
                }
            }
        }
        
        return next;
    }
    
    private boolean isAllowedMove(int discId, int newStickId) {
        // check if smaller discs are on top of the disc to move
        for (int i = discId + 1; i < 3; i++) {
            if (sticks[discId] == sticks[i]) {
                return false;
            }
        }
        // check if the new stick has a smaller disc on it
        for (int i = discId + 1; i < 3; i++) {
            if (sticks[i] == newStickId) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        StringBuilder str = new StringBuilder("|   |   |   |");
        str.setCharAt(1 + sticks[0] * 4, 'O');
        str.setCharAt(2 + sticks[1] * 4, 'o');
        str.setCharAt(3 + sticks[2] * 4, '.');
        return str.toString();
    }
    
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof State) {
            State other = (State) obj;
            result = other.sticks[0] == sticks[0] && other.sticks[1] == sticks[1]
                    && other.sticks[2] == sticks[2];
        }
        return result;
    }
}

class Path {
    private List<State> states = new ArrayList<State>();
    
    public void addState(State state) {
        states.add(state);
    }
    
    public void addPath(Path path) {
        states.addAll(path.states);
    }
    
    public boolean endsInLoop() {
        boolean result = false;
        if (states.size() > 1) {
            State last = getLastState();
            int max = states.size() - 1;
            for (int i = 0; i < max; i++) {
                if (states.get(i).equals(last)) {
                    result = true;
                }
            }
        }
        return result;
    }

    public State getLastState() {
        return states.get(states.size() - 1);
    }

    public boolean endsInState(State state) {
        return getLastState().equals(state);
    }

    public List<Path> nextPaths() {
        List<Path> result = new ArrayList<Path>();
        List<State> nextStates = getLastState().nextStates();
        for (State nextState : nextStates) {
            Path nextPath = new Path();
            nextPath.addPath(this);
            nextPath.addState(nextState);
            result.add(nextPath);
        }
        return result;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (State state : states) {
            sb.append(state).append('\n');
        }
        return sb.toString();
    }
}
