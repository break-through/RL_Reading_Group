package util;

import java.util.Objects;

public class Triplet<L, M, R> {
    private final L left;
    private final M middle;
    private final R right;
    
    private Triplet(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    
    public static<L, M, R>  Triplet<L, M, R> make(L left, M middle, R right) {
        return new Triplet<>(left, middle, right);
    }
    
    public L getLeft() {
        return left;
    }
    
    public M getMiddle() {
        return middle;
    }
    
    public R getRight() {
        return right;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(left.hashCode(), middle.hashCode(), right.hashCode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Triplet)) {
            return false;
        }
        Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
        return getLeft().equals(other.getLeft())
            && getMiddle().equals(other.getMiddle())
            && getRight().equals(other.getRight());
    }
    
    @Override
    public String toString() {
        return String.format("Triplet(%s, %s, %s)", getLeft(), getMiddle(), getRight());
    }
}
