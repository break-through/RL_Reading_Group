package util;

import java.util.Objects;

public final class RightComPair<L, R extends Comparable<R>> {
    private final L left;
    private final R right;

    private RightComPair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static<L,R extends Comparable<R>>  RightComPair<L, R> make(L left, R right) {
        return new RightComPair<>(left, right);
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
        return String.format("RightComPair(%s, %s)", getLeft(), getRight());
    }

    public int compareTo(RightComPair<L,R> pair) {
        if((this.getRight().compareTo(this.getRight()) > 0)) {
            return 1;
        } else if ((this.getRight().compareTo(this.getRight()) < 0)) {
            return -1;
        } else {
            return 0;
        }
    }

}