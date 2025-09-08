## For plugin developers
1. Add Repository

**Maven:**
```xml
<repository>
  <id>voidline-releases</id>
  <name>Voidline Repository</name>
  <url>https://maven.voidline.xyz/releases</url>
</repository>
```
**Gradle:**
```java
maven {
    name = "voidlineReleases"
    url = uri("https://maven.voidline.xyz/releases")
}
```

2. Add Dependency

**Maven:**
```xml
<dependency>
  <groupId>xyz.voidline.api</groupId>
  <artifactId>Communicator-API</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
**Gradle:**
```java
compileOnly("xyz.voidline.api:Communicator-API:1.0-SNAPSHOT")
```

3. Add Soft Dependency

```yml
softDepend:
  - Voidline communicator
```


### Api Reference
IVoidLinePlayer:

| Function    | Arguments |
|-------------|-----------|
| blockModule | String    |
| unblockAll  |           |
| setTabList  | TabList   |

TabList:

| Class      |
|------------|
| Builder    |


| Function   | Arguments |
|------------|-----------|
| getHeader  |           |
| getFooter  |           |

TabList.Builder:

| Function  | Arguments |
|-----------|-----------|
| setHeader | String    |
| setFooter | String    |

Custom events:

| Event                   | Arguments             |
|-------------------------|-----------------------|
| VoidLinePlayerJoinEvent | UUID, IVoidLinePlayer |

Event registration:
```java
EventManager.register(EventClass.class, event-> {

});
```

### Examples:
```java
public class Testing extends PluginBase implements Listener {
    @Override
    public void onEnable() {
        EventManager.register(VoidLinePlayerJoinEvent.class, event-> {
            event.getVoidLinePlayer().blockModule("armorhud");
        });
    }
}
```