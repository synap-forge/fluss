/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fluss.flink.adapter;

import org.apache.flink.util.MultipleParameterTool;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Flink 2.3 implementation of the command-line argument adapter used by the tiering job.
 *
 * <p>Flink 2.x moved {@link MultipleParameterTool} from {@code org.apache.flink.api.java.utils} to
 * {@code org.apache.flink.util}. This class replaces the Flink 1.x implementation packaged by
 * {@code fluss-flink-common} in the shaded tiering artifact.
 */
public class MultipleParameterToolAdapter {

    private MultipleParameterToolAdapter() {}

    private MultipleParameterTool multipleParameterTool;

    public static MultipleParameterToolAdapter fromArgs(String[] args) {
        MultipleParameterToolAdapter adapter = new MultipleParameterToolAdapter();
        adapter.multipleParameterTool = MultipleParameterTool.fromArgs(args);
        return adapter;
    }

    public Map<String, String> toMap() {
        return this.multipleParameterTool.toMap();
    }

    /** Returns whether the given key is present in the parsed arguments. */
    public boolean has(String key) {
        return this.multipleParameterTool.has(key);
    }

    /** Returns the value for the given key, or {@code null} if the key is not found. */
    @Nullable
    public String get(String key) {
        return this.multipleParameterTool.get(key);
    }

    /**
     * Returns all values associated with the given key, or {@code null} if the key is not found.
     */
    @Nullable
    public Collection<String> getMultiParameter(String key) {
        return this.multipleParameterTool.getMultiParameter(key);
    }
}
