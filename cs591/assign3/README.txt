Usage: python3 main.py [first/second] input_file

Design considerations:
Originally, I pre-computed the correlation matrix, and chose 20% of the overall user-base as test users.
However, since the matrix is sparse, these design considerations seemed flawed. Often only 1 or 2 of the
the selected test users would have seen the test movie, and sometimes this number would be 0. As a result,
I decided to instead select a test movie, then select 20% of the users that had seen the movie for a test set.
If this rounded down to 0, I would simply set the test size to 1.
If there was only one user that saw the movie, we just choose a new test movie.
Additionally, since there are so few test users it made more since to calculate the correlation matrix values on
the fly, since many of them would not be necessary. Finally, the paper discussed a per user average absolute deviation
value as a testing metric. I simply modified this to a per movie average absolute deviation, since I was only
selecting one test movie.

For my own collaborative filtering algorithm, I made the observation that the first collaborative filtering
algorithm makes use of the average score a user provides + a predicted offset. This predicted offset is weighted
based on the voting habits of the users. I considered however, that some movies may be more polarizing than others.
Thus, I created an algorithm which weights between this user-based predicted score, and a movie-based predicted score
(simply the average score for that movie) based upon how polarizing the movie seems to be. This weighting is based
on the standard deviation of scores for that movie, if only one user is in our training set, we can't calculate
standard deviation, so we simply fallback to the first algorithm (which is equivalent to weighting the movie-based
prediction with 0, and the user-based prediction with 1)
