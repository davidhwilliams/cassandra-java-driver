<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

## Address resolution

The driver auto-detects new Cassandra nodes added to the cluster through server
side push notifications and through checking the system tables.

For each node, the address the driver will receive will correspond to the address set as
`rpc_address` in the node's `cassandra.yaml` file. In most cases, this is the correct value.
However, sometimes the addresses received through this mechanism will either not be
reachable directly by the driver, or should not be the preferred address to use
(for instance, it might be a private IP, but some clients  may have to use a public IP, or
go through a router).

### The [AddressTranslater][at] interface

This interface allows you to deal with such cases, by transforming the address sent by a
Cassandra node to another address to be used by the driver for connection.

```java
public class MyAddressTranslater implements AddressTranslater {
    public InetSocketAddress translate(InetSocketAddress address) {
        ... // your custom translation logic
    }
}

Cluster cluster = Cluster.builder()
    .addContactPoint("1.2.3.4")
    .withAddressTranslater(new MyAddressTranslater())
    .build();
```

Notes:

* the contact points provided while creating the `Cluster` are not translated, only addresses
  retrieved from or sent by Cassandra nodes are;
* you might want to implement [CloseableAddressTranslater][cat] if your
  implementation has state that should be cleaned up when the `Cluster`
  shuts down. This is provided as a separate interface for historical
  reasons, the `close()` method will be merged in `AddressTranslater` in a
  future release.

[at]: http://docs.datastax.com/en/drivers/java/2.0/com/datastax/driver/core/policies/AddressTranslater.html
[cat]: http://docs.datastax.com/en/drivers/java/2.0/com/datastax/driver/core/policies/CloseableAddressTranslater.html

### EC2 multi-region

[EC2MultiRegionAddressTranslater][ec2] is provided out of the box. It
helps optimize network costs when your infrastructure (both Cassandra
nodes *and* clients) is distributed across multiple Amazon EC2 regions:

* a client communicating with a Cassandra node *in the same EC2 region* should use the node's
  private IP (which is less expensive);
* a client communicating with a node in a different region should use the public IP.

To use this implementation, provide an instance when initializing the `Cluster`:

```java
Cluster cluster = Cluster.builder()
    .addContactPoint("1.2.3.4")
    .withAddressTranslater(new EC2MultiRegionAddressTranslater())
    .build();
```

This class performs a reverse DNS lookup of the origin address, to find the domain name of the
target instance. Then it performs a forward DNS lookup of the domain name; the EC2 DNS does the
private/public switch automatically based on location.

[ec2]: http://docs.datastax.com/en/drivers/java/2.0/com/datastax/driver/core/policies/EC2MultiRegionAddressTranslater.html