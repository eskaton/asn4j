/*
 *  Copyright (c) 2015, Adrian Moser
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author nor the
 *  names of its contributors may be used to endorse or promote products
 *  derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL AUTHOR BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.eskaton.asn4j.parser;

import ch.eskaton.asn4j.parser.ast.SourcePosition;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.Objects;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class Position implements Comparable<Position> {

    private String file;

    private int line;

    private int position;

    protected Position() {
    }

    public Position(String file, int line, int position) {
        this.file = file;
        this.line = line;
        this.position = position;
    }

    public static Position of(Object... objects) {
        for (Object object : objects) {
            if (object instanceof SourcePosition) {
                return ((SourcePosition) object).getPosition();
            }
        }

        return NO_POSITION;
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }

    public void decrementPosition() {
        position--;
    }

    @Override
    public int compareTo(Position other) {
        if (!file.equals(other.file)) {
            return file.compareTo(other.file);
        }

        if (line != other.line) {
            return line - other.line;
        }

        if (position != other.position) {
            return position - other.position;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Position position1 = (Position) o;

        return line == position1.line &&
                position == position1.position &&
                Objects.equals(file, position1.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line, position);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
