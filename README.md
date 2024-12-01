# Advent of Code 2024

This is my repo for the [Advent of Code](https://adventofcode.com/) 2024. I'm expecting a busier December this year than I'm used to, so instead of trying out another new language, I'm heading back to my old friend Java for this year. I'm also once again returning to [Eclipse](https://eclipseide.org/) this year and fiddling with [EGit](https://eclipse.dev/egit/) to simplify my pipeline.

As before, this README serves as kind of blog and running commentary that I'll be updating as the month progresses.

Shoutouts this year to (1) once again, the fun folks at the [Indie Game Reading Club](https://www.indiegamereadingclub.com/) for their company again and (2) my friend [@codeforkjeff](https://github.com/codeforkjeff) for encouraging me to shake off the coding dust and get back in it.

# What to do with this code
The current state of affairs (as of Day 1):

Basically, the src folder should be able to be dropped anywhere and compiled with any modern Java compiler. There's nothing too complicated going on here. What I'm **not** doing is checking in any puzzle input. To try out your own input, drop a `resources` directory in your classpath, a `puzzle-input` directory inside that, and then a file with the name format `dayNN-input.txt` where `NN` is a left-zero-padded day number. For "test" input (optionally callable instead of "real" input with a command line argument, instead use the file name format `dayNN-input-test.txt`.

The main entry point is com.robabrazado.aoc2024.Aoc2024Cli, and it takes 2 or 3 command line arguments:

`java com.robabrazado.aoc2024.Aoc2024Cli <day> <part> [test]`

Where `day` is the number of the day to solve, `part` is `1` or `2`, and the optional `test` argument makes it use test data input or not as described above.


---

## Day 1

I wrote the main implementation for this right after the puzzle dropped, but then spent a little time trying to get some common infrastructure in place, and then I did a little refactoring in the solution to take advantage. Other than the infrastructure stuff, which was me making things more complicated for myself, the actual solution implementation was pretty straightforward. One thing I'm trying to do more of this year is process puzzle input in a more "as it comes in from the stream" kind of way, rather than loading the whole input into memory and then parsing from there. I don't think there's significant impact for that change...it's just one of those things I decided to try. The affect on this day is that it (of course) made things more complicated for me since the processing-while-parsing operations were more complicated for part 2 than part 1. For the first implementation, I ended up just duplicating a bunch of code, but after the refactoring, I just basically made part 1 processing live with the extra work it has to do for part 2. So it goes.

I may try and tinker with the common-use stuff as the month goes on, but we'll see.
