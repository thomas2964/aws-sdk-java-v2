/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.enhanced.dynamodb.internal.update;

import static software.amazon.awssdk.enhanced.dynamodb.model.UpdateExpression.keyRef;
import static software.amazon.awssdk.enhanced.dynamodb.model.UpdateExpression.valueRef;

import java.util.Collections;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DeleteUpdateAction implements UpdateAction {

    private final String actionExpression;
    private final Map<String, String> expressionNames;
    private final Map<String, AttributeValue> expressionValues;
    private final String attributeName;

    public DeleteUpdateAction(BuilderImpl builder) {
        this.actionExpression = builder.actionExpression;
        this.expressionNames = builder.expressionNames == null ? Collections.emptyMap() : builder.expressionNames;
        this.expressionValues = builder.expressionValues == null ? Collections.emptyMap() : builder.expressionValues;
        this.attributeName = builder.attributeName;
    }

    @Override
    public UpdateActionType type() {
        return UpdateActionType.DELETE;
    }

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    private static String removeValueExpression(String attributeName, String deltaName) {
        return keyRef(attributeName) + " " + valueRef(deltaName);
    }

    /**
     * Adds a numeric value (increment/decrement) or adds elements to a set.
     *
     * @param attributeName
     * @param value
     * @return
     */
    public static DeleteUpdateAction removeElements(String attributeName, AttributeValue value) {
        String valueName = attributeName + "_AMZN_DELETE";
        return builder().attributeName(attributeName)
                        .expression(removeValueExpression(attributeName, valueName))
                        .expressionNames(UpdateExpressionUtils.expressionNamesFor(attributeName, valueName))
                        .expressionValues(Collections.singletonMap(valueRef(valueName), value))
                        .build();
    }

    @Override
    public String expression() {
        return actionExpression;
    }

    @Override
    public Map<String, String> expressionNames() {
        return expressionNames;
    }

    @Override
    public Map<String, AttributeValue> expressionValues() {
        return expressionValues;
    }

    @Override
    public String attributeName() {
        return attributeName;
    }

    public static class BuilderImpl implements Builder {

        private String attributeName;
        private String actionExpression;
        private Map<String, String> expressionNames;
        private Map<String, AttributeValue> expressionValues;

        @Override
        public BuilderImpl type(UpdateActionType updateActionType) {
            return this;
        }

        @Override
        public BuilderImpl attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        @Override
        public BuilderImpl expression(String actionExpression) {
            this.actionExpression = actionExpression;
            return this;
        }

        @Override
        public BuilderImpl expressionNames(Map<String, String> expressionNames) {
            this.expressionNames = expressionNames;
            return this;
        }

        @Override
        public BuilderImpl expressionValues(Map<String, AttributeValue> expressionValues) {
            this.expressionValues = expressionValues;
            return this;
        }

        @Override
        public DeleteUpdateAction build() {
            return new DeleteUpdateAction(this);
        }
    }
}