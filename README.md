# PodcastFinder

**PodcastFinder** is a Java application for finding podcast feed URLs.

### Core concepts
**PodcastFinder** searches online for podcast feeds from a variety of sources. Podcast feeds are saved to a local SQLite database file named `podcastfinder.sqlite`, placed in the current user's home folder.  

### Commmand line usage

```
Usage: Podcastfinder [-defgis] [--clean] [--dups] [--export=<export>]
                     [-c=<concurrency>]
      --clean                 Remove all but the newest error statuses and vacuum the database
      --dups                  Find and display possible duplicate feeds
      --export=<export>       Export a list of all valid feeds with at least one episode
  -c, --concurrency=<concurrency>
                              Maximum number of concurrent downloads
  -d, --digitalpodcast        Search podcasts on digitalpodcast.com
  -e, --enrich                Retrieve podcast feeds to add additional information
  -f                          Search podcasts on iTunes using the feed generator API
  -g, --gpodder               Search podcasts on gpodder.net
  -i                          Search podcasts on iTunes using the search API
  -s, --status                Show status information about the data held by this application
```

A quick way to get started would be:

```
java -jar podcastfinder.jar -dfgs
```

This will create a new sqlite database in your home directory, and add podcasts to it from gpodder, digitalpodcast and iTunes.

### Embedded API usage

Take a look at packages `be.ceau.podcastfinder.action.api` and  `be.ceau.podcastfinder.action.impl`. For examples of how these methods can be called, look at `be.ceau.podcastfinder.cl.PodcastfinderCl`.

### Download JAR

Download a compiled jar with dependencies from the [releases page](https://github.com/mdewilde/podcastfinder/releases).

### GnuPG public key
Verify signature files with my [GnuPG public key](https://www.ceau.be/pubkey.gpg).

### License
Licensed under [the Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.txt).