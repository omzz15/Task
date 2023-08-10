**This is the FTC version of the library, meaning this uses java 8 and has instructions for installing this to an FTC project. For the normal version go [here](https://github.com/omzz15/task)**

[![javadoc](https://javadoc.io/badge2/io.github.omzz15/task/javadoc.svg)](https://javadoc.io/doc/io.github.omzz15/task)
# Java Task

The Task library is a versatile Java library engineered to streamline the creation and management of tasks using groups,
which can be interconnected to establish intricate hierarchies.
At its core, a task can be any executable code that implements the Runnable interface.
However, this library introduces many advanced task implementations.
From timed tasks to tasks with sequential steps,
the library equips developers with an extensive toolkit to cater to various task execution requirements.

The library also allows the managing of complex event structures.
By seamlessly integrating tasks into event-driven programming, the library enhances efficiency and flexibility.
This allows tasks to be driven by events, resulting in streamlined execution and optimized resource utilization.

## What does it do?
- Flexible Task Management: The library provides a highly flexible framework for creating, managing, and executing tasks. Tasks are not limited to simple operations; the framework offers advanced task types, including queue tasks and tasks with incrementation.

- Hierarchical Task Organization: Developers can organize tasks using groups that can be linked to create complex hierarchies. This feature enables the structuring of tasks in a way that matches the application's requirements and workflow.

- Event-Driven Efficiency: The framework seamlessly integrates tasks into event-driven programming paradigms. Tasks can be event-driven, making their execution more efficient and flexible.

- Streamlined Workflows: With support for tasks with sequential steps, the library facilitates the creation of streamlined workflows. This enables developers to break down complex processes into manageable and sequential units.

- Efficient Event Management: Beyond tasks, the library includes classes for managing complex event structures. This allows for the creation of event-driven tasks, improving overall application efficiency.

## Future Additions
- More examples
- More types of tasks
- Better Task and Event management
- Community Requests (your input is always important)

# How To Install
There are multiple ways to use this library,
but it was primarily made for Maven/Gradle so that will be the most up to date.
There will also be releases on GitHub,
but I would recommend using Maven or gradle as FTC projects already use them meaning you only need to add a few lines.

## To Install with Gradle
1. Go to the TeamCode folder in your project and open build.gradle
2. Add maven central to the repositories section (it should be in there by default)
    ```
    repositories {
        mavenCentral()
    }
    ```
3. Add the library to the dependencies section:
    ```
    dependencies {
        implementation 'io.github.omzz15:task:4.3.0-FTCRELEASE'
    }
    ```
4. Enjoy :)

## To Install with Maven:
**FTC does not use maven, so follow the installation with gradle instructions if you are using FTC**
1. In your project, make sure you can get libraries from Maven Central (This should be automatically available in maven projects)
2. Add the library to the project (by default, it should be in the dependencies section of pom.xml)
   ```
   <dependency>
      <groupId>io.github.omzz15</groupId>
      <artifactId>task</artifactId>
      <version>4.3.0-FTCRELEASE</version>
   </dependency>
   ```
3. Enjoy :)

# How To Use
Check examples [here](./src/test/java/examples)