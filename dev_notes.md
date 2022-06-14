* Issue: if gravity = 0, infinite loop
    * Will only matter when I make gravity configurable

* Evaluate my delta time strategy (based on the blog I read)
* Use TexturePacker
* Go through TODOs - there are a lot of things which I want to keep as reference code which needs to get cleaned up for future use
* RU localization & learn about string internationalization

# === Implementation Notes ===

Git Branching
* master - commit only on new release/hotfix
* dev - commit new work directly here or from feature branch if it's on the bigger side
* feature/#### - branch from dev, name after a task number
* release/#.# - branch from dev when features ready, name after major version
* hotfix/#### - branch from master, name after a Github issue, increment minor version

Patch Notes format
* Features
* Enhancements
* Bugfixes

Release process
1. Create release branch
2. Run test suite
3. Bundle release distributable
4. Verify changelog list
5. Playtest
6. Fix on release branch
7. Update version number in build.gradle
8. Merge into dev and master
9. Bundle distributable from master & publish
