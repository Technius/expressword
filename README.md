# ExpressWord

ExpressWord is an open-source vocabulary database website with functions that
assist the studying of vocabulary. The ExpressWord server is written in Scala,
using Akka and Akka HTTP. The client uses Angular.js as a framework.

# Usage

First, configure the ExpressWord server by editing `application.conf` and
`private.conf` located under `src/main/resources`.

The following commands can be run in `sbt` or `activator`:
* `run`: Starts the server. Not good for development as there is currently no
way of shutting it down without killing SBT.
* `reStart`: Starts the server as a background process. Use `~reStart` to have
the server automatically reload on a code change.
* `reStop`: Stops the server when it is running as a background process.
* `activator universal:packageBin`: Generates a `.zip` distribution.
* `activator docker:publishLocal`: Generates and locally publishes a Docker
image.

# Code Conventions

* The maximum line length for Scala and JavaScript are 80 characters per line.
* Use two spaces instead of tabs.
* Generally follow the Scala style guide for Scala and the Google JavaScript
  Style Guide for JavaScript, unless there is a stylistically better
  alternative.

# License

ExpressWord is licensed under the Apache 2.0 License. See LICENSE for more details.
