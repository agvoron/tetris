A not-so-subtle bug

Suddenly, the time it took for a new tetromino to spawn became dramatically longer. There was a substantial, painful lag.
The FPS logger wasn't sounding the alarm. I checked the frame delay manually and there was none.
I was baffled. I had not added any processing-heavy code that could lag the render loop.

I had changed the way new tetrominos were spawning, to come from the list of upcoming pieces.
The fall() method in the Tetromino class used to have side effects that correctly updated the Tetris board...
... but in the process of the change, I moved those out of the method, bringing it in line with the other tetromino movement methods like 'rotate'.
I edited the code in the place where fall() was called due to gravity.
It turns out I just forgot that the fall() method was also called on user input.
Namely, when space is pressed.
What was happening, is that when I pressed space, the piece would fall - but the board would not be updated until the gravity timer filled up again, calling fall() in the other place.
Essentially, the piece appeared to fall, but was still active and movable for one more gravity interval.

As simple as it turned out to be, I spent a long time trying to wrap my head around it.
I checked out the previous commit and made the same changes again, line by line, testing after to each to see which introduced the delay.
The process only confused me further. I was completely stumped until finally realizing my oversight.

A lesson in object interfaces, side effects, and debugging approach... it's a good idea to think about all the places a method is used when debugging it.
I had tunnel vision and was looking only at the code lines I had changed, and think of the effect they had.
That's why it seemed to me that the code was 'haunted'.

...