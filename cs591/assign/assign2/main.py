"""
Usage:
To find class for each query:
python3 main.py num_centroids queryset.txt dataset.txt
To find class of each document:
python3 main.py num_centroids dataset.txt
"""
import sys
import re
from collections import Counter


def file_to_lds(f):
    """convert file into a list of ds:
    ds for every query/document
    ds is a diciontary composed of {word: count}
    note that provided query/document numbering is 1-indexed
    while python is 0-indexed
    """
    line = f.readline()
    if line != "1\n":
        print("Format Error\n")
        return []
    i = 2
    line = f.readline()
    temp = []
    lds = []
    while line != "":
        if line == "{}\n".format(i): # new query/document
            i += 1
            lds.append(dict(Counter(temp)))
            temp = []
        else: # part of existing query/document
            temp = temp + line.rstrip('\n.').split()
        line = f.readline()
        line = re.sub(r'[^\w\s]', '', line) # remove non-alphanumeric characters
    lds = [{key: value/norm(doc) # unit vector normalization
           for key, value in doc.items()}
          for doc in lds]

    return lds


def file_to_list(f):
    """
    returns a list of documents
    note that provided list is 1-indexed
    while python is 0-indexed
    """
    line = f.readline()
    if line != "1\n":
        print("Format Error\n")
        return []
    i = 2
    line = f.readline()
    temp = ""
    lds = []
    while line != "":
        if line == "{}\n".format(i): # new query/document
            i += 1
            lds.append(temp)
            temp = ""
        else: # part of existing query/document
            temp = temp + line
        line = f.readline()
    return lds


def norm(ds):
    """
    computes the norm or length of a vector
    in ds format
    using euclidean distance
    """
    return (sum(x**2 for x in ds.values()))**(1/2)


def k_means(lds, k):
    """
    returns a list of centroids
    each centroid is in ds format
    """
    centroids = [lds[i] for i in range(k)]
    old_centroids = [dict() for i in range(k)]
    assignments = [0] * len(lds)
    while old_centroids != centroids:
        for i in range(len(lds)): # for each ds
            assignments[i] = find_nearest_centroid(lds, i, centroids) # find its class
        old_centroids = centroids
        centroids = new_centroids(lds, assignments, k)
    return centroids


def new_centroids(lds, assignments, k):
    """
    returns:
    list of centroids in ds format

    args:
    assignments[i] -> label of lds[i]

    go through each class and find all ds that belong to that class
    then find the centroid of each class
    """
    centroids = [dict() for i in range(k)]
    group_sizes = [0 for i in range(k)]
    for i in range(len(lds)): # for each point
        ds = lds[i]
        centroid = centroids[assignments[i]]
        group_sizes[assignments[i]] += 1
        # add existing dims
        for dim in set(ds.keys()).intersection(set(centroid.keys())):
            centroid[dim] += ds[dim]
        # add new dims
        for dim in set(ds.keys()).difference(set(centroid.keys())):
            centroid[dim] = ds[dim]

    # divide each dim of centroid by its group_size
    for i in range(k):
        centroids[i] = {key: value/group_sizes[i] for key, value
                        in centroids[i].items()}
    return centroids


def find_nearest_centroid(lds, index, centroids):
    """
    returns:
    an index into centroids corresponding to the nearest centroid


    args:
    index used to find ds of interest by lds[index]
    centroids is a list of vectors in ds format
    """
    ds = lds[index]
    min_distance = -1
    ans = -1
    for centroid_index in range(len(centroids)):
        centroid = centroids[centroid_index]
        common_dims = set(ds.keys()).intersection(set(centroid.keys()))
        component1 = sum((ds[dim]-centroid[dim])**2 for dim in common_dims)
        component2 = sum(ds[dim]**2 for dim
                         in set(ds.keys()).difference(centroid.keys()))
        component3 = sum(centroid[dim]**2 for dim
                         in set(centroid.keys()).difference(ds.keys()))
        distance = component1 + component2 + component3
        if min_distance == -1 or min_distance > distance:
            min_distance = distance
            ans = centroid_index
    return ans


def find_classes(centroids, lds):
    """
    returns list, results, such that
    results[i] -> index of nearest centroid to lds[i]
    """
    results = [0 for i in range(len(lds))]
    for i in range(len(lds)):
        results[i] = find_nearest_centroid(lds, i, centroids)
    return results

if len(sys.argv) == 3:
    num_classes = int(sys.argv[1])
    corpus = open(sys.argv[0], "r")
    doc_lds = file_to_lds(corpus)
    corpus = open(sys.argv[0], "r")
    doc_list = file_to_list(corpus)

    doc_centroids = k_means(doc_lds, num_classes)
    doc_classes = find_classes(doc_centroids, doc_lds)
    # print documents in each class
    for i in range(num_classes):
        print('class {} contains documents:'.format(i))
        for j in range(len(doc_lds)):
            if doc_classes[j] == i:
                print(j, end=' ')

if len(sys.argv) == 4:
    num_classes = int(sys.argv[1])
    query = open(sys.argv[2], 'r')
    corpus = open(sys.argv[3], 'r')
    doc_lds = file_to_lds(corpus)
    query_lds = file_to_lds(query)
    corpus = open(sys.argv[3], 'r')
    doc_list = file_to_list(corpus)

    doc_centroids = k_means(doc_lds, num_classes)
    doc_classes = find_classes(doc_centroids, doc_lds)
    # print documents in each class
    for i in range(num_classes):
        print('class i contains documents:')
        for j in range(len(doc_lds)):
            if doc_classes[j] == i:
                print(j, end=' ')

    print()
    # print rocchio assignment of each query
    for i, query in enumerate(query_lds):
        # remember i is 0-indexed
        # while our dataset is 1-indexed
        centroid_index = find_nearest_centroid(query_lds, i, doc_centroids)
        print('class of query {}: {}'.format(i+1, centroid_index))
