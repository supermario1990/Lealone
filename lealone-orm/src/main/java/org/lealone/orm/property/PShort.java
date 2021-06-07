/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.orm.property;

import java.io.IOException;

import org.lealone.db.value.Value;
import org.lealone.db.value.ValueShort;
import org.lealone.orm.Model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;

/**
 * Short property.
 *
 * @param <R> the root model bean type
 */
public class PShort<R> extends PBaseNumber<R, Short> {

    private short value;

    /**
     * Construct with a property name and root instance.
     *
     * @param name property name
     * @param root the root model bean instance
     */
    public PShort(String name, R root) {
        super(name, root);
    }

    private PShort<R> P(Model<?> model) {
        return this.<PShort<R>> getModelProperty(model);
    }

    public final R set(short value) {
        Model<?> model = getModel();
        if (model != root) {
            return P(model).set(value);
        }
        if (!areEqual(this.value, value)) {
            this.value = value;
            expr().set(name, ValueShort.get(value));
        }
        return root;
    }

    @Override
    public R set(Object value) {
        return set(Short.valueOf(value.toString()).shortValue());
    }

    public final short get() {
        Model<?> model = getModel();
        if (model != root) {
            return P(model).get();
        }
        return value;
    }

    @Override
    public R serialize(JsonGenerator jgen) throws IOException {
        jgen.writeNumberField(getName(), value);
        return root;
    }

    @Override
    public R deserialize(JsonNode node) {
        node = getJsonNode(node);
        if (node == null) {
            return root;
        }
        set(((NumericNode) node).asInt());
        return root;
    }

    @Override
    protected void deserialize(Value v) {
        value = v.getShort();
    }
}
