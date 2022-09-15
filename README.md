<p align="center">
  <img width="30%" height="30%" src="https://4750167.fs1.hubspotusercontent-na1.net/hubfs/4750167/Sidekick%20OS%20repo/logo-1.png">
</p>
<p align="center">
  Sidekick Java Agent
</p>

<p align="center">
    <a href="https://github.com/runsidekick/sidekick" target="_blank"><img src="https://img.shields.io/github/license/runsidekick/sidekick?style=for-the-badge" alt="Sidekick Licence" /></a>&nbsp;
    <a href="https://www.runsidekick.com/discord-invitation?utm_source=sidekick-java-readme" target="_blank"><img src="https://img.shields.io/discord/958745045308174416?style=for-the-badge&logo=discord&label=DISCORD" alt="Sidekick Discord Channel" /></a>&nbsp;
    <a href="https://www.runforesight.com?utm_source=sidekick-java-readme" target="_blank"><img src="https://img.shields.io/badge/Monitored%20by-Foresight-%239900F0?style=for-the-badge" alt="Foresight monitoring" /></a>&nbsp;
    <a href="https://app.runsidekick.com/sandbox?utm_source=sidekick-java-readme" target="_blank"><img src="https://img.shields.io/badge/try%20in-sandbox-brightgreen?style=for-the-badge" alt="Sidekick Sandbox" /></a>&nbsp;
    
</p>

<a name="readme-top"></a>

<div align="center">
    <a href="https://github.com/runsidekick/sidekick"><strong>Sidekick Main Repository »</strong></a>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#what-is-sidekick">What is Sidekick?</a>
      <ul>
        <li><a href="#sidekick-actions">Sidekick Actions</a></li>
      </ul>
    </li>
    <li>
      <a href="#sidekick-java-agent">Sidekick Java Agent</a>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#supported-jvms-and-languages">Supported JVMs and Languages</a></li>
      </ul>
      <ul>
        <li><a href="#download-the-agent">Download the Agent</a></li>
      </ul>
    </li>
    <li>
      <a href="#build">Build the agent</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
      </ul>
      <ul>
        <li><a href="#build-command">Build command</a></li>
      </ul>
    </li>
    <li>
      <a href="#official-sidekick-agents">Official Sidekick Agents</a>
    </li>
    <li>
      <a href="#resources">Resources</a>
    </li>
    <li><a href="#questions-problems-suggestions">Questions? Problems? Suggestions?</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## What is Sidekick?
Sidekick is a live application debugger that lets you troubleshoot your applications while they keep on running.

Add dynamic logs and put non-breaking breakpoints in your running application without the need of stopping & redeploying.

Sidekick Open Source is here to allow self-hosting and make live debugging more accessible. Built for everyone who needs extra information from their running applications. 
<p align="center">
  <img width="70%" height="70%" src="https://4750167.fs1.hubspotusercontent-na1.net/hubfs/4750167/Sidekick%20OS%20repo/HowSidekickWorks.gif">
</p>


##### Sidekick Actions:
Sidekick has two major actions; Tracepoints & Logpoints.

- A **tracepoint** is a non-breaking remote breakpoint. In short, it takes a snapshot of the variables when the code hits that line.
- **Logpoints** open the way for dynamic(on-demand) logging to Sidekick users. Replacing traditional logging with dynamic logging has the potential to lower stage sizes, costs, and time for log searching while adding the ability to add new logpoints without editing the source code, redeploying, or restarting the application.

Supported runtimes: Java, Python, Node.js

To learn more about Sidekick features and capabilities, see our [web page.](https://www.runsidekick.com/?utm_source=sidekick-java-readme)

<p align="center">
  <a href="https://app.runsidekick.com/sandbox?utm_source=github&utm_medium=readme" target="_blank"><img width="345" height="66" src="https://4750167.fs1.hubspotusercontent-na1.net/hubfs/4750167/Sidekick%20OS%20repo/try(1)%201.png"></a>
</p>

<p align="center">
  <a href="https://www.runsidekick.com/discord-invitation?utm_source=sidekick-java-readme" target="_blank"><img width="40%" height="40%" src="https://4750167.fs1.hubspotusercontent-na1.net/hubfs/4750167/Sidekick%20OS%20repo/joindiscord.png"></a>
</p>
<div align="center">
    <a href="https://www.runsidekick.com/?utm_source=sidekick-java-readme"><strong>Learn More »</strong></a>
</div>
<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Sidekick Java Agent

Sidekick Java agent allows you inject trace points (non-breaking breakpoints) and log points dynamically to capture call stack snapshots (with variables) and add log messages on the fly without code modification, re-build and re-deploy. So it helps you, your team and organization to reduce MTTR (Minimum Time to Repair/Resolve).

To achieve this, Sidekick Java agent has nothing to do with JDWP (Java Debug Wire Protocol), as it doesn't suspend the code execution, but hooks into code execution at application layer by bytecode instrumentation. Under the hood, Sidekick agent
- injects its hook call into the specified line at bytecode level 
- intercepts the code execution just before the specified line
- captures callstack snapshot (for trace point) or prints dynamic log message (for log point) 
- creates event to be sent asynchronously
- and then lets the code execution continue

Here, to keep Sidekick agent overhead at mimimum (sub-millisecond on average), in addition to non-blocking event publishing, we apply many performance improvements like async snapshot taking, async call stack collecting and fast serialization by reducing redundant memory copies.

The advantages of Sidekick over classical APM solutions is that, Sidekick 
- can debug and trace any location (your code base or 3rd party dependency) in your application, not just the external (DB, API, etc ...) calls like APM solutions
- has zero overhead when you don't have any trace point or log point but APMs have always
- doesn't produce too much garbage data because it collects data only at the certain points you specified as long as that point (trace point/log point) is active

#### Benchmarks
- [Production Debuggers — 2022 Benchmark Results](https://medium.com/runsidekick/sidekick-blog-production-debuggers-2022-benchmark-results-part-1-ec173d0f8ccd)

### Usage
##### Supported JVMs and Languages
JDK 8+ is supported
Java (8+), Kotlin (1.3+) and Scala (2.10+) JVM languages are supported

##### Download the agent
Download the latest Sidekick agent from this link [repo](https://repo.thundra.io/service/local/artifact/maven/redirect?r=sidekick-releases&g=com.runsidekick.agent&a=sidekick-agent-bootstrap&v=LATEST)

Agent configuration : https://docs.runsidekick.com/installation/installing-agents/java/configuration#configure-the-agent

Source bundling: https://docs.runsidekick.com/installation/installing-agents/java/source-bundling

### Build

##### Prerequisites 
- Java 8+
- Maven 3.x

##### Build command:
```mvn clean package```


##  Official Sidekick Agents

- [Java](https://github.com/runsidekick/sidekick-agent-java)
- [Node.js](https://github.com/runsidekick/sidekick-agent-nodejs)
- [Python](https://github.com/runsidekick/sidekick-agent-python)

## Resources:

- [Documentation](https://docs.runsidekick.com/?utm_source=sidekick-java-readme)
- [Community](https://github.com/runsidekick/sidekick/discussions)
- [Discord](https://www.runsidekick.com/discord-invitation?utm_source=sidekick-java-readme)
- [Contributing](https://github.com/runsidekick/sidekick/blob/master/CONTRIBUTING.md)
- [Sidekick Main Repository](https://github.com/runsidekick/sidekick)

## Questions? Problems? Suggestions?

To report a bug or request a feature, create a [GitHub Issue](https://github.com/runsidekick/sidekick-agent-java/issues). Please ensure someone else has not created an issue for the same topic.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

[Reach out on the Discord](https://www.runsidekick.com/discord-invitation?utm_source=sidekick-java-readme). A fellow community member or Sidekick engineer will be happy to help you out.

<p align="right">(<a href="#readme-top">back to top</a>)</p>
