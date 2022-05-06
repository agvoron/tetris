# === 1.2 ===
* Finish gameplay
    * Keys repeat if held
    * Rotations
    * QOL

# === 1.3 ===
* Issue: if gravity = 0, infinite loop
* Settings screen - configurables and tidy up

# === 1.4 ===
* Snazzy particle visuals
* Sound effects

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
1. Create feature branch
2. Run test suite
3. Bundle distributable
4. Verify changelog list
5. Playtest