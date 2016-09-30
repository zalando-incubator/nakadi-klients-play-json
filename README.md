## Nakadi Klients Play Json Support

Provides [play-json](https://www.playframework.com/documentation/2.5.x/ScalaJson) support 
for the various [nakadi-klients](https://github.com/zalando-incubator/nakadi-klients) models.

### Build

```sbt
sbt compile
```

### Install

Add the following to your `build.sbt` when using SBT.

```sbt
libraryDependencies += "org.zalando.nakadi.client" %% "nakadi-klients-play-json" % "0.1.0"
```

### Usage

Implicit play-json formats are can be imported by doing
```scala
import org.zalando.nakadi.client.scala.PlayJsonImplicits._
```

Note that the play-json formats are recursive, so you also need to provide
a `Format`/`Read`/`Writes` for the base `Event` type you are dealing with.

This library also provides a generic serialization method which can be
passed as a parameter to the nakadi `client.publishEvents(..)` call, i.e.

```scala
import org.zalando.nakadi.client.scala.model._
import org.zalando.nakadi.client.scala.{Client, ClientError}
import org.zalando.nakadi.client.utils.ClientBuilder
import org.zalando.nakadi.client.scala.NakadiSerializer

case class MyEvent(message: String)

val events = Seq(MyEvent("Dino was born, he says rawr"))

val dataChangeEvents = events.map { event =>
  DataChangeEvent(event, "my_model", DataOperation.CREATE, None)
}

val client: Client = ClientBuilder()
  .withHost("some_host")
  .withSecuredConnection(true)
  .withVerifiedSslCertificate(false)
  .withTokenProvider(Option(() => "some_token"))
  .build()

client.publishEvents("my_event_name",dataChangeEvents,NakadiSerializer.seqDataChangeEventSerializer[MyEvent])
```

Note that in this example, you have to provide an implicit `Writes` for `MyEvent`


Please make sure that you format the code using `scalafmt`. You can do this by running `scalafmt` in sbt before committing.
See [scalafmt](https://olafurpg.github.io/scalafmt/) for more info.

### License

Copyright © 2016 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.