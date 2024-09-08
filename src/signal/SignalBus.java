package signal;

import java.util.ArrayList;
import java.util.List;

public class SignalBus {
    private static final List<SignalListener> listeners = new ArrayList<>();

    public static <TItem> void fire(String key, TItem item){
        var valid = listeners.stream().filter(listener -> listener.isValid(key)).toList();
        for (SignalListener listener : valid) {
            listener.apply(item);
        }
    }

    public static void subscribe(SignalListener signalListener){
        listeners.add(signalListener);
    }
}

