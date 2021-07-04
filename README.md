# checkstyle-order
Checkstyle plugin to validate order of tokens (properties/variables, methods).

At the moment the plugin will scan for variables that are tied to a class and validate they are in order.

## What does it look like?

```
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:96:3: borderColor should be before object on line 19 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:98:3: frame should be before object on line 19 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:99:3: fullArt should be before object on line 19 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:101:3: textless should be before uri on line 36 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:102:3: booster should be before object on line 19 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:103:3: storySpotlight should be before tcgplayerId on line 28 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:105:3: edhrecRank should be before object on line 19 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:107:3: prices should be before tcgplayerId on line 28 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/model/ScryfallCard.java:108:3: relatedUris should be before tcgplayerId on line 28 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/rest/DatasetApi.java:30:3: log should be before restHighLevelClient on line 32 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/rest/DatasetApi.java:31:3: cards should be before log on line 30 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/rest/DatasetApi.java:33:3: requestOptions should be before restHighLevelClient on line 32 [OrderedProperties]
[ERROR] /Users/hugh/Projects/mtg-deck-builder/mtg-api/src/main/java/com/kaznowski/hugh/mtgapi/rest/DatasetApi.java:35:3: objectMapper should be before restHighLevelClient on line 32 [OrderedProperties]
Audit done.
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] mtg-api 1.0.0-SNAPSHOT ............................. FAILURE [  1.433 s]
[INFO] My Application 1.0-SNAPSHOT ........................ SKIPPED
[INFO] parent-mtg-mvn-project 1.0-SNAPSHOT ................ SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.647 s
[INFO] Finished at: 2021-07-04T02:50:45+01:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.1.2:check (validate) on project mtg-api: Failed during checkstyle execution: There are 172 errors reported by Checkstyle 8.44 with ../checkstyle-config.xml ruleset. -> [Help 1]
```

## Missing features

* Exclusion based on comment or annotation (ex. `@IgnoreOrderCheck` or `//Checkstyle.Ignore`).
* Grouping based on modifiers (public, final, static etc).
* Ordering methods.
* Excluding files or package names.
* Lazy evaluation - everything is fully processed at the moment, could use iterators/streams.
* Testing

## Testing

This is hardly tested, only on the other project I am working on.
It is absolutely possible to write tests for it though, as seen in [the checkstyle repository](https://github.com/checkstyle/checkstyle/blob/master/src/test/java/com/puppycrawl/tools/checkstyle/checks/ArrayTypeStyleCheckTest.java).

## Installation

Sadly, I haven't paid for a repository.
You will have to install this the old fashioned way - clone and build.

```bash
git clone git@github.com:phughk/checkstyle-order.git
cd checkstyle-order
mvn clean install
```

## Adding to your Maven project

You are going to want to add it to the xml file describing your checkstyle preferences (`checkstyle-config.xml` in this case).
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <module name="TreeWalker">
        <module name="com.kaznowski.hugh.checkstyleorderedproperties.OrderedPropertiesCheck">
        </module>
    </module>
</module>
```

Then you will need to add it to your maven as part of validation stage

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.2</version>
    <dependencies>
        <dependency>
            <groupId>com.kaznowski.hugh</groupId>
            <artifactId>checkstyle-ordered-properties</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.puppycrawl.tools</groupId>
            <artifactId>checkstyle</artifactId>
            <version>8.44</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <configuration>
                <configLocation>checkstyle-config.xml</configLocation>
                <encoding>UTF-8</encoding>
                <consoleOutput>true</consoleOutput>
                <failsOnError>true</failsOnError>
                <linkXRef>false</linkXRef>
            </configuration>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```