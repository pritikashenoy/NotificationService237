# NotificationService237
Start zookeeper:
```
/usr/local/opt/kafka/bin/zookeeper-server-start /usr/local/etc/zookeeper/zoo.cfg &
```

Start 3 kafka brokers: (copied server props for each, Modified broker id, listener port and logs.dir in server.properties for each broker)
```
/usr/local/opt/kafka/bin/kafka-server-start /usr/local/etc/kafka/server.properties &
/usr/local/opt/kafka/bin/kafka-server-start /usr/local/etc/kafka/server.properties.1 &
/usr/local/opt/kafka/bin/kafka-server-start /usr/local/etc/kafka/server.properties.2 &
```

To view topic configurations:

List topics:
```
/usr/local/opt/kafka/bin/kafka-topics --list --bootstrap-server localhost:9092
```
Per topic config:
```
/usr/local/opt/kafka/bin/kafka-topics --describe --topic weather --bootstrap-server localhost:9092
```

For address already in use errors, check:
```
lsof -i :<port>
```
To kill the process:
```
lsof -t -i :<port> | xargs kill -9
```
