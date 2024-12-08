# Advent of Code 2024

This is my repo for the [Advent of Code](https://adventofcode.com/) 2024. I'm expecting a busier December this year than I'm used to, so instead of trying out another new language, I'm heading back to my old friend Java for this year. I'm also returning to [Eclipse](https://eclipseide.org/) for my IDE, but new this year is fiddling with [EGit](https://eclipse.dev/egit/) to simplify my pipeline. [Update: EGit so far seems quirky in some way that I don't understand that does **not** appear to be simplifying my pipeline. ;) ] \[Further update: I figured out what was causing the quirkiness, so I guess the workflow is fine now, though I don't fully understand why it wasn't to begin with. But in a nutshell, I have my Eclipse project and my Git working tree set up separately, and somehow that means EGit doesn't always automatically detect changes in source files that need to get added to the index. Whatever.\]

As before, this README serves as kind of blog and running commentary that I'll be updating as the month progresses.

Shoutouts this year to (1) once again, the fun folks at the [Indie Game Reading Club](https://www.indiegamereadingclub.com/) for their company again and (2) my friend [@codeforkjeff](https://github.com/codeforkjeff) for encouraging me to shake off the coding dust and get back in it.

# What to do with this code
The current state of affairs (as of Day 1):

Basically, the src folder should be able to be dropped anywhere and compiled with any modern Java compiler. There's nothing too complicated going on here. What I'm **not** doing is checking in any puzzle input. To try out your own input, drop a `resources` directory in your classpath, a `puzzle-input` directory inside that, and then a file with the name format `dayNN-input.txt` where `NN` is a left-zero-padded day number. For "test" input (optionally callable instead of "real" input with a command line argument, instead use the file name format `dayNN-input-test.txt`.

The main entry point is com.robabrazado.aoc2024.Aoc2024Cli, and it takes 2 or 3 command line arguments:

`java com.robabrazado.aoc2024.Aoc2024Cli <day> <part> [test]`

Where `day` is the number of the day to solve, `part` is `1` or `2`, and the optional `test` argument makes it use test data input or not as described above.


---

## Day 1: Historian Hysteria

I wrote the main implementation for this right after the puzzle dropped, but then spent a little time trying to get some common infrastructure in place, and then I did a little refactoring in the solution to take advantage. Other than the infrastructure stuff, which was me making things more complicated for myself, the actual solution implementation was pretty straightforward. One thing I'm trying to do more of this year is process puzzle input in a more "as it comes in from the stream" kind of way, rather than loading the whole input into memory and then parsing from there. I don't think there's significant impact for that change...it's just one of those things I decided to try. The effect on this day is that it (of course) made things more complicated for me since the processing-while-parsing operations were more complicated for part 2 than part 1. For the first implementation, I ended up just duplicating a bunch of code, but after the refactoring, I just basically made part 1 processing live with the extra work it has to do for part 2. So it goes.

I may try and tinker with the common-use stuff as the month goes on, but we'll see.

## Day 2: Red-Nosed Reports

Just by way of "I solved the puzzle but I'm not happy about it," I am *sure* there's a better way to approach part 2 than what I did, but my first attempt at a smarter solution turned out to be wrong, so after some fruitless debugging, I went the long way around the barn just so I could get to an answer. I know this "oh well just brute force it" technique is not going to serve me well going deeper into the month, but this was one of those ones I thought I could knock out quickly before I went to bed, and then it turned out...not. I'm hoping to revise this one soon. But if not, I guess it's no biggie. I'll just note for the record that I'm not happy with my solution for part 2, and I'm sure there's a better way. I also stuck to my guns as far as my initial desire to *only* process and store what I needed to in order to get the job done, but I think in doing that for part 1, I might have made my life harder for part 2. So I dunno...I may loosen that guideline going forward.

I will note, though...at least I remembered to commit between parts 1 and 2 this time. Not that it mattered that much, it turns out, but it'll matter a lot in the future, I'm sure, plus it'll *really* matter this time if I end up revising my part 2 solution.

**Spoilers for Day 2 Part 2**

My initial attempt at implementing the Problem Dampener was to build it right into the Report object. Basically, as I was processing for "safeness" in the constructor, I gave the processing a free do-over the first time it encountered a fault, so that only the second fault would mark the Report as unsafe. It passed the test data but not the full input, and after staring at it for a while, I came to realize that what I implemented would never catch the fault as being caused by the first level being the "bad" one. So my *first* attempt at a cheap hack was actually that the Day02Solver would, after getting an "unsafe" Report with the dampener toggle on, then try making a non-dampened report with the same inputs minus the first level. That at least produced a better result than my first attempt, but it was still incorrect. So from there I just went to the crappy brute force you see before you (at the time of this writing). Obviously there were further edge cases that even my cheap hack of my first attempt were still missing, so I went to the brute force solution so I could log a correct answer before I went to bed. I'm convinced there's a better way, though.

**End spoilers**

## Day 3: Mull It Over

What a (relative) disaster this was! I got part 1 quickly when the puzzle dropped, and I *thought* I got part 2 quickly, too, but I ended up in that (uncomfortable) state where I was passing the test data but not the live data. After not too much time, I gave up and went to bed. I spent a bunch of time in the afternoon trying to figure out what I was doing wrong. I rewrote the damn thing like four times ~~(the saga is preserved in my source; I left the old attempts as commented code blocks)~~, and I just could not figure out what was wrong. All I knew was that the issue was in the design, because I implemented basically the same algorithm in a bunch of different ways and would get consistent but incorrect results. Finally I looked at Jeff's solution, which was already done. At first, I *still* couldn't figure out what was wrong, but I eventually got there. It turned out I was misinterpreting the ask. Ah, well.

[Day 7 update] In doing a refactoring of my entire project code on Day 7, I removed the commented previous code form the Day 3 source, because it made
even *less* sense in light of the refactoring. Previous attempts are still present in the commit history for those *really* intent on reviewing my mistakes.

**Spoilers for Day 2 Part 2**

What first struck me about looking at Jeff's code was that our algorithms were almost identical! Which I thought was cool, even though it took me four tries to get where he was. :) But I was happy with my latest implementation even before I looked at his code. (As an aside, he's also doing much cooler stuff than I am with regard to loading different solvers per day; I knew to be fancy I should be using dynamic class loading, but I just didn't do it. Now I kinda wish I did. And I still can; we'll see how much extra time I can free up.) [Update: I refactored to start doing this as of Day 7.] Anyway, what ended up being the difference was that I misinterpreted part of the puzzle. The spec said that at the beginning of the "program," the state started out enabled. What I was doing was treating each *line* of text as a separate program, rather than just taking the entire input as a single program. So basically I was resetting the enabled flag to `true` every once in a while when I thought it was warranted, but the puzzle didn't. That's the kind of thing I probably would have never come to on my own; I really think I needed to compare to another solution in order to figure it out. Because his code and my code worked so similarly, it was easy for me to squeeze out some easily comparable debug output to see what we were doing differently. I'm not sure I would have ever noticed the difference in our enable state management just by looking at the code. I don't know what that says about me, but probably nothing good.

** End spoilers **

## Day 4: Ceres Search

[On day 5] Yeah, I kinda bungled this one. In trying to do this on actual day 4, I got caught in that trap I sometimes get caught in where I was totally overengineering things, trying to write general-purposes tools and stuff that...might or might not come in handy later in the month, but definitely complicated the present day and slowed me down. Halfway down that road, I realized I wasn't doing myself any favors, so I figured I'd come back to it later, but I never found the time on day 4. By the time the day 5 puzzle dropped, I thought that would be easier, so I worked on that. I'm writing this update after I finished day 5, so I'm still planning on coming back to day 4.

## Day 5: Print Queue

I got part 1 relatively quickly, but then had to step away before getting sucked into part 2. When I got around to starting part 2, I had a neato idea that I thought would be pretty fun, and it was, but it was flawed in some way, because it passed test data but not real data. Rather than try and fix it, since it was already unnecessarily complicated, I just did a full rewrite with a way simpler algorithm. The solution I ended up with was *way* easier to implement, but probably isn't going to win any performance or elegance awards. I can also envision plenty of input conditions that would send me into an infinite loop, so it's basically the definition of quick and dirty. It works...but only for now. But! That's how it goes around here sometimes. I did a commit after I realized my first attempt failed, so it's in there for posterity. I dunno...I give myself a D for this one. Passing grade, but barely. :)

As an aside...I really need a better system for compartmentalizing the different days' solutions. I am *certain* I'm committing unbuildable code, so...that sucks. Maybe I'll try and straighten that stuff up this weekend (day 7 or 8).

[Day 7 update] Updated this to not use copy-pasted code between parts 1 and 2.

## Day 6: Guard Gallivant

Upon initially reading this puzzle, I realize that all that overengineering I gave up on for Day 4 would actually come in handy now, so I should finish Day 4 before going on to Day 6. Then I never went back to either 4 *or* 6.

## Day 7: Bridge Repair

This won't be a full report. I've logged answers for Day 7 as far as the puzzle is concerned, but two things remain before I finalize. One, I know that my solution is (a) terrible, and (b) badly managed, because I can't switch between solving between part 1 and part 2. ~~Two, I'm on the verge of just totally refactoring everything in the project, so (at time of check-in), the Day 7 Solver runs independently of the main entry point and isn't accessible from there (you have to run `Day07Solver.main` directly). So...I would like to rewrite the main entry point code, and~~ I also assume I'll be rewriting Day 7 altogether, because the solution is not great.

[Later on Day 7] **The Day 7 Refactor** I finally sat down and refactored *everything* so that the main entry point now takes advantage of dynamic class loading. This will (hopefully, anyway) result in me much less often just checking in a totally broken build. The other big change for this refactor is that the solvers now take a `Stream<String>` input. More specific to Day 7, I also rewrote Day 7 a bit, but only enough to fit into the refactored framework and to separate parts 1 and 2. That said, I *did* move all the code into a new object. I still haven't rewritten the algorithm, though, so part 2 still runs like shit.

## Day 8: Resonant Collinearity

I took a look at the puzzle before I went to bed, because I had *some* time I could work on it in the morning, but not a lot of time. So I had some time to mull it over before actually coding, and I thought I could at least get part 1 in before I headed out. It turned out part 2 wasn't totally bizarre relative to what I did for part 1, so I ended up being able to do both parts before the rest of my day got underway. So hooray for that! I'm also liking the new structure under yesterday's refactor, though I noticed something else I should refactor, so that'll go on my TODO list, too.

** Spoilers (mild) for Day 8 **

When I first looked at today's puzzle and saw it was more grid stuff, I figured this would, like Day 6, have to wait until I did Day 4. But having the time to think about it before going to bed, I realized I could basically handle it mathematically without having to actually use a grid structure to do it. (Honestly, probably a lot of the grid problems could be done that way, but it's just so much easier to have a grid object around to maintain state. Or...at least I *think* it is. Maybe I should revisit the previous grid problems with the idea in mind that I may not need a grid!) Anyway, once I warmed up to the idea that I wouldn't need an actual matrix, at least for part 1, I decided to give it a shot, and it turned out to work all right. I was a little worried that part 2 would mean I'd need a grid anyway, and I was fully prepared to delay part 2 until I had a grid setup ready, but it turned out I could do both with just math, so...yay for that. Also...there may be some built-in Java way to handle GCF stuff...I forgot to look.

** End spoilers **
