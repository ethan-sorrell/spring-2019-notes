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


def run_query(rtype, query, doc):
    if len(query) * len(doc) == 0:
        return 0
    elif rtype == 'binary':
        return len(set(query.keys()).intersection(set(doc.keys())))
    elif rtype == 'count':
        return sum(query[key] * doc[key]
                   for key
                   in set(query.keys()).intersection(set(doc.keys()))) / \
                   (norm(query) * norm(doc))


def query_all(rtype, query, doclist):
    return [(run_query(rtype, query, doc), i+1)
            for i, doc in enumerate(doclist)]


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

num_classes = 9
corpus = open('dataset.txt', "r")
doc_lds = file_to_lds(corpus)
corpus = open('dataset.txt', "r")
doc_list = file_to_list(corpus)

doc_centroids = k_means(doc_lds, num_classes)
doc_classes = find_classes(doc_centroids, doc_lds)
# print documents in each class
for i in range(num_classes):
    print('********class {} contains documents:********'.format(i))
    print(doc_centroids[i])
    #for j in range(len(doc_lds)):
        #if doc_classes[j] == i:
            #print('#######Document {}#######'.format(j))
            #print(doc_list[j])
