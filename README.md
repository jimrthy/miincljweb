FIXME: Need to specifically switch to Stuart Sierra's
Workflow Revisited. That's basically the entire
justification for this project over luminus.

Should also add lib-noir. Seems only reasonable.


miincljweb
==========

Clojure web stack, with the bare-bones batteries
that seem useful to me.

It combines
* http-kit for actually processing the requests
* compojure for routing
* clostache for templating
* korma for the data layer
* sqlite under the hood

Actually, most of those batteries will most likely
be going away. The useful part that this performs
is running multiple servers on multiple ports inside
the same JVM.

This should help conserve memory if you're running
a bunch of miniscule sites behind, say, nginx.

Usage
=====

For now:
    lein install

Then, in your project, add a dependency to 
    [jimrthy/miincljweb "0.1.0-SNAPSHOT"]

In whatever you want to run the server from, add a
    [miincljweb.system] 
to your ns declaration.

Start with the default map returned from
    (miincljweb.system/init)

Then set up definitions for your sites. These should
be a seq of maps. See config.clj for examples (you
really just need :domain, :port, and :router entries
for each).

reset! the system map's atom to be that sequence.

Then call
    (miincljweb.system/start updated-map)

You can call
    (miincljweb.system/stop updated-map)
to stop all your sites if you need to, for example,
update the routing.

Restarting individual sites is fairly high on my
priority list.

