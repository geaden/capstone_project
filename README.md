# Capstone Project

[Trello Board](https://trello.com/b/BPasjtiY)

Capstone project for Udacity Android Nanodegree.

App allows to read top stories from https://news.ycombinator.com/ and store content locally. One can also bookmark a story with cloud sync (i.e., if you change device, your bookmarks will be restored from cloud server).

Content is not always ideally extracted from the urls, but sometimes it's more helpfule then opening article in the browser any time.

## Running tests

```bash
gradle clean checkAll -PdisablePreDex
```

Make sure emulator is running.

## Links

- [Google Services Quickstart](https://github.com/googlesamples/google-services)
- [Example app to use Android application and GAE Endpoints](https://github.com/udacity/conference-central-android-app)

