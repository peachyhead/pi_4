package cooktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class ReactiveProperty<T> {
    private T value;
    
    private final Action onChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    };
    
    public T get(){
        return value;
    }
    
    public void set (T value) {
        this.value = value;
        onChange.putValue("value", value);
    }
    
    public void subscribe(PropertyChangeListener listener) {
        onChange.addPropertyChangeListener(listener);
    }
}
