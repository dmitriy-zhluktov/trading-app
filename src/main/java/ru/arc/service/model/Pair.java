package ru.arc.service.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
final public class Pair<L, R> {
    public final L left;
    public final R right;
}
