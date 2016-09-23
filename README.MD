## Building

It needs to accomplish the following commands in order to build the producer and consumer:

```
mvn install
```

And also there is an approach to run all modules + RabbitMQ within a virtual machine by Vagrant,
for that, it needs to invoke commands:

```
vagrant up                                      # It can take quite long time.
vagrant ssh -c "cd /vagrant && ./start.sh"

```

## Principle

 - the producer generates random phrase from 10 hardcoded words once per a second;
 - the consumer split phrases to words and agregates number of words
 - the consumer uses a local file system for saving word counters
 - a special script shows state of word counters from the directory ./words


