import sys
import re
from collections import Counter

rtype = sys.argv[1]
query = open(sys.argv[2], "r")
corpus = open(sys.argv[3], "r")

def f_to_ds(f):
    """convert file into a list of lists of words
    note that in-file numbering is 1-indexed while python is 0-indexed
    """
    line = f.readline()
    if line != "1\n":
        print("Format Error\n")
        return []
    i = 2
    line = f.readline()
    temp = []
    ds = []
    while line != "":
        if line == "{}\n".format(i): # new query/document
            i += 1
            ds.append(dict(Counter(temp)))
            temp = []
        else: # part of existing query/document
            temp = temp + line.rstrip('\n.').split()
        line = f.readline()
        line = re.sub(r'[^\w\s]', '', line) # remove non-alphanumeric characters
    return ds


def run_query(rtype, query, doclist):
    if rtype == 'binary':
        return [(0, i+1) if len(query) * len(doc) == 0 else
                (len(set(query.keys()).intersection(set(doc.keys()))) /
                 (len(query) * len(doc)), i+1)
                for i, doc in enumerate(doclist)]
    elif rtype == 'count':
        i = 1
        result = []
        for doc in doclist:
            if len(query) == 0 or len(doc) == 0:
                result.append((0, i))
                i += 1
            else:
                n = sum(query[key] * doc[key]
                        for key
                        in set(query.keys()).intersection(set(doc.keys())))
                result.append((n / (len(query) * len(doc)), i))
                i += 1
        return result


doc_list = f_to_ds(corpus)
query_list = f_to_ds(query)

for i, query in enumerate(query_list):
    result = run_query(rtype, query, doc_list)
    top = sorted(result, key=lambda x: x[0], reverse=True)[:5]
    print('Top 5 results for query {}:'.format(i+1))
    n = 1
    for score, num in top:
        print('{}: Doc {} with score of {}'.format(n, num, score))
        n += 1
    # remember that python is 0-indexed
