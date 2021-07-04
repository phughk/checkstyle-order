# checkstyle-order
Checkstyle plugin to validate order of tokens (properties/variables, methods).

At the moment the plugin will scan for variables that are tied to a class and validate they are in order.

## Missing features

* Exclusion based on comment or annotation (ex. `@IgnoreOrderCheck` or `//Checkstyle.Ignore`).
* Grouping based on modifiers (public, final, static etc).
* Ordering methods.
* Excluding files or package names.
* Lazy evaluation - everything is fully processed at the moment, could use iterators/streams.

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