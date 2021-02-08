package util;

import java.util.Objects;

public final class Pair<L, R> {
    private final L left;
    private final R right;
    
    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    
    public static<L,R>  Pair<L, R> make(L left, R right) {
        return new Pair<>(left, right);
    }
    
    public L getLeft() {
        return left;
    }
    
    public R getRight() {
        return right;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left.hashCode(), right.hashCode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return getLeft().equals(other.getLeft())
            && getRight().equals(other.getRight());
    }
    
    @Override
    public String toString() {
        return String.format("Pair(%s, %s)", getLeft(), getRight());
    }
}
