# Does not work anymore

# immo24scraper

A web scraper for immobilenscout24 that will send you an email everytime a new offer (matching your search cirteria) goes online 

> Why should I use this? Immobilenscout24 already has a notification feature.

However, the notification feature appeared to be unreliable, so I came up with this DIY solution.

> Where should I run this?

e.g. raspberry pi, heroku dyno or any pc running 24/7

## Usage
Note: 
* Works only with gmail atm. Other providers will need some tweaks [see](https://github.com/drewr/postal)
* JVM is required (only tested with java 11 and 14)

1) Clone repository
2) Allow [less secure apps](https://myaccount.google.com/intro/security) and create a password for this app
3) Add credentials under `resources/credentials.edn` you may want to unstage the file with `git reset resources/credentials.edn`
4) Go to [immobilenscout24](https://www.immobilienscout24.de/) enter your search cirteria, press search copy the url
4) Start with `java -jar immo24scraper.jar "copiedUrlhere" &` put the url into quotation marks, the `&` puts the process into background on unix systems this may not work with windows.


## License

Copyright © 2020 lhrb

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
