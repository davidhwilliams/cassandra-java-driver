/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.api.core.metadata.schema;

import com.datastax.oss.driver.api.core.session.SessionBuilder;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tracks schema changes.
 *
 * <p>An implementation of this interface can be registered in the configuration, or with {@link
 * SessionBuilder#withSchemaChangeListener(SchemaChangeListener)}.
 *
 * <p>Note that the methods defined by this interface will be executed by internal driver threads,
 * and are therefore expected to have short execution times. If you need to perform long
 * computations or blocking calls in response to schema change events, it is strongly recommended to
 * schedule them asynchronously on a separate thread provided by your application code.
 *
 * <p>If you implement this interface but don't need to implement all the methods, extend {@link
 * SchemaChangeListenerBase}.
 */
public interface SchemaChangeListener extends AutoCloseable {

  void onKeyspaceCreated(@NonNull KeyspaceMetadata keyspace);

  void onKeyspaceDropped(@NonNull KeyspaceMetadata keyspace);

  void onKeyspaceUpdated(@NonNull KeyspaceMetadata current, @NonNull KeyspaceMetadata previous);

  void onTableCreated(@NonNull TableMetadata table);

  void onTableDropped(@NonNull TableMetadata table);

  void onTableUpdated(@NonNull TableMetadata current, @NonNull TableMetadata previous);

  void onUserDefinedTypeCreated(@NonNull UserDefinedType type);

  void onUserDefinedTypeDropped(@NonNull UserDefinedType type);

  void onUserDefinedTypeUpdated(
      @NonNull UserDefinedType current, @NonNull UserDefinedType previous);

  void onFunctionCreated(@NonNull FunctionMetadata function);

  void onFunctionDropped(@NonNull FunctionMetadata function);

  void onFunctionUpdated(@NonNull FunctionMetadata current, @NonNull FunctionMetadata previous);

  void onAggregateCreated(@NonNull AggregateMetadata aggregate);

  void onAggregateDropped(@NonNull AggregateMetadata aggregate);

  void onAggregateUpdated(@NonNull AggregateMetadata current, @NonNull AggregateMetadata previous);

  void onViewCreated(@NonNull ViewMetadata view);

  void onViewDropped(@NonNull ViewMetadata view);

  void onViewUpdated(@NonNull ViewMetadata current, @NonNull ViewMetadata previous);
}
