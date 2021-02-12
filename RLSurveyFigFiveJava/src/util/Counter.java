package util;

public final class Counter {
    private long count;
    
    public Counter() {
        count = 0;
    }
    
    final public void increment() {
        count++;
    }
    
    final public long value() {
        return count;
    }
    
    @Override
    final public String toString() {
        return String.format("Counter(n=%s)", value());
    }
}
