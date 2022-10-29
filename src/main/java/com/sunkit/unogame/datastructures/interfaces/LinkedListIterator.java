package com.sunkit.unogame.datastructures.interfaces;

public interface LinkedListIterator<E> {
    boolean hasNext();
    E next();
    boolean hasPrevious();
    E previous();
    int nextIndex();
    int previousIndex();
}
