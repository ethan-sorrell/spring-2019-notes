"""
Usage: python3 main.py [binary/count] queryset.txt dataset.txt
"""
import sys
import re
from collections import Counter

#rtype = sys.argv[1]
#query = open(sys.argv[2], "r")
#corpus = open(sys.argv[3], "r")
rtype = 'count'
query = open('queries.txt', 'r')
corpus = open('dataset.txt', 'r')


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
    returns a list, assignments, such that
    assignments[i] -> label of nearest centroid to lds[i]
    """
    centroids = [lds[i] for i in range(k)]
    old_centroids = [dict() for i in range(k)]
    assignments = [0] * len(lds)
    while old_centroids != centroids:
        for i in range(len(lds)): # for each ds
            assignments[i] = find_nearest_centroid(lds, i, centroids) # find its class
        old_centroids = centroids
        centroids = new_centroids(lds, assignments, k)
    return assignments


def new_centroids(lds, assignments, k):
    """
    assignments[i] -> label of lds[i]
    go through each class and find all ds that belong to that class
    then find the centroid of them all
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
    index defines ds, lds[index], we are interested in
    centroids is a list of indexes into lds
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


doc_lds = file_to_lds(corpus)
query_lds = file_to_lds(query)
corpus = open('dataset.txt', 'r')
doc_list = file_to_list(corpus)



#for i, query in enumerate(query_lds):
#    result = query_all(rtype, query, doc_lds)
#    top = sorted(result, key=lambda x: x[0], reverse=True)[:5]
#    print('Top 5 results for query {}:'.format(i+1))
#    n = 1
#    for score, doc_num in top:
#        print('{}: Doc {} with score of {}'.format(n, doc_num, score))
#        print(doc_list[doc_num-1])
#        n += 1
