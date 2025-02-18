# Advent of Code 2024

This is my repo for the [Advent of Code](https://adventofcode.com/) 2024. I'm expecting a busier December this year than I'm used to, so instead of trying out another new language, I'm heading back to my old friend Java for this year. I'm also returning to [Eclipse](https://eclipseide.org/) for my IDE, but new this year is fiddling with [EGit](https://eclipse.dev/egit/) to simplify my pipeline. [Update: EGit so far seems quirky in some way that I don't understand that does **not** appear to be simplifying my pipeline. ;) ] \[Further update: I figured out what was causing the quirkiness, so I guess the workflow is fine now, though I don't fully understand why it wasn't to begin with. But in a nutshell, I have my Eclipse project and my Git working tree set up separately, and somehow that means EGit doesn't always automatically detect changes in source files that need to get added to the index. Whatever.\]

As before, this README serves as kind of blog and running commentary that I'll be updating as the month progresses.

Shoutouts this year to (1) once again, the fun folks at the [Indie Game Reading Club](https://www.indiegamereadingclub.com/) for their company again and (2) my friend [@codeforkjeff](https://github.com/codeforkjeff) for encouraging me to shake off the coding dust and get back in it.

# What to do with this code
Basically, the src folder should be able to be dropped anywhere and compiled with any modern Java compiler. ~There's nothing too complicated going on here.~ [As the year went on, things of course got more complicated. But even so, I don't think there's any bleeding edge Java happening here.] What I'm **not** doing is checking in any puzzle input. To try out your own input, drop a `resources` directory in your classpath, a `puzzle-input` directory inside that, and then a file with the name format `dayNN-input.txt` where `NN` is a left-zero-padded day number. For "test" input (optionally callable instead of "real" input with a command line argument, instead use the file name format `dayNN-input-test.txt`.

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

**Spoilers for Day 3 Part 2**

What first struck me about looking at Jeff's code was that our algorithms were almost identical! Which I thought was cool, even though it took me four tries to get where he was. :) But I was happy with my latest implementation even before I looked at his code. (As an aside, he's also doing much cooler stuff than I am with regard to loading different solvers per day; I knew to be fancy I should be using dynamic class loading, but I just didn't do it. Now I kinda wish I did. And I still can; we'll see how much extra time I can free up.) [Update: I refactored to start doing this as of Day 7.] Anyway, what ended up being the difference was that I misinterpreted part of the puzzle. The spec said that at the beginning of the "program," the state started out enabled. What I was doing was treating each *line* of text as a separate program, rather than just taking the entire input as a single program. So basically I was resetting the enabled flag to `true` every once in a while when I thought it was warranted, but the puzzle didn't. That's the kind of thing I probably would have never come to on my own; I really think I needed to compare to another solution in order to figure it out. Because his code and my code worked so similarly, it was easy for me to squeeze out some easily comparable debug output to see what we were doing differently. I'm not sure I would have ever noticed the difference in our enable state management just by looking at the code. I don't know what that says about me, but probably nothing good.

**End spoilers**

## Day 4: Ceres Search

[On day 5] Yeah, I kinda bungled this one. In trying to do this on actual day 4, I got caught in that trap I sometimes get caught in where I was totally overengineering things, trying to write general-purposes tools and stuff that...might or might not come in handy later in the month, but definitely complicated the present day and slowed me down. Halfway down that road, I realized I wasn't doing myself any favors, so I figured I'd come back to it later, but I never found the time on day 4. By the time the day 5 puzzle dropped, I thought that would be easier, so I worked on that. I'm writing this update after I finished day 5, so I'm still planning on coming back to day 4.

[On day 12] After some time away, I spent a little time coming back to fill in the gaps of puzzles I had abandoned earlier in the month. I did enough to get part 1, but I probably won't be able to come back for part 2 today. I don't think it's too spoilery to say: my original plan for this puzzle (on the day of) was to write a general-use 2D array container. That was the part I ended up abandoning, but some of the pieces remained and came in handy for later puzzles. For this attempt at Day 4, I still didn't try to revive the general use container, but I did make use of some of the other groundwork I had laid for this puzzle before (and used in subsequent puzzles), so that was nice. In looking at part 2 of the puzzle, I'm not sure what I did for part 1 is going to help me, but I'm out of time at the moment, so will have to come back to it later.

Also! I should say I found a huge bug in the original implementation of my Coords object. At this late date, I can't even remember why I decided to implement what I did, but suffice it to say...I did it wrong. So that's been corrected with Day 4 Part 1, too.

[On day 13] Part 2 not so bad. What I did probably isn't the best-performing solution I could have done, but it was fast to implement since I did end up making use of the part 1 functionality.

## Day 5: Print Queue

I got part 1 relatively quickly, but then had to step away before getting sucked into part 2. When I got around to starting part 2, I had a neato idea that I thought would be pretty fun, and it was, but it was flawed in some way, because it passed test data but not real data. Rather than try and fix it, since it was already unnecessarily complicated, I just did a full rewrite with a way simpler algorithm. The solution I ended up with was *way* easier to implement, but probably isn't going to win any performance or elegance awards. I can also envision plenty of input conditions that would send me into an infinite loop, so it's basically the definition of quick and dirty. It works...but only for now. But! That's how it goes around here sometimes. I did a commit after I realized my first attempt failed, so it's in there for posterity. I dunno...I give myself a D for this one. Passing grade, but barely. :)

As an aside...I really need a better system for compartmentalizing the different days' solutions. I am *certain* I'm committing unbuildable code, so...that sucks. Maybe I'll try and straighten that stuff up this weekend (day 7 or 8).

[Day 7 update] Updated this to not use copy-pasted code between parts 1 and 2.

## Day 6: Guard Gallivant

Upon initially reading this puzzle, I realize that all that overengineering I gave up on for Day 4 would actually come in handy now, so I should finish Day 4 before going on to Day 6. Then I never went back to either 4 *or* 6.

[On day 15] So, first, turns out I didn't need a grid object for this (not really), so I could have just done this without Day 4. Not that it ended up mattering; I guess I didn't have time for either one. In any case, part 1 went pretty well, though feel like there's more elegant way to, like...look ahead multiple steps and not just move one step at a time. This notion is somewhat reflected in the code; there's a method for taking multiple steps that I ended up never using. Part 2...oof. I went with an ugly brute force search, which I'm pretty sure was not the intended solution, if for no other reason than it took a while to run (like 3.5 mins). But it was quick to code it from the skeleton of part 1, and although I knew it was going to be "slow" to execute, I figured it wouldn't be unbearably slow, so I just went with it. Not my proudest moment, not my best code, but it's done. I also tweaked enough functionality that I felt it necessary to re-test part 1 after the part 2 modifications, but it seems fine.

## Day 7: Bridge Repair

This won't be a full report. I've logged answers for Day 7 as far as the puzzle is concerned, but two things remain before I finalize. One, I know that my solution is (a) terrible, and (b) badly managed, because I can't switch between solving between part 1 and part 2. ~~Two, I'm on the verge of just totally refactoring everything in the project, so (at time of check-in), the Day 7 Solver runs independently of the main entry point and isn't accessible from there (you have to run `Day07Solver.main` directly). So...I would like to rewrite the main entry point code, and~~ I also assume I'll be rewriting Day 7 altogether, because the solution is not great.

## The Day 7 Refactor
[Later on Day 7] I finally sat down and refactored *everything* so that the main entry point now takes advantage of dynamic class loading. This will (hopefully, anyway) result in me much less often just checking in a totally broken build. The other big change for this refactor is that the solvers now take a `Stream<String>` input. More specific to Day 7, I also rewrote Day 7 a bit, but only enough to fit into the refactored framework and to separate parts 1 and 2. That said, I *did* move all the code into a new object. I still haven't rewritten the algorithm, though, so part 2 still runs like shit.

## Day 8: Resonant Collinearity

I took a look at the puzzle before I went to bed, because I had *some* time I could work on it in the morning, but not a lot of time. So I had some time to mull it over before actually coding, and I thought I could at least get part 1 in before I headed out. It turned out part 2 wasn't totally bizarre relative to what I did for part 1, so I ended up being able to do both parts before the rest of my day got underway. So hooray for that! I'm also liking the new structure under yesterday's refactor, though I noticed something else I should refactor, so that'll go on my TODO list, too.

**Spoilers (mild) for Day 8**

When I first looked at today's puzzle and saw it was more grid stuff, I figured this would, like Day 6, have to wait until I did Day 4. But having the time to think about it before going to bed, I realized I could basically handle it mathematically without having to actually use a grid structure to do it. (Honestly, probably a lot of the grid problems could be done that way, but it's just so much easier to have a grid object around to maintain state. Or...at least I *think* it is. Maybe I should revisit the previous grid problems with the idea in mind that I may not need a grid!) Anyway, once I warmed up to the idea that I wouldn't need an actual matrix, at least for part 1, I decided to give it a shot, and it turned out to work all right. I was a little worried that part 2 would mean I'd need a grid anyway, and I was fully prepared to delay part 2 until I had a grid setup ready, but it turned out I could do both with just math, so...yay for that. Also...there may be some built-in Java way to handle GCF stuff...I forgot to look.

**End spoilers**

## Day 9: Disk Fragmenter

I guess I felt unduly contrarian this puzzle, and my first stab at part 1 was unduly complicated, because I decided to forego the obvious data structure and try something different (more details in spoiler section). When I started getting myself into trouble with that, I almost abandoned ship, but I stuck with it, and I *think* it made my life easier for part 2, but I'm not sure.

Whatever the case, I'm also due for another overall refactor, so...that may or may not happen today, but I have to work on other stuff now. So I may or may not refactor today, but it's coming.

**Spoilers (mild) for Day 9**

Upon first reading of the puzzle, the obvious (and probably better) choice is to use an array to represent the disk blocks, but I got it in my head that it would end up being too big, so I decided to try something more like a linked list, instead. Actually a double-linked list, because I envisioned myself having to traverse in both directions from both ends. I know I could have used a Deque or even a List, probably, and saved myself a lot of implementation pain, but I dunno...I enjoyed the exercise, and for some reason I had it in my head to just not use anything backed by an array. Anyway...I ran into a little trouble implementing that (of course), and I *almost* gave it up and just went to using an array, instead, but I just decided to live with the sunk cost and just hope it was time well spent for part 2. In brief...I think the choice made most appealing by the puzzle presentation was just an array of disk blocks (so one array element for each block). I think the second choice was an array of objects that mirrored how the disk map was set up, so one element for each file or free space with an associated size. I pretty much went with the latter choice, except using a linked list instead of an array.

When part 2 came around, it felt like the right choice, since I had already baked in the concept of files as nodes instead of having to manage them at the block level. Even so, there's probably no reason I couldn't have done this with an array and just used indexes or something to do the traversals. But, whatever. It was fun, and it ended up working, even if it was a pain implementing the element inserts and shit like that that probably would have been easier just making the file/space object and using a List or so. So it goes.

**End spoilers**

## Day 10: Hoof it

[On day 15] All things considered, this one went pretty fast, but I took a *lot* of shortcuts. My inner class is...lazy, I'd call it. I have separate methods for handling part 1 and part 2, and part 2 is mostly just copy-pasted from part 1. I know in my gut that means suboptimal design, because I should be reusing code instead of duplicating it. Plus I know the loop structure is a complete mess, performance-wise. I mean, in practical terms, it runs fast enough, but in academic terms it's pretty inefficient. But, again...I'm playing a lot of catchup today, so I'm just going to live with it.

## Day 11: Plutonian Pebbles

[On day 18] So...at the time of this writing, I'm still in the midst of failing to do Day 16 (meaning 16-18 remain undone, plus I haven't done 11-13 yet, either), so I went back to catch up on some "easier" stuff. Part 1 was straightforward and I just went with a straight-up implementation of the algorithm as presented in the puzzle. Part 2 seemed like a simple change of an input parameter, but it was taking so long that I did a rewrite. Not of the algorithm, mind you, but of the underlying data structure, like a chump. Turned out the data structure wasn't the problem; it really WAS the algorithm that was taking so long, so eventually I just decided to let it run and come back later. I'm finding this year that, since I'm so short on time, I'm doing a lot more of throwing my hands in the air and going "oh, well!" even when I know I've coded a bad solution.

[Later] Welp. I let it run and was on my way out to do errands, but before I left, it ran out of memory. Back to the drawing board on this, I guess, but now I'm still going out for errands, so this will have to wait.

[On day 22] Okay, so...I'm pretty sure I got it to the point where it wouldn't run out of memory (on day 18) but would still take a hell of a lot of time to run. I'll admit, I just let it chug away for like two or three days, figuring I'd just live with the shame, but eventually I had to throw in the towel and just admit that I need a better algorithm. Trying out some other puzzles, and I'll come back to this one. At the time of this writing, I'm committing part 1 of day 12, but I'm still way, way behind.

[On day 28] Awright, I'm coming back to this one to try and work it out. Because it's been quite a while since I worked on this, and also due to the Great Git Mishap of Day 27, I'm going to just start this bad boy over again. That'll mean a whole new solution for part 1, so...first run will be in the commit history for the morbidly curious.

[Later] Well THAT wasn't such a chore, now was it?! I don't know why this caused me so much trouble before, except that I know I had a bunch of trouble figuring out the caching. Seems to work this time, though! Don't be fooled...I spent plenty of time staring at this thing without the caching and wondering if I was just botching the algorithm. Turns out I just needed to memoize.

## The Day 12 Refactor
Starting on day 10, I had decided to take a few days break from Advent of Code, because I had fallen so far behind in my other work. :) By day 12, I'm definitely making progress catching up (on my other work), and I had some extra time in the evening, so I just took care of the refactor I'd been meaning to do. The Day 7 Refactor took care of the dynamic class loading and the new way of consuming puzzle input that I wanted, but it originally enforced separate part 1 and part 2 solving methods on every puzzle, which wasn't always appropriate, plus it included a vestigial "test data" parameter being passed around that was unneeded. So this refactor corrects those issues.

## Day 12: Garden Groups
[On day 22] Just dashing off a note in here for the part 1 commit. Part 2 is sufficiently different that I already know I'll be doing some significant rewriting.

[A bit later] Well...I didn't to the rewrite. I just tacked on some extra and ugly code, and seems to do fine. Definitely not the cleanest solution, but it works and doesn't take days to run, so I'll take it.

## Day 13: Claw Contraption

[On day 22] I had initial thoughts of just searching through a virtual search space (like...each button press would be like a node, etc.), but at some point I realized I could just do this with math. I think that saved me a ton of time going into part 2. I _did_ however, have to rewrite everything with BigInteger instead of int. I maybe could have used long? But why take the chance.

## Day 14: Restroom Redoubt

I've skipped many puzzles at the time I'm working on 14, but for starters, this one made me (un)refactor a change I made in the Day 12 Refactor, notably that now the Solver _does_ need to know whether it's a test run or not, so I've put that parameter back in.

That aside, part 1 went relatively well and was fun to work on. Part 2 looks like it's going to be a royal pain.

[Later] What a pain part 2 was. That's all I have to say about that.

[On day 15] I sort of redid part 2, because it was bothering me, but even so, it's still not what I'd call a rigorous general solution. I left the old version in as commented code, though.

## Day 15: Warehouse Woes

Part 1 seemed straightforward enough that I was very suspicious of every assumption I made to implement it, because I couldn't even guess as to what the part 2 twist might be. The part 2 twist turned out to be _very_ twisty, so I basically had to recode everything. Granted, at this time of this writing, I've skipped a lot of the previous puzzles, but for me in this timeline, it's the first time I have two completely different objects to handle parts 1 and 2. (And for once, I didn't regret putting an enum as an inner class, because I had to redo it for part 2 anyway.) Still, it was a good puzzle day! I enjoyed both parts.

Also, there's some code style inconsistency with this day, because I only remembered like halfway through part 2 that I could use switch-case on enums. :) So it goes!

Just a small side note: I'm getting more and more fond of backing grid-like puzzles with non-grid backing implementations...until it comes time to make a `toString()`. If I was never doing visualizations, I wouldn't have to be mucking around with 2D arrays so much. That's not a complaint, though...just an observation. It does make me wonder if I'm saving myself anything by not using 2D arrays just from the get-go, but I think it's good practice regardless.

## Day 16: Reindeer Maze

[On day 22] I'm not even checking any code in for this yet. I did start working on it on day 16 but quickly ran into trouble, and eventually I just had to abandon ship and go to bed. At the time of this writing, it's been long enough that I don't even remember what I was stuck on, so when I do get back to this one, I might end up starting from scratch. We'll see.

[On day 28] Yeah...I'm coming back to this now, and I'm just going to start over.

[Later] I finished part 1 with what I think is some pretty straightforward pathfinding that I think I may have made unnecessarily complicated with some suboptimal object design, but so it goes. Looking at part 2...I'm thinking initially it only needs minor modification to work, so...we'll see how that goes. Even so...I'm strongly considering making part 2 code completely separate from part 1 just so part 1's performance (which is already shaky) won't necessarily get even worse by depending on part 2 functionality. [After some thought.] Actually, no...might was well just stick with what I've got. [After some more thought.] Know what? Fuck it. I wasn't that happy with the original implementation of part 1, so I'm just going to refit the whole thing, now with part 2 in mind.

[On day 29] It turned out that I fucked around so much with the refit that I had to go to bed before I was done debugging the part 2 functionality. I got it working when I got up the morning, though, so...it's almost like I finished it last night. :) During debugging, I broke a lot of stuff out into separate methods that didn't really need to be in separate methods _except_ for debugging (especially debugging output), so...the method structure is all messed up, but eh.

**Spoilers for day 16**

Part 1 was, I think, supposed to be a fairly straightforward pathfinding operation. It's possible I could have squeezed out an ounce more of performance with something like A\*, but I tend to stick with good ol' Dijkstra, so that's what I did this time, too. Turned out to be for the best, I think, at least for me, because I don't know enough about adapting A\* to find multiple paths.

Anyway, between parts 1 and 2, in theory the only big change I needed to make was storing the actual routes of the paths instead of only storing the distances. I think that seemed fine on its own. But the refit I had in mind changed the whole means by which I was collecting pathfinding information. I was shy about making it a part of the object state, but in retrospect, I'm not really sure why. In any case, that took some amount of doing, and then the collecting of the route information (which should have been the easy part!) actually seemed to give me the most trouble toward the end. I had introduced some subtle error that didn't show up in the test data but was fouling my live data run. So that's always fun. But, anyway...this _should_ have been pretty straightforward Dijkstra's stuff, but I just couldn't seem to get it together for this one. On the upside, I'm leaning a lot more into Java Streams now and having fun with that.

**End spoilers**

## Day 17: Chronospatial Computer

[On day 25] I love an emulator day! I had fun doing part 1. Now seeing part 2...I'm less enthused about an emulator day. I'll try brute force first and see how it goes.

[Later] Mmm...brute force looks to be untenable. I'm doing a commit, though, just to commemorate the attempt. Plus it includes some refactoring of the part 1 code. Not that I'm going to need it; I'll have to approach part 2 way differently, I think.

[On day 26] Ugh...I spent a LOT of time trying to figure out how I could tackle part 2, and in the end, I basically just cheesed it. The spoilery stuff on my thought process is all in the code comments, so I'll spare it in the README, except to say that I _know_ for sure it's not a general solution, but I _think_ it would work for other input? It basically depends on how similarly other people's puzzle input programs work and if they operate similarly to mine. But in the end, it's just a smarter version of brute force. Still, I basically had to figure out how my input program worked before I could even start to write the solution. So...I don't love it, not by a long way, but it got the job done.

## Day 18: RAM Run

[On day 27] Part 1 went relatively well; I tried out a small change to a familiar technique basically just for the exercise of it, and it seems fine. Going into part 2, I'm _pretty_ sure I can get away with minimal adjustments to part 1 to get a solution. We shall see!

Success! Though, granted, it took longer to run than I expected, so...there may be a better way. Also, I am aware that my solution is not completely 100% rigorous...more in spoilers.

**Spoilers for Day 18**

The familiar technique I referenced for part 1 was Djikstra's Algorithm, which I have implemented many times for AoC, but the new thing I tried out was using it with a priority queue instead of just a regular set for the unseen nodes. Took a bit of extra code, but they tell me it performs better, so... At this scale, I can't really tell the difference, but I'm glad to have that in my toolbox.

For part 2, it's not 100% rigorous in the sense that...in the rare case where the shortest path to the exit takes `Integer.MAX_VALUE` steps, I won't be able to tell that apart from not being able to reach the end. So...there's that, but I'll live with it.

**End spoilers**

## Day 19: Linen Layout

[On day 27] I went into part 1 semi-confident that I had an idea that would work but that might be time-consuming. The first run bore that suspicion out, but after adding a bit of caching, it went much better. Going into part 2, I'm confident I can use basically the same algorithm, **but** in order to keep the code tight, I'll have to rewrite it a bit to support part 2, and then part 1 will just take advantage of part 2 and read the results differently. Sadly, I can't jump straight into it, and I'll have to come back to part 2 later.

[Later] I have somehow completely bungled part 2 when trying to adapt it to be usable by part 1. I'm rolling back to the part 1 commit, and I'm just going to start part 2 over from scratch.

WELP when I rolled things back I committed some kind of git sin and lost a bunch of stuff that I'd been sitting on for other puzzles but hadn't committed yet. It's fine, there's no big loss. None of that stuff was working anyway. :) But also now I gotta stop work again, so I'll come back to part 2 later AGAIN.

[On day 28] Welp. Kind of frustrated with myself on this one. I took like the whole rest of yesterday coming up with some big complicated algorithm to deal with part 2, and then I implemented it this morning, and it super didn't work, so I started over again. This would be my third attempt at part 2. The third attempt was _very_ simple and also may even run faster than my original part 1, so it makes me think once again I'd be better off junking part 1 and just having it use the part 2 code, but I can't be bothered at this point. Onward!

**Spoilers (mild) for Day 19 part 2 and some extra pondering**

Once I implemented my third attempt at part 2, the only stumbling block was that it overflowed int, so I redid the thing with BigInteger instead. I'm once again wondering if it's worth just using BigInteger all the time, which would take more time to code but which would mean I'd never have to worry about overflow (especially if it happens in a way that's hard to spot...that's my real nightmare). Or is it worth taking the half-step of long? I wish I had a better way to estimate what the outputs would be like beforehand. Which...I mean, I could maybe code something for each puzzle to try and see, but at that point, it's probably faster to just use BigInteger from the start. Eh, minor problems, but still...it's on my mind.

**End spoilers**

## Day 20: Race Condition

[On day 28] I felt pretty good about my initial design going into part 1, and I vaguely guessed what was going to happen in part 2. Even so, I proceeded with part 1 not trying to anticipate part 2, because that always gets me into trouble. The hope, though, is that part 2 won't take *too* much tweaking.

[Later] I have an (ostensibly working) part 2 for which I did minimal tweaking, but it's taking a good, long while to execute. I may need to look into some optimization.

[A bit later] Ehhhh...it did take a noticeably long time, but it finished before I even started to look at optimizations, so I'm just going to leave it. This part 2 runs slow!

## Day 21: Keypad Conundrum

[On day 22] I started work on this on day 21 but bungled it somehow. As with Day 16, by the time I end up getting back to this, I might start over.

[On day 29] Yeah...I think I'm largely starting over with this. I'll keep some of the basic object structure from before if it still makes sense, but I'm pretty much rewriting all the guts.

[Some time later] Okay, I'm just scrapping this whole thing and starting over.

[A lot of time later] This is surely the most cockamamie thing I have ever tried to cobble together so far for this year's AoC, and it keeps blowing out my memory heap. I am clearly doing something very wrong. The really sad part is that it works for three nested controllers, but not the four I need for the puzzle. I'm out of time for the day, and I don't know when I can get back to this, so I'm just kind of leaving a note for myself. This is a bummer!

[On day 30] As tends to go, I had inspiration last night after I had shut everything down about how to fix this. I don't have time to work on it at the time of this writing, but I am committing the work I've done so far, even though I'm going to largely rewrite everything. One, it's bordering on unhinged, and I want to save it. :) Two...who knows, it might even come in handy later! I haven't seen part 2 yet, so maybe I'll get to make use of something unhinged after all. It's not a terrible commit. At least it's better than the version I was bughunting before where it turned out my numeric keypad didn't have a "1 2 3" row. :D

[A little later] It occurs to me that maybe I *can* salvage what I already wrote and just make it do less with a flag or something. Maybe I don't need a whole rewrite.

[A little later] GOD DAMN IT the algorithm wasn't the problem! I mean...it was, and still is, but it's not that the puzzle took up too much RAM, it's that I **added an extra keypad to the chain**. So...with the correct number of robots (and therefore keypads), my solution actually executes and looks like it's producing a result in the right neighborhood, but it's still wrong. It looks like whatever I was cooking up in there isn't passing test data. So *once again*, I am going to commit for posterity and then basically start over. I could halfway justify trying to salvage the unhinged implementation if it meant I could just shortcut the performance problems and get the right answer. But...I shortcut the performance problems and got the _wrong_ answer. Between that and the knowledge that the thing is already wildly overengineered for the puzzle ask, I might as well start from scratch. Sadly, I can't do it as I'm writing this, but I hope to give it another shot in the afternoon.

[Much later] I spent the time I could have spent on development today on taking notes, instead. The Day 21 solution is nowhere near functional at this point, but there are a *ton* of notes in there that should guide my future development (which isn't going to be tonight). Like a genius, I buried much of it inside the `KeypadRobot` source, but I'll move it before the real part 1 commit.

[Day 32] I've reached the point where it's _sort of_ working (finally), by which I mean it passes the test input but not the real input. That means my whole theory is flawed, so it's once again either try and revamp what I've got or go back to the drawing board. I'm gonna be real...I encountered a lot of issues during development that tempts me to redesign the whole thing again, but I'll give it a little bit before trying again and see how I feel. All the same, I'm doing another commit, because this is the closest I've gotten so far, and I guess also I'd like to checkpoint my notes again. The urge to start over again is strong, but we'll see.

[Day 33] Well, I rewrote the whole thing. Spent most of the day on it, in fact. I got it back to the point where it was passing the test data but not the real data...again! And then I made some changes, and now I broke it...just enough that it still runs, but now it doesn't even pass the test data. I'm most likely headed for another rewrite, but definitely headed for a break. Oh...and I moved all the commentary to the `Day21Solver` class where it "belongs." But anyway...I really need to get back to basics and figure out what I'm doing wrong. This is for sure an algorithm problem; I've rewritten the damn thing like three times based on the same core idea, so...I doubt it's bugs. It's just...flawed somehow. Doing another milestone commit, but I'm sure all this code will be gone by next time.

[Day 34] After some contemplation last night, I realized that my current implementation isn't even fulfilling the contract I set out for myself, but I also realized I'm basically going down the same road as the first implementation, so that's probably not going to get me anywhere. So I'm going to take _one more shot_ at trying out the current plan. For a fresh take and also as a technical exercise, I'm going to make a significant change in architecture (with more commentary on that in the code), but the overall shape of the algorithm will be the same, I think. Either way, I'm just taking some notes now to remind myself where I'm at. I don't think I'll necessarily have a chance to do any coding until much later today.

[Much later that day] That was a bust. I mean, I had a great time playing with stuff...made my own command-generating Iterator and all kindsa fun stuff. But in the end, it's not getting any better results, and it is way WAY more complicated. I think I'm just not approaching this correctly at all. I may try and take a break from this puzzle and try for some other starts I haven't gotten yet. But I think I need a whole new approach here.

[Day 36] Okay. I rewrote this thing (yet again), tried to make it as uncomplicated as I could, and I'm back to the point of passing the test data but failing the live data. It *has* to be a problem with my algorithm. Or, more specifically, my assumptions. This needs more thought.

[Day 42] HOLY GOOD CHRIST IT FINALLY WORKS. Doing a commit before anything else happens. I do NOT know what I did differently this time...as far as I know, this is just another implementation of the same algorithm I've been trying to implement, like, five or six times now. Sadly, I am on my way out the door, and part 2 looks like...I should be able to use the same setup as part 1? But I don't have time to try it right now. I guess later today I'll find out if my caching is working right or what.

[Later] Honestly, it went better than I thought it would, performance-wise, so I guess the caching was fine. Of course, after the first run, I had to rejigger everything to use BigInteger, because I saw an int value overflow in the output. I guess I should have anticipated that; when will I learn. Still...I am SO happy to see this one behind me.

## Day 22: Monkey Market

I wasn't even going to start a new puzzle today, but the description for part 1 seemed so straightforward that I assumed either part 2 would be way complicated, or else I was fooling myself and part 1 would take forever with a naive implementation. I rolled the dice, though, and got part 1 relatively quickly. Then, of course, part 2 is *way* more complicated, so I'm not going to try that tonight. Until next time, monkeys!

[Day 35] After banging my head against Day 21 for...quite some time, I decided to take a break and work on a different puzzle. In looking at Day 22 again, I realized a totally different way I could have done part 1 (more in spoilers), so I think I may just rewrite that for the exercise of it, and to get my head back in the Day 22 space. I've read part 2 and think I know the approach I want to take, and thankfully I don't think it matters what the implementation of part 1 is, so...it'll be fine. However! I'm also thinking of restructuring the model to be more of a...mmm...puzzle-specfic solution, rather than something theoretically more general-use. I don't think there's a technical reason for this, really, but it'll help me get in a better headspace. I think a lot of why I keep getting off track in Day 21 is because I'm trying to do things for general use rather than just trying to solve a specific problem.

[Later] Part 1 rewrite worked out well! On to part 2.

[Later] The theory was sound for how to approach part 2, but I was tripping myself with what turned out to be an implementation bug, which was a bummer because I didn't have a good way to debug it. Like...it was just a whole lot of data to sift through, so I wasn't sure how to narrow down where the problem was. Anyway...got there eventually! Not without one round of "Oh, *that's* why it's so slow," but the slowness was just my own naivete and not related to the bug I had coded. In any case. It's fine now. Onward! The decision at this point is whether to try to finish part 2 of 24 or to go back to banging my head against 21. Either way, I think I'm done for today.

**Spoilers for Day 22 part 1**

In my first run at part 1 (which was successful), I pretty much just implemented the problem as described in the puzzle using math methods. It occurred to me as I was pondering part 2 (on day 35), that it might be cool to do it only with bitwise operations.

**End spoilers**

## Day 23: LAN Party

I thought I could dash off part 1 real quick over lunch...but it turned out I misunderstood the assignment. Just as well...what I wrote was very quick and very ugly, and it didn't work, to boot. I'm not even committing it. I'll just be coming back to this one later when I have some time, but I'll have to start over.

[Later] I rewrote part 1, now understanding what was being asked. Seems to work well! Sadly, it doesn't seem like what I wrote is going to help me with part 2, despite me trying to make it more general use than what they were asking! I'll need a new approach to tackle part 2.

[Later still] Yeah...my logic for part 2 is totally different from part 1, but both seem to work well, so I'm taking the W!

## Day 24: Crossed Wires

I had a pretty good time implementing part 1, due in no small part to thinking I was doing something "clever." Now having done part 1 and seen the ask for part 2, I think it's likely I'll have to throw away everything I did for part 1. I'm not going to try it out now, though; it's late and I have to sleep. I'll think on it, though...I'll try to use this extra mulling time to see if I an approach part 2 with the architecture I set up for part 1...or if I'll just have to start the whole thing over.

[Day 35] Yep. I'm looking at this puzzle again, and I think I should just start the thing over. My part 1 is so unhelpful for part 2 that I'd have to to write part 2 from scratch anyway, so I might as well redo part 1. Do I'm doing a checkpoint commit so I can find it later if I want to, but I'm not going to be doing any coding for this today.

**Day 24 part 1 spoilers**

For whatever reason, it seemed to make sense to me to have the signal information work backwards...that it would get "pulled" from the output wires instead of "pushed" by the initial wire values. Since the gates have to "wait" for their signals to come in, it felt like it made sense for me to pull on the dependency chain from the output side. Like...it seemed like it would make it easier to detect errors?

[Later] Now that I've completed part 1 and seen part 2, I think I've really hosed myself. By "easier to detect errors" before, I meant easier to detect if I wasn't wiring the board internals correctly based on what the puzzle input was saying. Now that I've seen part 2, "easier to detect errors" is laughably wrong; I can't detect shit with this implementation, I'm pretty sure. I thought I was so funny not keeping any gate references. Who's laughing now?! Not me.

**End spoilers**

[Day 43] Having finally gotten past Day 21, I'm now coming back to this part 2, now my penultimate star. Contrary to my previous instinct, I don't think I have to *completely* rewrite everything, but I'm definitely going to do some restructuring of the part 1 model to better support part 2. At the time of this writing, I have a vague notion of how I want to try to tackle part 2. There's a lot of specifics I haven't worked out yet, but I have an idea of what I want the object model to be able to support, so I'm going to start there.

[Later] I'm checking in a commit for the refit of part 1. I've changed the architecture so that Wires no longer keep their own state, so the inputs and outputs only live as long as a Board method call. I also moved the parsing into the solver object. I don't know why I don't do that more. I mean...at first, it was part of my desire to be able to process data as it comes in, as opposed to parsing all the input and holding it all in memory when I didn't have to. That was more an academic exercise than a practical one, but it just became a habit, and I don't think it was necessary (or even useful) in many cases. In this case, though, there's definitely no gain for processing the input as it comes in, and it helps me keep the Board object clearer of junk, so...yeah. Anyway...I retested part 1 with the new architecture, and it's fine, so I'm moving on to part 2.

[A bit later] Slightly reworked part 1. BoardInput is now BoardSignals to be of more general use, and now it also serves as a signal cache during evaluation, which I think will come in handy later.

[Much later] Well...I'm thinking now I should actually do a full rewrite. I'm still happy with the changes I made for part 1, but embarrassingly, one of the things I "streamlined" out would, I think now, be helpful for part 2. So, all told, I think I just need a total redesign for part 2. Not sure if that's going to be today, though.

[Day 44] I've started the rewrite and gotten it back to doing part 1, so I'm checking that part in. I know `BoardSignals` seems way overboard for this right now, and there's a ton left over in there from the previous run, but there may be some things in there I can salvage for part 2, so I'm leaving it for now. `Board`, `Gate`, and `Wire` have been rewritten to various degrees. `Gate` is totally different now, and `Wire` looks a lot more like its original implementation than the most recent one.

**Day 24 part 1 spoilers (again)**

The big reversal this time from the original implementation is that signals now travel in the more traditional direction (from input to output), as opposed to the reverse direction I did for the original. The similarity to the original is that the signal states are once again stored in the `Wire`s (and therefore the `Board`), as opposed to breaking the state out into the `BoardSignals` object like I did for the previous implementation.

**End spoilers**

## Day 25: Code Chronicle

[On day 28] I really phoned it in on this one. :) The solution is not rigorous...it makes lots of assumptions about the input and probably some other stuff, so it's not as generally applicable as I would normally like, but here we are. At the time of this writing, I'm 7 stars away from completion.
