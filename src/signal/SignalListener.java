package signal;

import java.util.function.Consumer;

public class SignalListener<TProvider> {
    private final Consumer<TProvider> func;
    private final String key;

    public SignalListener(String key, Consumer<TProvider> func) {
        this.key = key;
        this.func = func;
    }

    public void apply(TProvider provider){
        func.accept(provider);
    }

    public boolean isValid(String key) {
        return this.key.equals(key);
    }
}
