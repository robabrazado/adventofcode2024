# Advent of Code 2024

This is my repo for the [Advent of Code](https://adventofcode.com/) 2024. I'm expecting a busier December this year than I'm used to, so instead of trying out another new language, I'm heading back to my old friend Java for this year. I'm also returning to [Eclipse](https://eclipseide.org/) for my IDE, but new this year is fiddling with [EGit](https://eclipse.dev/egit/) to simplify my pipeline. [Update: EGit so far seems quirky in some way that I don't understand that does **not** appear to be simplifying my pipeline. ;) ]

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

**Spoilers for part 2**

My initial attempt at implementing the Problem Dampener was to build it right into the Report object. Basically, as I was processing for "safeness" in the constructor, I gave the processing a free do-over the first time it encountered a fault, so that only the second fault would mark the Report as unsafe. It passed the test data but not the full input, and after staring at it for a while, I came to realize that what I implemented would never catch the fault as being caused by the first level being the "bad" one. So my *first* attempt at a cheap hack was actually that the Day02Solver would, after getting an "unsafe" Report with the dampener toggle on, then try making a non-dampened report with the same inputs minus the first level. That at least produced a better result than my first attempt, but it was still incorrect. So from there I just went to the crappy brute force you see before you (at the time of this writing). Obviously there were further edge cases that even my cheap hack of my first attempt were still missing, so I went to the brute force solution so I could log a correct answer before I went to bed. I'm convinced there's a better way, though.

**End spoilers**
