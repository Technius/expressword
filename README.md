# ExpressWord

ExpressWord is an open-source vocabulary database website with functions that
assist the studying of vocabulary. The ExpressWord server is written in Scala,
using Akka and Akka HTTP. The client uses Angular.js as a framework.

# Usage

First, configure the ExpressWord server by editing `application.conf` and
`private.conf` located under `src/main/resources`.

To start the ExpressWord server, use `activator reStart` in a terminal. If the
server was started from an SBT console, the server can be stopped using
`reStop`.

A `.zip` file can be generated using `activator universal:packageBin` and a
Docker image can be generated with `activator docker:publishLocal`.

# Code Conventions

* The maximum line length for Scala and JavaScript are 80 characters per line.
* Use two spaces instead of tabs.
* Generally follow the Scala style guide for Scala and the Google JavaScript
  Style Guide for JavaScript, unless there is a stylistically better
  alternative.

# License

ExpressWord is licensed under the Apache 2.0 License. See LICENSE for more details.
