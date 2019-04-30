import sys
import random
import math
from statistics import stdev

def mean(_list):
    new_list = [item for item in _list if item != 0]
    return sum(new_list) / len(new_list)

def sd(_list):
    new_list = [item for item in _list if item != 0]
    return stdev(new_list)

def sqrt(num):
    return num ** 1/2

def correlation_weight(data_matrix, index1, index2):
    """Find the correlation weight between user with index1 and
       user with index2 in data_matrix
    """
    user1 = data_matrix[index1]
    user2 = data_matrix[index2]
    assert(len(user1) == len(user2))
    num_movies = len(user1)
    numer = sum([(user1[i]-mean(user1))*(user2[i]-mean(user2))
                     if user1[i] != 0 and user2[i] != 0
                     else 0
                     for i in range(num_movies)])
    denom1 = sqrt(sum([(user1[i]-mean(user1))**2
                      if user1[i] != 0 and user2[i] != 0
                      else 0
                      for i in range(num_movies)]))
    denom2 = sqrt(sum([(user2[i]-mean(user2))**2
                       if user1[i] != 0 and user2[i] != 0
                       else 0
                       for i in range(num_movies)]))
    denom = denom1*denom2
    return 0 if denom == 0 else numer/denom

def prediction(data_matrix, useri, moviei):
    """Find the predicted score for user with index useri and
       movie with index moviei using our first algorithm
    """
    user = data_matrix[useri]
    num_users = len(data_matrix)
    numer = sum([correlation_weight(data_matrix,useri,i)\
                 *(data_matrix[i][moviei] - mean(data_matrix[i]))
                 if data_matrix[i][moviei] != 0 else 0
                 for i in range(num_users)])
    denom = sum([correlation_weight(data_matrix,useri,i)
                 if data_matrix[i][moviei] != 0 else 0
                 for i in range(num_users)])
    return mean(user) + (numer/denom)

def prediction2(data_matrix, useri, moviei):
    """Find the predicted score for user with index useri and
       movie with index moviei using our second algorithm
    """
    p1 = prediction(data_matrix, useri, moviei)
    p2 = mean([user[moviei] for user in data_matrix])
    weight = sd([user[moviei] for user in data_matrix]) / 2.8
    return p1*(weight) + p2*(1-weight)



if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Error: expected 2 commandline arguments got {}".format(len(sys.argv)))
        print("Argument format: [first/second] input_file")
        sys.exit()
    if sys.argv[1] != "first" and sys.argv[1] != "second":
        print("Error: invalid commandline argument")
        print("Expected either first or second, got {}".format(sys.argv[1]))

    data_file = open(sys.argv[2])
    data_matrix = list()
    for line in data_file:
        if line[-1] == "\n":
            line = line[:-1]
        tmp = line.split("\t")
        tmp = [int(item) for item in tmp]
        data_matrix.append(tmp)

    num_users = len(data_matrix)
    num_movies = len(data_matrix[0])

    # now we need to select the movie to test on
    test_movie = random.randint(0, num_movies)
    print("Selected test movie {}".format(test_movie))
    # perform 80/20 split for training and testing
    num_watched = len([user for user in data_matrix if user[test_movie] != 0])
    while(num_watched < 1):
        print("Too few users have seen movie, selecting a new test movie")
        test_movie = random.randint(0, num_movies)
        print("Selected test movie {}".format(test_movie))
        num_watched = len([user for user in data_matrix if user[test_movie] != 0])
    print("{} users have seen the test movie".format(num_watched))
    test_size = math.floor(num_watched * 0.2)
    test_size = test_size if test_size != 0 else 1
    print("Selecting {} of these users for our test set".format(test_size))
    testable_users = list()
    for i in range(num_users):
        if data_matrix[i][test_movie] != 0:
            testable_users.append(i)
    test_split = random.randint(0, num_watched-test_size)
    test_users = [testable_users[i] for i in range(test_split,test_split+test_size)]
    test_matrix = [[data_matrix[i][j] for j in range(num_movies)]
                   for i in test_users]

    for i in range(test_split, test_split+test_size):
        data_matrix[i][test_movie] = 0

    if num_watched < 3 and sys.argv[1] == "second":
        print("Too few users to calculate weighting")
        print("Falling back to our first algorithm")
        print("(This is equivalent to placing all of the weight")
        print("on the user-based prediction)")
        sys.argv[1] == "first"

    print()
    sum_error = 0
    if sys.argv[1] == "first":
        print("Making predictions using our first algorithm")
        for i in range(test_split, test_split+test_size):
            tmp_pred = prediction(data_matrix, i, test_movie)
            tmp_true = test_matrix[i-test_split][test_movie]
            print("For user {}: prediction={:.2f} actual={}".format(i, tmp_pred, tmp_true))
            sum_error += abs(tmp_pred - tmp_true)
        print("Average absolute deviation={:.2f}".format(sum_error/test_size))
    elif sys.argv[1] == "second":
        print("Making predictions using our second algorithm")
        for i in range(test_split, test_split+test_size):
            tmp_pred = prediction2(data_matrix, i, test_movie)
            tmp_true = test_matrix[i-test_split][test_movie]
            print("For user {}: prediction={:.2f} actual={}".format(i, tmp_pred, tmp_true))
            sum_error += abs(tmp_pred - tmp_true)
        print("Average absolute deviation={:.2f}".format(sum_error/test_size))
